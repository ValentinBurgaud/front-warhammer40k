package com.magic.front.warhammer40k.repository

import com.altima.lib.toolbox.extensions.preparedReactiveQuery
import com.magic.front.warhammer40k.model.Card
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.*
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

    fun insertDocSetting(card: Card): Mono<Unit> {
        val query = """
                INSERT INTO card(name, mana_cost, cmc, color, color_identity, type, types, subtypes, rarity, set, set_name, text, flavor, artist, number, power, toughness, image_url, multiverse_id, legalities, race) 
                VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21);
            """
        val tuple = Tuple.of(
            card.name,
            card.manaCost,
            card.cmc,
            card.color,
            card.colorIdentity,
            card.type,
            card.types,
            card.subtypes,
            card.rarity,
            card.set,
            card.setName,
            card.text,
            card.flavor,
            card.artist,
            card.number,
            card.power,
            card.toughness,
            card.imageUrl,
            card.multiverseId,
            card.legalities,
            card.race
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
}
