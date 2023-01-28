package com.magic.front.warhammer40k.services

import com.magic.front.warhammer40k.clients.MagicClient
import com.magic.front.warhammer40k.model.Card
import com.altima.lib.toolbox.common.AppErrors
import io.vavr.control.Either
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CardService(
    private val magicClient: MagicClient,
) {
    fun listCardWarhammer40KMagicApi(): Mono<Either<AppErrors, List<Card>>> {
        return magicClient.listCardsWarhammer()
    }

    fun getCardById(id: String): Mono<Either<AppErrors, Card>> {
        return magicClient.getCardById(id)
    }
}