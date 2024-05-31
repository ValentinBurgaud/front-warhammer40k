package com.magic.front.warhammer40k.handlers

import com.custom.lib.toolbox.extensions.flatMapEither
import com.custom.lib.toolbox.extensions.logger
import com.custom.lib.toolbox.extensions.onErrorOrEmptyResume
import com.magic.front.warhammer40k.asMultipart
import com.magic.front.warhammer40k.model.Card
import com.magic.front.warhammer40k.model.internalServerError
import com.magic.front.warhammer40k.model.notFound
import com.magic.front.warhammer40k.model.status
import com.magic.front.warhammer40k.services.ImageService
import com.magic.front.warhammer40k.validators.CardValidator
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Component
class ImageHandler(
    val imageService: ImageService,
    val cardValidator: CardValidator
) {

    private val authorizedImageType = listOf("image/jpg", "image/jpeg", "image/pjpeg", "image/png")

    fun downloadImage(request: ServerRequest): Mono<ServerResponse> {
        val cardId = request.pathVariable("cardId")
        logger.info("Downloading image for Warhammer40k")

        return cardValidator.checkCardId(cardId)
            .flatMapEither { cardId -> imageService.downloadCardById(cardId) }
            .flatMap { either ->
                either.fold(
                    { errors ->
                        when (errors.errors[0].message) {
                            "image.not.found" -> notFound("image for this specified cardId was not found")
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

    fun addImageOnCard(request: ServerRequest): Mono<ServerResponse> {
        logger.info("Add image on existing card in database")

        val cardId = request.pathVariable("cardId")
        return request.asMultipart(authorizedImageType)
            .flatMapEither {
                imageService.createImage(it, UUID.fromString(cardId))
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
                    { _ ->
                        ServerResponse.noContent().build()
                    })
            }
            .onErrorOrEmptyResume {
                logger.error("an error occurred while calling database", it)
                internalServerError()
            }
    }
}