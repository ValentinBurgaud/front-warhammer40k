package com.magic.front.warhammer40k.handlers

import com.custom.lib.toolbox.extensions.flatMapEither
import com.custom.lib.toolbox.extensions.logger
import com.custom.lib.toolbox.extensions.onErrorOrEmptyResume
import com.magic.front.warhammer40k.model.internalServerError
import com.magic.front.warhammer40k.model.notFound
import com.magic.front.warhammer40k.services.ImageService
import com.magic.front.warhammer40k.validators.CardValidator
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ImageHandler(
    val imageService: ImageService,
    val cardValidator: CardValidator
) {

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
}