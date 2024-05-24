package com.magic.front.warhammer40k.handlers

import com.magic.front.warhammer40k.model.enums.ColorType
import io.vavr.kotlin.toVavrList
import org.reactivecouchbase.json.Json
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class StaticListHandler() {
    fun getColor(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Json.array(ColorType.values().map { it.toJson() }.toVavrList()).stringify())
    }
}