package com.magic.front.warhammer40k.services


import com.altima.lib.toolbox.extensions.logger
import com.altima.lib.toolbox.extensions.mapRight
import com.magic.front.warhammer40k.clients.CacheClient
import com.magic.front.warhammer40k.clients.MagicClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.extra.retry.retryExponentialBackoff
import java.time.Duration

@Service
class CacheService(
    private val magicClient: MagicClient,
    private val cacheClient: CacheClient
) {
    fun cacheTypes(): Mono<Unit> {
        logger.info("caching document types")
        return Mono.zip(
            refApiClient.getTypes()
                .retryExponentialBackoff(3, Duration.ofSeconds(1)),
            refApiClient.getTypesDefault()
                .retryExponentialBackoff(3, Duration.ofSeconds(2)),
            refApiClient.getTypesClients()
                .retryExponentialBackoff(3, Duration.ofSeconds(3)),
            refApiClient.getTypesGenericClaims()
                .retryExponentialBackoff(3, Duration.ofSeconds(4))
        ).map { tupleEither ->
            listOf(
                Pair("types", tupleEither.t1),
                Pair("typesDefault", tupleEither.t2),
                Pair("typesClient", tupleEither.t3),
                Pair("typesGenericClaims", tupleEither.t4)
            )
        }.map { nodesOpt ->
            nodesOpt.forEach { (key, node) ->
                cacheClient.typesDocCache.put(
                    key,
                    node.values.map { type -> Pair(type.string("code"), type.string("label")) }.toMap()
                )
            }
            cacheClient.setUp()
            logger.info("caching document types finished")
        }
    }

    fun cacheOrigins(): Mono<Unit> {
        logger.info("caching origins")
        return magicClient.listCardsWarhammer()
            .retryExponentialBackoff(3, Duration.ofSeconds(1)).mapRight { cards ->
            cacheClient.cardsCache.put(
                "cards",
                cards
            )
            cacheClient.setUpOrigins()
            logger.info("caching origins finished")
        }.map {  }
    }
}
