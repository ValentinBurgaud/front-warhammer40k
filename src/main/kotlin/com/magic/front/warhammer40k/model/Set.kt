package com.magic.front.warhammer40k.model

import org.reactivecouchbase.json.JsValue
import java.time.LocalDate

data class Set (
    val code: String,
    val name: String,
    val type: String,
    val releaseDate: LocalDate,
    val onlineOnly: Boolean
) {
    companion object {
        fun fromJsonMagicApi(json: JsValue): Set {
            return Set(
                code = json.string("code"),
                name = json.string("name"),
                type = json.string("type"),
                releaseDate = LocalDate.parse(json.string("code")),
                onlineOnly = json.field("onlineOnly").asBoolean(),
            )
        }
    }
}