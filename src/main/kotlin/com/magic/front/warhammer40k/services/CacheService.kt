package com.magic.front.warhammer40k.services

import com.altima.lib.toolbox.extensions.logger
import com.altima.lib.toolbox.extensions.mapLeftEither
import com.altima.lib.toolbox.extensions.mapRight
import com.magic.front.warhammer40k.clients.CacheClient
import com.magic.front.warhammer40k.clients.MagicClient
import io.vavr.control.Either
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CacheService(
    private val magicClient: MagicClient,
    private val cacheClient: CacheClient
) {
    fun cacheCards(): Mono<Unit> {
        logger.info("caching cards")
        //TODO Improve retry condition
        return magicClient.listCardsWarhammer()
            .retry()
            .mapLeftEither { error -> Either.right(emptyList()) }
            .mapRight { cards ->
            cacheClient.cardsCache.put(
                "cards",
                cards
            )
            cacheClient.setUp()
            logger.info("caching cards finished")
        }.map {  }
    }
}
