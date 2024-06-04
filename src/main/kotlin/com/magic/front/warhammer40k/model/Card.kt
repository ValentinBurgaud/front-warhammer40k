package com.magic.front.warhammer40k.model

import com.custom.lib.toolbox.json.*
import com.magic.front.warhammer40k._uuid
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
    val power: Option<Int>,
    val toughness: Option<Int>,
    val multiverseId: Option<String>,
    val legalities: List<Legality>,
    val race: Option<String>
) {

    data class Builder(
        var id: UUID = UUID.randomUUID(),
        var name: String = "",
        var manaCost: String = "",
        var cmc: BigDecimal = BigDecimal.ONE,
        var color: List<String> = emptyList(),
        var colorIdentity: List<String> = emptyList(),
        var type: String = "",
        var types: List<String> = emptyList(),
        var subtypes: List<String> = emptyList(),
        var rarity: String = "",
        var set: String = "",
        var setName: String = "",
        var text: String = "",
        var flavor: Option<String> = Option.none(),
        var artist: String = "",
        var number: String = "",
        var power: Option<Int> = Option.none(),
        var toughness: Option<Int> = Option.none(),
        var multiverseId: Option<String> = Option.none(),
        var legalities: List<Legality> = emptyList(),
        var race: Option<String> = Option.none()
    ) {
        fun id(id: UUID) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun manaCost(manaCost: String) = apply { this.manaCost = manaCost }
        fun cmc(id: BigDecimal) = apply { this.cmc = cmc }
        fun color(color: List<String>) = apply { this.color = color }
        fun colorIdentity(colorIdentity: List<String>) = apply { this.colorIdentity = colorIdentity }
        fun type(type: String) = apply { this.type = type }
        fun types(types: List<String>) = apply { this.types = types }
        fun subtypes(id: List<String>) = apply { this.subtypes = subtypes }
        fun rarity(rarity: String) = apply { this.rarity = rarity }
        fun set(set: String) = apply { this.set = set }
        fun setName(setName: String) = apply { this.setName = setName }
        fun text(text: String) = apply { this.text = text }
        fun flavor(flavor: Option<String>) = apply { this.flavor = flavor }
        fun artist(artist: String) = apply { this.artist = artist }
        fun number(number: String) = apply { this.number = number }
        fun power(power: Option<Int>) = apply { this.power = power }
        fun toughness(toughness: Option<Int>) = apply { this.toughness = toughness }
        fun multiverseId(multiverseId: Option<String>) = apply { this.multiverseId = multiverseId }
        fun legalities(legalities: List<Legality>) = apply { this.legalities = legalities }
        fun race(race: Option<String>) = apply { this.race = race }
        fun build() = Card(id, name, manaCost, cmc, color, colorIdentity, type, types, subtypes, rarity, set, setName, text, flavor, artist, number, power, toughness, multiverseId, legalities, race)
    }
    companion object {

        val format: JsonFormat<Card> = JsonFormat.of(
            _string("name") { name -> Builder().name(name) }
                .and(_string("manaCost")) { b, manaCost -> b.manaCost(manaCost) }
                .and(_field("cmc", _bigDecimal(10))) { b, cmc -> b.cmc(cmc) }
                .and(_list("color", _string())) { b, color -> b.color(color.asJava()) }
                .and(_list("colorIdentity", _string())) { b, colorIdentity -> b.colorIdentity(colorIdentity.asJava()) }
                .and(_string("type")) { b, type -> b.type(type) }
                .and(_list("types", _string())) { b, types -> b.types(types.asJava()) }
                .and(_list("subtypes", _string())) { b, subtypes -> b.subtypes(subtypes.asJava()) }
                .and(_string("rarity")) { b, rarity -> b.rarity(rarity) }
                .and(_string("set")) { b, set -> b.set(set) }
                .and(_string("setName")) { b, setName -> b.setName(setName) }
                .and(_string("text")) { b, text -> b.text(text) }
                .and(_opt("flavor", _string())) { b, flavor -> b.flavor(flavor) }
                .and(_string("artist")) { b, artist -> b.artist(artist) }
                .and(_string("number")) { b, number -> b.number(number) }
                .and(_opt("power", _int())) { b, power -> b.power(power) }
                .and(_opt("toughness", _int())) { b, toughness -> b.toughness(toughness) }
                .and(_opt("multiverseId", _string())) { b, multiverseId -> b.multiverseId(multiverseId) }
                .and(_list("legalities", Legality.format.reader)) { b, legalities -> b.legalities(legalities.asJava()) }
                .and(_opt("race", _string())) { b, race -> b.race(race) }
                .map { it.build() }
        ) { card ->
            card.toJson()
        }

        val formatUpdate: JsonFormat<Card> = JsonFormat.of(
            _field("id", _uuid()) { id -> Builder().id(id) }
                .and(_string("name")) { b, name -> b.name(name) }
                .and(_string("manaCost")) { b, manaCost -> b.manaCost(manaCost) }
                .and(_field("cmc", _bigDecimal(10))) { b, cmc -> b.cmc(cmc) }
                .and(_list("color", _string())) { b, color -> b.color(color.asJava()) }
                .and(_list("colorIdentity", _string())) { b, colorIdentity -> b.colorIdentity(colorIdentity.asJava()) }
                .and(_string("type")) { b, type -> b.type(type) }
                .and(_list("types", _string())) { b, types -> b.types(types.asJava()) }
                .and(_list("subtypes", _string())) { b, subtypes -> b.subtypes(subtypes.asJava()) }
                .and(_string("rarity")) { b, rarity -> b.rarity(rarity) }
                .and(_string("set")) { b, set -> b.set(set) }
                .and(_string("setName")) { b, setName -> b.setName(setName) }
                .and(_string("text")) { b, text -> b.text(text) }
                .and(_opt("flavor", _string())) { b, flavor -> b.flavor(flavor) }
                .and(_string("artist")) { b, artist -> b.artist(artist) }
                .and(_string("number")) { b, number -> b.number(number) }
                .and(_opt("power", _int())) { b, power -> b.power(power) }
                .and(_opt("toughness", _int())) { b, toughness -> b.toughness(toughness) }
                .and(_opt("multiverseId", _string())) { b, multiverseId -> b.multiverseId(multiverseId) }
                .and(_list("legalities", Legality.format.reader)) { b, legalities -> b.legalities(legalities.asJava()) }
                .and(_opt("race", _string())) { b, race -> b.race(race) }
                .map { it.build() }
        ) { card ->
            card.toJson()
        }

        fun asRequest(card: Card): JsValue {
            return Json.obj(
                `$`("id", card.id.toString()),
                `$`("name", card.name),
                `$`("manaCost", card.manaCost),
                `$`("cmc", card.cmc),
                `$`("color", Json.array(card.color.toVavrList())),
                `$`("colorIdentity", Json.array(card.colorIdentity.toVavrList())),
                `$`("type", card.type),
                `$`("types", Json.array(card.types.toVavrList())),
                `$`("subtypes", Json.array(card.subtypes.toVavrList())),
                `$`("rarity", card.rarity),
                `$`("set", card.set),
                `$`("setName", card.setName),
                `$`("text", card.text),
                `$`("flavor", card.flavor),
                `$`("artist", card.artist),
                `$`("number", card.number),
                `$`("power", card.power),
                `$`("toughness", card.toughness),
                `$`("multiverseId", card.multiverseId),
                `$`("legalities", Json.array(card.legalities.toVavrList())),
                `$`("race", card.race)
            )
        }

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
                power = json.field("power").asOptString().map { it.toInt() },
                toughness = json.field("toughness").asOptString().map { it.toInt() },
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
                manaCost = row.getString("mana_cost"),
                cmc = row.getBigDecimal("cmc"),
                color = row.getArrayOfStrings("color").toList(),
                colorIdentity = row.getArrayOfStrings("color_identity").toList(),
                type = row.getString("type"),
                types = row.getArrayOfStrings("types").toList(),
                subtypes = emptyList(),
                rarity = row.getString("rarity"),
                set = row.getString("set"),
                setName = row.getString("set_name"),
                text = row.getString("text"),
                flavor = row.getString("flavor").option(),
                artist = row.getString("artist"),
                number = row.getInteger("number").toString(),
                power = row.getInteger("power").option(),
                toughness = row.getInteger("toughness").option(),
                multiverseId = row.getString("multiverse_id").option(),
                legalities = emptyList(),
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

    data class Builder(
        var format: String = "",
        var legality: String = "",
    ) {
        fun format(format: String) = apply { this.format = format }
        fun legality(legality: String) = apply { this.legality = legality }
        fun build() = Legality(format, legality)
    }
    companion object {

        val format: JsonFormat<Legality> = JsonFormat.of(
            _string("format") { format -> Builder().format(format) }
                .and(_string("legality")) { b, legality -> b.legality(legality) }
                .map { it.build() }
        ) { legality ->
            legality.toJson()
        }
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