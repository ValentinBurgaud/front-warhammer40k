package com.magic.front.warhammer40k.services

import com.custom.lib.toolbox.common.AppErrors
import com.custom.lib.toolbox.extensions.mapEither
import com.custom.lib.toolbox.extensions.mapRight
import com.magic.front.warhammer40k.model.Card
import com.magic.front.warhammer40k.model.parts.File
import com.magic.front.warhammer40k.model.parts.FilePart
import com.magic.front.warhammer40k.repository.ImagesRepository
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.option
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class ImageService(
    private val imagesRepository: ImagesRepository,
) {
    fun createImage(file: FilePart, cardId: UUID): Mono<Either<AppErrors, File>> {
        return imagesRepository.insertImage(file, cardId).flatMap {
            getImageByCardID(cardId.toString())
        }
    }

    fun getImageByCardID(cardId: String): Mono<Either<AppErrors, File>> {
        return imagesRepository.getImageByCardId(cardId).map { images ->
            Option.`when`(images.isNotEmpty()) { images }.toEither(AppErrors.error("image.not.found"))
        }.mapRight { it.first() }
    }

    fun downloadCardById(id: String): Mono<Either<AppErrors, File>> {
        return imagesRepository.downloadImagByCardId(id).map { images ->
            Option.`when`(images.isNotEmpty()) { images }.toEither(AppErrors.error("image.not.found"))
        }.mapRight { it.first() }
    }
}