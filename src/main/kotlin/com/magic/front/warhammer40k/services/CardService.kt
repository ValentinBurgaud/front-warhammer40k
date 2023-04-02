package com.magic.front.warhammer40k.services

import com.magic.front.warhammer40k.clients.MagicClient
import com.magic.front.warhammer40k.model.Card
import com.custom.lib.toolbox.common.AppErrors
import com.custom.lib.toolbox.extensions.mapEither
import com.custom.lib.toolbox.extensions.mapRight
import com.magic.front.warhammer40k.clients.CardsCache
import com.magic.front.warhammer40k.repository.CardsRepository
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.option
import io.vavr.kotlin.toVavrList
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class CardService(
    private val magicClient: MagicClient,
    private val cardsRepository: CardsRepository,
) {
    fun listCardWarhammer40KMagicApi(): Mono<Either<AppErrors, List<Card>>> {
        return magicClient.listCardsWarhammer()
    }

    fun listCardWarhammer40KMagicApiWithCache(): Mono<Either<AppErrors, List<Card>>> {
        val cacheCards = CardsCache.values.map { it.value as List<Card>}.flatten()
        return Option.`when`(cacheCards.isNotEmpty()) { cacheCards }.toEither(AppErrors.error("empty.response")).toMono()
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

    fun createCard(card: Card): Mono<Either<AppErrors, Card>> {
        return cardsRepository.insertCard(card).flatMap {
            listCardBdd().mapEither { cards ->
                cards.find { it.name == card.name && it.power == card.power }
                    .option()
                    .map {
                        Either.right<AppErrors, Card>(it)
                    }.getOrElse {
                        Either.left(AppErrors.error("card.not.found"))
                    }
            }
        }
    }

    fun deleteCard(id: String): Mono<Unit> {
        return cardsRepository.deleteById(id)
    }

    fun updateCard(card: Card): Mono<Either<AppErrors, Card>> {
        return cardsRepository.updateCard(card)
            .flatMap { cardsRepository.getCardById(card.id.toString()) }
            .map { cards ->
                Option.`when`(cards.isNotEmpty()) { cards }.toEither(AppErrors.error("card.not.found"))
            }.mapRight { it.first() }
    }
}