package com.magic.front.warhammer40k.repository

import com.custom.lib.toolbox.extensions.preparedReactiveQuery
import com.magic.front.warhammer40k.model.Card
import io.vavr.kotlin.toVavrList
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.*
import org.reactivecouchbase.json.Json
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class CardsRepository(private val jdbcClient: PgPool) {

    fun getCardById(cardId: String): Mono<List<Card>> {
        val query =
            """
                SELECT * FROM CARD
                WHERE id = $1
            """

        return jdbcClient.preparedReactiveQuery(query, Tuple.of(cardId)) { result ->
            result.map { row ->
                Card.fromBdd(row)
            }
        }.contextWrite { ctx ->
            ctx.put("sqlQuery", query)
            ctx.put("poolName", "PgPool")
        }
    }

    fun listCards(): Mono<List<Card>> {
        val query = """
                SELECT * FROM card
            """
        return jdbcClient.preparedReactiveQuery(query) { result ->
            result.map { row ->
                Card.fromBdd(row)
            }
        }
    }

    fun insertCard(card: Card): Mono<Unit> {
        val query = """
                INSERT INTO card(name, mana_cost, cmc, color, color_identity, type, types, subtypes, rarity, set, set_name, text, flavor, artist, number, power, toughness, image_url, multiverse_id, legalities, race) 
                VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21);
            """
        val tuple = Tuple.of(
            card.name,
            card.manaCost,
            card.cmc,
            //TODO Convertion problems
            arrayOf(Json.arr(card.color.map { it.uppercase() }.toVavrList()).stringify()),
            arrayOf(Json.arr(card.colorIdentity.map { it.uppercase() }.toVavrList()).stringify()),
            card.type,
            arrayOf(Json.arr(card.types.toVavrList()).stringify()),
            arrayOf(Json.arr(card.subtypes.toVavrList()).stringify()),
            card.rarity,
            card.set,
            card.setName,
            card.text,
            card.flavor.orNull,
            card.artist,
            card.number.toInt(),
            card.power.getOrElse(0),
            card.toughness.getOrElse(0),
            card.imageUrl.getOrElse("A ajouter plus tard"),
            card.multiverseId.orNull,
            card.legalities.firstOrNull(),
            card.race.getOrElse("")
        )
        return jdbcClient.preparedReactiveQuery(query, tuple) {}
    }

    fun deleteById(cardId: String): Mono<Unit> {
        val query = """
                DELETE FROM CARD WHERE WHERE id = ${'$'}1
            """
        return jdbcClient.preparedReactiveQuery(query, Tuple.of(cardId)) {}.contextWrite { ctx ->
            ctx.put("sqlQuery", query)
            ctx.put("poolName", "PgPool")
        }
    }

    fun updateCard(card: Card): Mono<Unit> {
        val query = """
                UPDATE card 
                SET name = $1, mana_cost = $2, cmc = $3, color = $4, colorIdentity = $5, 
                type = $6, types = $7, subtypes = $8, rarity = $9, set = $10, setName = $11, 
                text = $12, flavor = $13, artist = $14, number = $15, power = $16, toughness = $17, 
                imageUrl = $18, multiverseId = $19, legalities = $20, race = $21
                WHERE id = $22
            """
        val tuple = Tuple.of(
            card.name,
            card.manaCost,
            card.cmc,
            //TODO Convertion problems
            arrayOf(Json.arr(card.color.map { it.uppercase() }.toVavrList()).stringify()),
            arrayOf(Json.arr(card.colorIdentity.map { it.uppercase() }.toVavrList()).stringify()),
            card.type,
            arrayOf(Json.arr(card.types.toVavrList()).stringify()),
            arrayOf(Json.arr(card.subtypes.toVavrList()).stringify()),
            card.rarity,
            card.set,
            card.setName,
            card.text,
            card.flavor,
            card.artist,
            card.number.toInt(),
            card.power.getOrElse(0),
            card.toughness.getOrElse(0),
            card.imageUrl.getOrElse("A ajouter plus tard"),
            card.multiverseId.orNull,
            card.legalities.firstOrNull(),
            card.race.getOrElse(""),
            card.id
        )
        return jdbcClient.preparedReactiveQuery(query, tuple) {}
    }
}
