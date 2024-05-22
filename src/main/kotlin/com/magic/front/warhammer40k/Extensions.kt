package com.magic.front.warhammer40k


import com.magic.front.warhammer40k.model.parts.FilePart
import com.magic.front.warhammer40k.model.parts.MultiPart
import com.custom.lib.toolbox.common.AppErrors
import com.custom.lib.toolbox.extensions.mapEither
import com.custom.lib.toolbox.json.JsResult
import com.magic.front.warhammer40k.model.Card
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.control.Try
import io.vavr.kotlin.Try
import io.vavr.kotlin.option
import org.apache.tika.Tika
import org.reactivecouchbase.json.JsString
import org.reactivecouchbase.json.JsValue
import org.reactivecouchbase.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class Extensions {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(Extensions::class.java)
    }
}

fun _uuid(): (JsValue) -> JsResult<UUID> {
    return { json: JsValue ->
        if (!Objects.isNull(json) && json.`is`(JsString::class.java) &&
            json.asString().isNotBlank() &&
            Try { UUID.fromString(json.asString()) }.isSuccess) {
            JsResult.success(UUID.fromString(json.asString()))
        } else {
            JsResult.error(io.vavr.collection.List.of(JsResult.Error.error("uuid.expected")))
        }
    }
}

fun <T> ServerRequest.asMultipart(reader: (JsValue) -> JsResult<T>, authorizedDocTypes: kotlin.collections.List<String>): Mono<Either<AppErrors, MultiPart<Card>>> =
    Option.ofOptional(headers().contentType()).getOrElse { MediaType.APPLICATION_JSON }.let { media ->
        Extensions.logger.info(" --- got content type {}", media)
        when (media.toString().substringBefore(';')) {
            MediaType.MULTIPART_FORM_DATA_VALUE -> {
                body(BodyExtractors.toMultipartData())
                    .flatMap { parts ->
                        val filePart = parts.toSingleValueMap()["file"].option()
                        if (filePart.isEmpty) {
                            Either.left<AppErrors, MultiPart<Card>>(AppErrors.error("file.is.required")).toMono()
                        } else {
                            val metadataPart = parts.toSingleValueMap()["metadata"].option()
                            val headers = filePart.map { it.headers().toSingleValueMap().mapKeys { (key) -> key.lowercase() } }
                                .getOrElse { emptyMap<String, String>() }
                            when {
                                filePart is Option.None -> Either.left<AppErrors, MultiPart<Card>>(AppErrors.error("unable.to.read.file")).toMono()
                                metadataPart is Option.None -> Either.left<AppErrors, MultiPart<Card>>(AppErrors.error("metadata.json.empty")).toMono()
                                authorizedDocTypes.none { authorizedType ->
                                    authorizedType == headers["content-type"].option().map { type -> type.substringBefore(';') }
                                        .getOrElse("")
                                } -> {
                                    val contentType = headers["content-type"].option().map { type -> type.substringBefore(';') }.getOrElse("")
                                    Extensions.logger.info(" --- got wrong content-type {}", contentType)
                                    Either.left<AppErrors, MultiPart<Card>>(AppErrors.error("file".option(), "content.type.unsupported", *authorizedDocTypes.toTypedArray())).toMono()
                                }
                                else -> {
                                    val contentType = headers["content-type"].option().map { type -> type.substringBefore(';') }.get()
                                    val contentDisposition = headers["content-disposition"].option().get().split("; ")
                                    val fileName: String = contentDisposition.first { value -> value.startsWith("filename=") }
                                        .split("=")[1]
                                        .replace("\"", "")

                                    val filePartBuilder = FilePart.Builder(contentType, fileName)

                                    val meta = metadataPart.get().content().reduce("") { accMeta, next ->
                                        val bytes = ByteArray(next.readableByteCount())
                                        next.read(bytes)
                                        accMeta + String(bytes)
                                    }.map { metaJs ->
                                        Try.of { Json.parse(metaJs) }.toEither().mapLeft { AppErrors.error("json.invalid") }
                                    }.mapEither { metaJson ->
                                        reader(metaJson).toEither()
                                            .mapLeft { errors -> AppErrors.fromJsErrors(errors.asJava()) }
                                    }
                                    val file = filePart.get().content().reduce(filePartBuilder) { accFile, next ->
                                        accFile.collectInputStream(next.asInputStream(true), next.readableByteCount().toLong())
                                    }.map { part ->
                                        part.inputStream.toEither(AppErrors.error("unable.to.read.file")).map { part.build() }
                                    }
                                    Mono.zip(file, meta).map { tuple ->
                                        Either.sequence(List.of(tuple.t1, tuple.t2))
                                            .bimap({ errors ->
                                                AppErrors.errors(errors.map { it.errors }.flatten())
                                            }, { rights ->
                                                MultiPart(rights[0] as FilePart, rights[1] as Card)
                                            })
                                    }
                                }
                            }
                        }
                    }
            }
            else -> Either.left<AppErrors, MultiPart<Card>>(AppErrors.error("unsupported.media.type")).toMono()
        }.mapEither { multiPart ->
            Option.`when`(multiPart.file.size < 15728640, multiPart).toEither(AppErrors.error("file.too.large"))
        }.mapEither { multiPart ->
            val baos = ByteArrayOutputStream()
            multiPart.file.inputStream.transferTo(baos)
            val firstClone: InputStream = ByteArrayInputStream(baos.toByteArray())
            val secondClone: InputStream = ByteArrayInputStream(baos.toByteArray())
            val inspectedContentType: String = Tika().detect(firstClone)
            Extensions.logger.debug(" --- inspectedContentType {}", inspectedContentType)
            Extensions.logger.debug(" --- actual contentType {}", multiPart.file.contentType)
            Extensions.logger.debug(" --- file size {}", multiPart.file.size)
            Option.`when`( multiPart.file.contentType == inspectedContentType, multiPart.copy(multiPart.file.copy(inputStream = secondClone)))
                .toEither(AppErrors.error("file".option(), "content.type.mismatch", multiPart.file.contentType, inspectedContentType))
        }
    }
