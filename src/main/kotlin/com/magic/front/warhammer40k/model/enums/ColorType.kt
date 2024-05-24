package com.magic.front.warhammer40k.model.enums

import io.vavr.kotlin.toVavrList
import org.reactivecouchbase.json.JsValue
import org.reactivecouchbase.json.Json

enum class ColorType(val code: String, val label: String) {
    R("R", "Red"),
    W("W", "White"),
    B("B", "Black"),
    G("G", "Green"),
    U("U", "Blue");

    fun toJson(): JsValue {
        return Json.obj()
            .with("code", code)
            .with("label", label)
    }

    companion object {
        fun byCode(code: String): ColorType {
            return when (code) {
                "R" -> R
                "W" -> W
                "B" -> B
                "G" -> G
                "U" -> U
                else -> throw IllegalStateException("bad code $code")
            }
        }

        val byCode = { s: String -> ColorType.byCode(s.uppercase()) }

        val valuesToString = { -> ColorType.values().map { it.toString() }.toVavrList() }
    }
}