package com.magic.front.warhammer40k.model

import com.custom.lib.toolbox.common.AppErrors
import io.vavr.control.Option
import org.reactivecouchbase.json.JsValue
import org.reactivecouchbase.json.Json
import org.reactivecouchbase.json.Syntax.`$`
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono

data class ErrorResponse(val code: Int, val message: String, val errors: Option<AppErrors> = Option.none()) {
    fun toJson(): JsValue {
        return Json.obj(
            `$`("message", message)
        ).merge(errors.map(AppErrors.format.writer).map { it.asObject() } .getOrElse(Json.obj()))
    }
}

fun status(status: HttpStatus, message: String, errors: AppErrors) = error(status, message, errors)

fun badRequest(message: String, errors: AppErrors) = error(HttpStatus.BAD_REQUEST, message, errors)

fun notFound(message: String) = error(HttpStatus.NOT_FOUND, message)

fun conflict(message: String) = error(HttpStatus.CONFLICT, message)

fun unauthorized(message: String) = error(HttpStatus.UNAUTHORIZED, message)

fun internalServerError(message: String) = error(HttpStatus.INTERNAL_SERVER_ERROR, message)

fun internalServerError(errors: AppErrors) = error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during your call", errors)

fun internalServerError() = error(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during your call, sorry for the inconvenience")

private fun error(status: HttpStatus, message: String): Mono<ServerResponse> {
    val error = ErrorResponse(status.value(), message)
    return status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .syncBody(error.toJson().stringify())
}

private fun error(status: HttpStatus, message: String, errors: AppErrors): Mono<ServerResponse> {
    val error = ErrorResponse(status.value(), message, Option.some(errors))
    return status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .syncBody(error.toJson().stringify())
}