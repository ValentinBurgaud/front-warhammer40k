package com.magic.front.warhammer40k.repository

import com.custom.lib.toolbox.extensions.preparedReactiveQuery
import com.magic.front.warhammer40k.model.Card
import com.magic.front.warhammer40k.model.parts.File
import com.magic.front.warhammer40k.model.parts.FilePart
import io.vavr.kotlin.toVavrList
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.*
import org.reactivecouchbase.json.Json
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class ImagesRepository(private val jdbcClient: PgPool) {

    fun downloadImageById(cardId: String): Mono<List<File>> {
        val query =
            """
                SELECT * FROM IMAGE
                WHERE id = $1
            """

        return jdbcClient.preparedReactiveQuery(query, Tuple.of(cardId)) { result ->
            result.map { row ->
                File.fromBdd(row)
            }
        }.contextWrite { ctx ->
            ctx.put("sqlQuery", query)
            ctx.put("poolName", "PgPool")
        }
    }

    fun downloadImagByCardId(cardId: String): Mono<List<File>> {
        val query =
            """
                SELECT * FROM IMAGE
                WHERE card_id = $1
            """

        return jdbcClient.preparedReactiveQuery(query, Tuple.of(cardId)) { result ->
            result.map { row ->
                File.fromBdd(row)
            }
        }.contextWrite { ctx ->
            ctx.put("sqlQuery", query)
            ctx.put("poolName", "PgPool")
        }
    }

    fun getImageByCardId(cardId: String): Mono<List<File>> {
        val query =
            """
                SELECT * FROM IMAGE
                WHERE card_id = $1
            """

        return jdbcClient.preparedReactiveQuery(query, Tuple.of(cardId)) { result ->
            result.map { row ->
                File.fromBdd(row)
            }
        }.contextWrite { ctx ->
            ctx.put("sqlQuery", query)
            ctx.put("poolName", "PgPool")
        }
    }

    fun insertImage(file: FilePart, idCard: UUID): Mono<Unit> {
        val query = """
                INSERT INTO image(image, image_name, image_size, image_content_type, card_id) 
                VALUES($1, $2, $3, $4, $5);
            """
        val tuple = Tuple.of(
            file.inputStream.readAllBytes(),
            file.fileName,
            file.size,
            file.contentType,
            idCard,
        )
        return jdbcClient.preparedReactiveQuery(query, tuple) {}
    }
}
