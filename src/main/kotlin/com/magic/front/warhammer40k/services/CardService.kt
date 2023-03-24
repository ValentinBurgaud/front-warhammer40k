package com.magic.front.warhammer40k.services

import com.magic.front.warhammer40k.clients.MagicClient
import com.magic.front.warhammer40k.model.Card
import com.altima.lib.toolbox.common.AppErrors
import com.altima.lib.toolbox.extensions.mapRight
import com.magic.front.warhammer40k.repository.CardsRepository
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CardService(
    private val magicClient: MagicClient,
    private val cardsRepository: CardsRepository,
) {
    fun listCardWarhammer40KMagicApi(): Mono<Either<AppErrors, List<Card>>> {
        return magicClient.listCardsWarhammer()
    }

    fun listCardBdd(): Mono<Either<AppErrors, List<Card>>> {
        return cardsRepository.listCards().map { cards ->
            Option.`when`(cards.isNotEmpty()) { cards }.toEither(AppErrors.error("empty.response"))
        }
    }

    fun getCardById(id: String): Mono<Either<AppErrors, Card>> {
        return magicClient.getCardById(id)
    }

    fun getCardByIdBdd(id: String): Mono<Either<AppErrors, Card>> {
        return cardsRepository.getCardById(id).map { cards ->
            Option.`when`(cards.isNotEmpty()) { cards }.toEither(AppErrors.error("card.not.found"))
        }.mapRight { it.first() }
    }

    fun getAllCardsCombinated(): Mono<Either<AppErrors, List<Card>>> {
        return Mono.zip(
            magicClient.listCardsWarhammer(),
            listCardBdd()
        ).map { tuple ->
            Either.sequence(listOf(tuple.t1, tuple.t2).toVavrList())
                .bimap({ errors ->
                    AppErrors.errors(errors.map { it.errors }.flatten())
                }, { rights ->
                    rights.flatten()
                })
        }
    }
}