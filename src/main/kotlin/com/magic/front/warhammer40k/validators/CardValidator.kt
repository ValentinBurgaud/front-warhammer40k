package com.magic.front.warhammer40k.validators

import com.custom.lib.toolbox.common.AppError
import com.custom.lib.toolbox.common.AppErrors
import com.custom.lib.toolbox.extensions.isUUID
import com.custom.lib.toolbox.extensions.mapEither
import com.custom.lib.toolbox.json.JsonExt
import com.custom.lib.toolbox.json.JsonFormat
import com.custom.lib.toolbox.validations.Rule
import com.magic.front.warhammer40k.common.JsonPatch
import com.magic.front.warhammer40k.model.Card
import com.magic.front.warhammer40k.parsers.patch.PatchOp
import com.magic.front.warhammer40k.parsers.patch.Patches
import com.magic.front.warhammer40k.services.CardService
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.Try
import io.vavr.kotlin.option
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CardValidator(
    private val cardService: CardService
) {
    fun checkCardId(id: String): Mono<Either<AppErrors, String>> {
        return if (id.isUUID()){
            Either.right<AppErrors, String>(id).toMono()
        } else {
            Either.left<AppErrors, String>(AppErrors.error("invalid.card.format")).toMono()
        }
    }

    fun validateCreation(card: Card): Either<AppErrors, Card> {
        return card.vCreation.toEither(card).mapLeft { AppErrors.errors(it.asJava()) }
    }

    fun validateCardPatch(patch: Patches, cardId: String): Mono<Either<AppErrors, Pair<Patches, Card>>> {
        return cardService.getCardByIdBdd(cardId)
            .mapEither { card ->
                Rule.combine(
                    *patch.operations.mapIndexed { i, op ->
                        Rule.invalidUnless(
                            AppError.error("array[$i].path".option(), "not.allowed",
                            listOf("/name", "/manaCost", "/cmc", "/color", "/colorIdentity", "/type", "/types", "/subtypes", "/rarity", "/set", "/setName", "/text", "/flavor", "/artist", "/number", "/power", "/toughness", "/multiverseId", "/legalities", "/race")
                        )) {
                            op.path.startsWith("/name") ||
                                op.path.startsWith("/manaCost") ||
                                op.path.startsWith("/cmc") ||
                                op.path.startsWith("/color") ||
                                op.path.startsWith("/colorIdentity") ||
                                op.path.startsWith("/type") ||
                                op.path.startsWith("/types") ||
                                op.path.startsWith("/subtypes") ||
                                op.path.startsWith("/rarity") ||
                                op.path.startsWith("/set") ||
                                op.path.startsWith("/setName") ||
                                op.path.startsWith("/text") ||
                                op.path.startsWith("/flavor") ||
                                op.path.startsWith("/artist") ||
                                op.path.startsWith("/number") ||
                                op.path.startsWith("/power") ||
                                op.path.startsWith("/toughness") ||
                                op.path.startsWith("/multiverseId") ||
                                op.path.startsWith("/legalities") ||
                                op.path.startsWith("/race")
                        }.and(Rule.invalidWhen(AppError.error("array[$i].value", "path.not.found")) {
                            op.op != PatchOp.REMOVE && op.value.isEmpty
                        })
                    }.toTypedArray()
                )
                    .toEither()
                    .mapLeft { errors -> AppErrors.errors(errors.asJava()) }
                    .flatMap {
                        JsonPatch.apply(
                            card,
                            JsonExt.toJson(patch, Patches.format).asJsonNode(),
                            JsonFormat.of(Card.formatUpdate.reader) { p -> Card.asRequest(p) }
                        )
                    }.flatMap {
                        Rule.combine(
                            it.vAll
                        ).toEither(Pair(patch, it))
                            .mapLeft { errors -> AppErrors.errors(errors.asJava()) }
                    }
            }
    }

    val Card.vAll: Rule<AppError>
        get() = Rule.combine(
            vName,
            vRace
        )

    val Card.vCreation: Rule<AppError>
        get() = Rule.combine(
            vName,
            vRace,
            vManaCost,
            vColor,
            vColorIdentity,
            vType,
            vRarity,
            vSet,
            vSetName,
            vText,
            vPower,
            vToughness
        )

    val Card.vName: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("name"), "invalid.body")) {
            name.isEmpty()
        }

    val Card.vRace: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("race"), "invalid.body")) {
            !race.isDefined
        }

    val Card.vManaCost: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("manaCost"), "invalid.body")) {
            manaCost.isEmpty()
        }

    val Card.vColor: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("color"), "invalid.body")) {
            color.isEmpty()
        }

    val Card.vColorIdentity: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("colorIdentity"), "invalid.body")) {
            colorIdentity.isEmpty()
        }

    val Card.vType: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("type"), "invalid.body")) {
            type.isEmpty()
        }

    val Card.vRarity: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("rarity"), "invalid.body")) {
            rarity.isEmpty()
        }

    val Card.vSet: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("set"), "invalid.body")) {
            set.isEmpty()
        }

    val Card.vSetName: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("setName"), "invalid.body")) {
            setName.isEmpty()
        }

    val Card.vText: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("set"), "invalid.body")) {
            set.isEmpty()
        }

    val Card.vPower: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("power"), "invalid.body")) {
            !power.isDefined
        }

    val Card.vToughness: Rule<AppError>
        get() = Rule.invalidWhen(AppError.error(Option.some("toughness"), "invalid.body")) {
            !toughness.isDefined
        }
}