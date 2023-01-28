package com.magic.front.warhammer40k.validators

import com.altima.lib.toolbox.common.AppErrors
import com.altima.lib.toolbox.extensions.isUUID
import io.vavr.control.Either
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CardValidator {
    fun checkCardId(id: String): Mono<Either<AppErrors, String>> {
        return if (id.isUUID()){
            Either.right<AppErrors, String>(id).toMono()
        } else {
            Either.left<AppErrors, String>(AppErrors.error("invalid.card.format")).toMono()
        }
    }
}