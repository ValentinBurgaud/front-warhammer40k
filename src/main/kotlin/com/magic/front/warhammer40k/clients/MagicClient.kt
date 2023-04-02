package com.magic.front.warhammer40k.clients

import com.magic.front.warhammer40k.config.domain.ApiKeyConfig
import com.magic.front.warhammer40k.model.Card
import com.custom.lib.toolbox.common.AppErrors
import com.custom.lib.toolbox.extensions.logCall
import com.custom.lib.toolbox.extensions.logger
import com.custom.lib.toolbox.extensions.mapRight
import com.custom.lib.toolbox.extensions.measure
import io.vavr.control.Either
import org.reactivecouchbase.json.JsArray
import org.reactivecouchbase.json.Json
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.netty.http.client.HttpClient

class MagicClient(private val apiConfig: ApiKeyConfig) {

    private val httpClient = HttpClient.create().baseUrl(apiConfig.baseUrl)
        .logCall("magic")

    fun getCardById(id: String): Mono<Either<AppErrors, Card>> {
        val uri = UriComponentsBuilder.newInstance()
            .pathSegment("v1", "cards", id)
            .build()
        return magicCall(uri) { client ->
            client.get()
        }.mapRight { response ->
            Card.fromJsonMagicApi(Json.parse(response).field("card"))
        }.switchIfEmpty(
            Either.left<AppErrors, Card>(AppErrors.fault("got unexpected status from magic")).toMono()
        )
    }

    fun listCardsWarhammer(): Mono<Either<AppErrors, List<Card>>> {
        val uri = UriComponentsBuilder.newInstance()
            .pathSegment("v1", "cards")
            .queryParam("set", "40K")
            .build()
        return magicCall(uri) { client ->
            client.get()
        }.mapRight { response ->
            Json.parse(response).array("cards").values.map {
                Card.fromJsonMagicApi(it)
            }.asJava()
        }.switchIfEmpty(
            Either.right<AppErrors, List<Card>>(emptyList()).toMono()
        )
    }

    private fun magicCall(
        uriComponents: UriComponents,
        use: (HttpClient) -> HttpClient.ResponseReceiver<*>
    ): Mono<Either<AppErrors, String>> {
        val uri = uriComponents.encode().toUriString()
        return use(
            httpClient.headers { h ->
                h.add("Accept", MediaType.APPLICATION_JSON_VALUE)
            }).uri(uri)
            .responseSingle<Either<AppErrors, String>> { t, u ->
                when (val status = t.status().code()) {
                    in 200..300 -> u.asString().map { Either.right(it) }
                    404 -> Mono.empty()
                    else -> u.asString()
                        .switchIfEmpty(Mono.just("no response"))
                        .map { responseStr ->
                            logger.error("got bad response from magic api :$responseStr, status:$status")
                            Either.left(
                                AppErrors.fault(
                                    "got bad status from magic api :$status",
                                    "${apiConfig.baseUrl}${uriComponents.encode().toUriString()}"
                                )
                            )
                        }
                }
            }.measure()
    }
}
