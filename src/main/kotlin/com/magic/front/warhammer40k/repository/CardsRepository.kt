package com.magic.front.warhammer40k.repository

import com.custom.lib.toolbox.extensions.preparedReactiveQuery
import com.magic.front.warhammer40k.model.Card
import com.magic.front.warhammer40k.model.parts.File
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
                INSERT INTO card(name, mana_cost, cmc, color, color_identity, type, types, subtypes, rarity, set, set_name, text, flavor, artist, number, power, toughness, multiverse_id, legalities, race) 
                VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20);
            """

        val tuple = Tuple.of(
            card.name,
            card.manaCost,
            card.cmc,
            card.color.toTypedArray(),
            card.colorIdentity.toTypedArray(),
            card.type,
            card.types.toTypedArray(),
            card.subtypes.toTypedArray(),
            card.rarity,
            card.set,
            card.setName,
            card.text,
            card.flavor.orNull,
            card.artist,
            card.number.toInt(),
            card.power.getOrElse(0),
            card.toughness.getOrElse(0),
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
        //TODO Fix request for update
        val query = """
                UPDATE card
                SET name = $1, mana_cost = $2, cmc = $3, color = $4, color_identity = $5,
                type = $6, types = $7, subtypes = $8, rarity = $9, set = $10, set_name = $11,
                text = $12, flavor = $13, artist = $14, number = $15, power = $16, toughness = $17,
                multiverse_id = $18, legalities = $19, race = $20
                WHERE id = $21
            """

        val tuple = Tuple.of(
            card.name,
            card.manaCost,
            card.cmc,
            card.color.toTypedArray(),
            card.colorIdentity.toTypedArray(),
            card.type,
            card.types.toTypedArray(),
            card.subtypes.toTypedArray(),
            card.rarity,
            card.set,
            card.setName,
            card.text,
            card.flavor.orNull,
            card.artist,
            card.number.toInt(),
            card.power.getOrElse(0),
            card.toughness.getOrElse(0),
            card.multiverseId.orNull,
            card.legalities.firstOrNull(),
            card.race.getOrElse(""),
            card.id.toString()
        )
        return jdbcClient.preparedReactiveQuery(query, tuple) {}
    }
}
