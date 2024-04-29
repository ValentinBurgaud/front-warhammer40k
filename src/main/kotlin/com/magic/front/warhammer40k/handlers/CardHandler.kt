package com.magic.front.warhammer40k.handlers

//import com.custom.lib.toolbox.errors.internalServerError
//import com.custom.lib.toolbox.errors.notFound
//import com.custom.lib.toolbox.errors.status
import com.magic.front.warhammer40k.model.Card.Companion.toJson
import com.magic.front.warhammer40k.services.CardService
import com.magic.front.warhammer40k.validators.CardValidator
import com.magic.front.warhammer40k.model.internalServerError
import com.magic.front.warhammer40k.model.notFound
import com.magic.front.warhammer40k.model.status
import com.custom.lib.toolbox.extensions.*
import com.magic.front.warhammer40k.asMultipart
import com.magic.front.warhammer40k.model.Card
import com.magic.front.warhammer40k.parsers.patch.Patches
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class CardHandler(
    val cardService: CardService,
    val cardValidator: CardValidator
) {
    // TODO a déplacer
    private val authorizedImageType = listOf("\"image/jpg\", \"image/jpeg\", \"image/pjpeg\", \"image/png\"")
//    private val authorizedImageType = listOf("image/jpg, image/jpeg, image/pjpeg, image/png")

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

    fun listCardWithCache(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Listing Magic cards from cache")

        return cardService.listCardWarhammer40KMagicApiWithCache()
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

    fun downloadImage(request: ServerRequest): Mono<ServerResponse> {
        val cardId = request.pathVariable("cardId")
        logger.info("Downloading image for Warhammer40k")

        return cardValidator.checkCardId(cardId)
            .flatMapEither { cardId -> cardService.downloadCardById(cardId) }
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "card.not.found" -> notFound("card with the specified id was not found")
                            else -> internalServerError()
                        }
                    },
                    { file ->
                        ServerResponse.ok()
                            .contentType(file.mediaType)
                            .header(
                                "Content-Disposition",
                                "attachment; filename=\"${file.fileName}\"; filename*=UTF-8''${file.fileName}';"
                            )
                            .body(BodyInserters.fromResource(InputStreamResource(file.file)))
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling database", it)
                internalServerError()
            }
    }

    fun createCard(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Create card in database")

        //TODO validator on data
        return request.readBodyUsing(Card.format.reader)
            .flatMapEither {
                cardService.createCard(it)
            }
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

    fun createCardWithImage(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Create card with image in database")

        //TODO validator on data
        return request.asMultipart(Card.format.reader, authorizedImageType)
            .flatMapEither {
                cardService.createCard(it.file, it.metadata)
            }
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "card.not.found" -> notFound("card with the specified id was not found")
                            "content.type.unsupported" -> status(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid body", errors)
                            "file.too.large" -> status(HttpStatus.PAYLOAD_TOO_LARGE, "Invalid body", errors)
                            "unable.to.read.file" -> status(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid body", errors)
                            "content.type.mismatch" -> status(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid body", errors)
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

    fun deleteCard(request: ServerRequest): Mono<ServerResponse> {
        val cardId = request.pathVariable("cardId")
        logger.info("Deleted card on database")

        return cardService.deleteCard(cardId)
            .flatMap {
                ServerResponse.noContent().build()
            }.onErrorOrEmptyResume {
                logger.error("an error occurred while deleted card", it)
                ServerResponse.status(500).build()
            }
    }

    fun updateCard(request: ServerRequest): Mono<ServerResponse> {
        logger.info("update card in database")
        val cardId = request.pathVariable("cardId")

        return request.readBodyUsing(Patches.format.reader)
            .flatMapEither { patches -> cardValidator.checkCardId(cardId).mapRight { Pair(patches, it) } }
            .flatMapEither { (patches, id) -> cardValidator.validateCardPatch(patches, id).mapRight { it.second } }
            .flatMapEither { card -> cardService.updateCard(card) }
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