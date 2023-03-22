package com.magic.front.warhammer40k.model

import io.vavr.control.Option
import io.vavr.kotlin.option
import io.vavr.kotlin.toVavrList
import io.vertx.sqlclient.Row
import org.reactivecouchbase.json.JsArray
import org.reactivecouchbase.json.JsValue
import org.reactivecouchbase.json.Json
import org.reactivecouchbase.json.Syntax.`$`
import java.math.BigDecimal
import java.util.UUID

data class Card(
    val id: UUID,
    val name: String,
    val manaCost: String,
    val cmc: BigDecimal,
    val color: List<String>,
    val colorIdentity: List<String>,
    val type: String,
    val types: List<String>,
    val subtypes: List<String>,
    val rarity: String,
    val set: String,
    val setName: String,
    val text: String,
    val flavor: Option<String>,
    val artist: String,
    val number: String,
    val power: Option<String>,
    val toughness: Option<String>,
    val imageUrl: Option<String>,
    val multiverseId: Option<String>,
    val legalities: List<Legality>,
    val race: Option<String>
) {
    companion object {
        fun fromJsonMagicApi(json: JsValue): Card {
            return Card(
                id = UUID.fromString(json.field("id").asString()),
                name = json.string("name"),
                manaCost = json.string("manaCost"),
                cmc = json.field("cmc").asBigDecimal(),
                color = json.array("colors").toList().map { it.asString() },
                colorIdentity = json.array("colorIdentity").toList().map { it.asString() },
                type = json.string("type"),
                types = json.array("types").toList().map { it.asString() },
                subtypes = json.field("subTypes").asOptArray().map { it.toList().map { it.asString() } }.getOrElse(emptyList()),
                rarity = json.string("rarity"),
                set = json.string("set"),
                setName = json.string("setName"),
                text = json.string("text"),
                flavor = json.field("flavor").asOptString(),
                artist = json.string("artist"),
                number = json.string("number"),
                power = json.field("power").asOptString(),
                toughness = json.field("toughness").asOptString(),
                imageUrl = json.field("imageUrl").asOptString(),
                multiverseId = json.field("multiverseId").asOptString(),
                legalities = json.array("legalities").toList().map { Legality.fromJsonMagicApi(it) },
                race = json.field("race").asOptString()
            )
        }

        fun List<Card>.toJson(groupByEntityId: Boolean = false): JsArray {
            return if (groupByEntityId) {
                Json.array(
                    groupBy { cards -> cards.race.get() }
                        .entries
                        .map {
                            Json.obj(
                                `$`("cards", Json.array(it.value.map { card ->
                                    card.toJson()
                                }.toVavrList())),
                                `$`("entityId", it.key.trimStart('0')),
                            )
                        }.toVavrList()
                )
            } else {
                Json.array(this.map { card ->
                    card.toJson()
                }.toVavrList())
            }
        }

        fun fromBdd(row: Row): Card {
            return Card(
                id = row.getUUID("id"),
                name = row.getString("name"),
                manaCost = row.getString("manaCost"),
                cmc = row.getBigDecimal("cmc"),
                color = row.getArrayOfStrings("colors").toList(),
                colorIdentity = row.getArrayOfStrings("colorIdentity").toList(),
                type = row.getString("type"),
                types = row.getArrayOfStrings("types").toList(),
                subtypes = row.getArrayOfStrings("subTypes").toList(),
                rarity = row.getString("rarity"),
                set = row.getString("set"),
                setName = row.getString("setName"),
                text = row.getString("text"),
                flavor = row.getString("flavor").option(),
                artist = row.getString("artist"),
                number = row.getString("number"),
                power = row.getString("power").option(),
                toughness = row.getString("toughness").option(),
                imageUrl = row.getString("imageUrl").option(),
                multiverseId = row.getString("multiverseId").option(),
                legalities = row.getArrayOfStrings("legalities").toList().map { Legality.fromJsonMagicApi(Json.parse(it)) },
                race = row.getString("race").option()
            )
        }
    }

    fun toJson(): JsValue {
        return Json.obj(
            `$`("id", id.toString()),
            `$`("name", name),
            `$`("manaCost", manaCost),
            `$`("cmc", cmc),
            `$`("color", Json.array(color.toVavrList())),
            `$`("colorIdentity", Json.array(colorIdentity.toVavrList())),
            `$`("type", type),
            `$`("types", Json.array(types.toVavrList())),
            `$`("subtypes", Json.array(subtypes.toVavrList())),
            `$`("rarity", rarity),
            `$`("set", set),
            `$`("setName", setName),
            `$`("text", text),
            `$`("flavor", flavor),
            `$`("artist", artist),
            `$`("number", number),
            `$`("power", power),
            `$`("toughness", toughness),
            `$`("imageUrl", imageUrl),
            `$`("multiverseId", multiverseId),
            `$`("legalities", Json.array(legalities.toVavrList())),
            `$`("race", race),
        )
    }
}

data class Legality(
    val format: String,
    val legality: String
) {
    companion object {
        fun fromJsonMagicApi(json: JsValue): Legality {
            return Legality(
                format = json.string("format"),
                legality = json.string("legality")
            )
        }
    }

    fun toJson(): JsValue {
        return Json.obj(
            `$`("format", format),
            `$`("legality", legality)
        )
    }
}