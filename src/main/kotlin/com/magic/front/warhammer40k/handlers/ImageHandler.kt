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
import com.magic.front.warhammer40k.services.ImageService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
    // TODO a déplacer
    private val authorizedImageType = listOf("\"image/jpg\", \"image/jpeg\", \"image/pjpeg\", \"image/png\"")
//    private val authorizedImageType = listOf("image/jpg, image/jpeg, image/pjpeg, image/png")


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