package com.magic.front.warhammer40k.repository

import com.altima.lib.toolbox.extensions.preparedReactiveQuery
import com.magic.front.warhammer40k.model.Card
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Tuple
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
                SELECT * FROM CARD
            """
        return jdbcClient.preparedReactiveQuery(query) { result ->
            result.map { row ->
                Card.fromBdd(row)
            }
        }
    }
}


