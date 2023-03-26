package com.magic.front.warhammer40k.handlers

import com.magic.front.warhammer40k.model.Card.Companion.toJson
import com.magic.front.warhammer40k.services.CardService
import com.magic.front.warhammer40k.validators.CardValidator
import com.altima.lib.toolbox.errors.internalServerError
import com.altima.lib.toolbox.errors.notFound
import com.altima.lib.toolbox.extensions.flatMapEither
import com.altima.lib.toolbox.extensions.logger
import com.altima.lib.toolbox.extensions.onErrorOrEmptyResume
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class CardHandler(
    val cardService: CardService,
    val cardValidator: CardValidator
) {
    fun listCardWarhammer40k(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Listing Magic cards for Warhammer40k")

        return cardService.listCardWarhammer40KMagicApi()
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "card.not.found" -> notFound("card with the specified id was not found")
                            else -> internalServerError()
                        }
                    },
                    { cards ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                cards.toJson().stringify()
                            )
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling magic Api", it)
                internalServerError()
            }
    }

    fun listCardBdd(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Listing Magic cards from bdd")

        return cardService.listCardBdd()
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "empty.response" -> notFound("cards in bdd was empty")
                            else -> internalServerError()
                        }
                    },
                    { cards ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                cards.toJson().stringify()
                            )
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling database", it)
                internalServerError()
            }
    }

    fun listCardBothSource(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Listing Magic cards from bdd")

        return cardService.getAllCardsCombinated()
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "card.not.found" -> notFound("card with the specified id was not found")
                            "empty.response" -> notFound("cards in bdd was empty")
                            else -> internalServerError()
                        }
                    },
                    { cards ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                cards.toJson().stringify()
                            )
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling magic Api", it)
                internalServerError()
            }
    }

    fun getCardWarhammer40kById(request: ServerRequest): Mono<ServerResponse> {
        val cardId = request.pathVariable("cardId")
        logger.info("Listing Magic cards for Warhammer40k")

        return cardValidator.checkCardId(cardId)
            .flatMapEither { cardId -> cardService.getCardById(cardId) }
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "card.not.found" -> notFound("card with the specified id was not found")
                            else -> internalServerError()
                        }
                    },
                    { card ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                card.toJson().stringify()
                            )
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling magic Api", it)
                internalServerError()
            }
    }

    fun getCardBddById(request: ServerRequest): Mono<ServerResponse> {
        val cardId = request.pathVariable("cardId")
        logger.info("Listing Magic cards for Warhammer40k")

        return cardValidator.checkCardId(cardId)
            .flatMapEither { cardId -> cardService.getCardByIdBdd(cardId) }
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "card.not.found" -> notFound("card with the specified id was not found")
                            else -> internalServerError()
                        }
                    },
                    { card ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                card.toJson().stringify()
                            )
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling database", it)
                internalServerError()
            }
    }
}