package com.magic.front.warhammer40k.common

import com.altima.lib.toolbox.extensions.logger
import com.altima.lib.toolbox.json.JsonExt
import com.altima.lib.toolbox.json.JsonFormat
import com.altima.lib.toolbox.common.AppErrors
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jsonpatch.JsonPatch
import io.vavr.control.Either
import io.vavr.control.Try
import org.reactivecouchbase.json.Json

object JsonPatch {

    fun <T> apply(value: T, patch: JsonNode, format: JsonFormat<T>): Either<AppErrors, T> {
        return Try.of { JsonPatch.fromJson(patch) }
            .toEither()
            .mapLeft { AppErrors.error("error.jsonpatch.invalid") }
            .flatMap { jsonPatch ->
                logger.info("Applying patch \n{} \nto \n{}", patch, value)
                Try.of { jsonPatch.apply(JsonExt.toJson(value, format).asJsonNode()) }
                    .onFailure { logger.error("Error during patch ", it) }
                    .toEither(AppErrors.error("error.jsonpatch.apply.error"))
            }
            .peek { j -> logger.info("Result is \n{}", j) }
            .flatMap { j ->
                Try.of {
                    JsonExt.fromJson(Json.fromJsonNode(j), format)
                        .toEither()
                        .mapLeft { errors -> AppErrors.fromJsErrors(errors.asJava()) }
                }.toEither()
                    .mapLeft { AppErrors.error("error.jsonpatch.invalid") }
                    .flatMap { it }
            }
    }
}