package com.magic.front.warhammer40k.parsers.patch

import com.custom.lib.toolbox.json.JsonFormat
import com.custom.lib.toolbox.json._enum
import com.custom.lib.toolbox.json._field
import com.custom.lib.toolbox.json._jsValue
import com.custom.lib.toolbox.json._list
import com.custom.lib.toolbox.json._opt
import com.custom.lib.toolbox.json._string
import com.custom.lib.toolbox.json.and
import com.custom.lib.toolbox.json.map
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import org.reactivecouchbase.json.JsValue
import org.reactivecouchbase.json.Json
import org.reactivecouchbase.json.Syntax.`$`

class Patch private constructor(
    val op: PatchOp,
    val path: String,
    val value: Option<JsValue>
) {
    data class Builder(
        var op: PatchOp = PatchOp.ADD,
        var path: String = "",
        var value: Option<JsValue> = Option.none()
    ) {
        fun op(op: PatchOp) = apply { this.op = op }
        fun path(path: String) = apply { this.path = path }
        fun value(value: Option<JsValue>) = apply { this.value = value }
        fun build() = Patch(op, path, value)
    }

    companion object {
        val format: JsonFormat<Patch> = JsonFormat.of(
            _field("op", _enum({ s -> PatchOp.byCode(s) }, { -> PatchOp.values().map { it.code }.toVavrList() })) { op -> Builder().op(op) }
                .and(_field("path", _string())) { b, path -> b.path(path) }
                .and(_opt("value", _jsValue())) { b, value -> b.value(value) }
                .map { it.build() }
        ) { patch ->
            Json.obj(
                `$`("op", patch.op.code),
                `$`("path", patch.path),
                `$`("value", patch.value)
            )
        }
    }
}

data class Patches(val operations: List<Patch>) {
    companion object {
        val format: JsonFormat<Patches> = JsonFormat.of(
            _list(null, Patch.format.reader) { operations -> Patches(operations.asJava()) }
        ) { patches -> Json.arr(patches.operations.map(Patch.format.writer).toVavrList()) }
    }
}