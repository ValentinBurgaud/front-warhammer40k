package com.magic.front.warhammer40k.model.parts

import com.custom.lib.toolbox.json.*
import io.vavr.control.Option
import io.vavr.kotlin.option
import io.vavr.kotlin.toVavrList
import io.vertx.core.buffer.Buffer
import org.reactivecouchbase.json.Json
import org.reactivecouchbase.json.Syntax.`$`
import org.springframework.http.MediaType
import java.io.InputStream
import java.io.SequenceInputStream
import io.vertx.sqlclient.Row

data class File(
    val fileName: String,
    val file: InputStream,
    val mediaType: MediaType,
) {
    companion object {
        fun fromBdd(row: Row): File {
            return File(
                fileName = row.getString("image_name").option().getOrElse(""),
                mediaType = MediaType.valueOf(row.getString("image_content_type").option().getOrElse("")),
                file = row.getBuffer("image").option().getOrElse(Buffer.buffer()).bytes.inputStream(),
            )
        }
    }
}

data class FilePart(
    val contentType: String,
    val fileName: String,
    val size: Long,
    val inputStream: InputStream
) {

    class Builder(
        val contentType: String = "",
        val fileName: String = "",
        var size: Long = 0,
        var inputStream: Option<InputStream> = Option.none()
    ) {
        fun collectInputStream(next: InputStream, size: Long): Builder {
            if (inputStream.isEmpty) this.inputStream = Option.some(next)
            inputStream = inputStream.map { SequenceInputStream(inputStream.get(), next) }
            this.size += size
            return this
        }

        fun build(): FilePart {
            return FilePart(contentType, fileName, size, inputStream.get())
        }
    }
}

data class MultiPart<T>(
    val file: FilePart,
    val metadata: T
)

data class FileBase64(
    val fileName: String,
    val contentType: String,
    val base64Bytes: String
) {
    data class Builder(
        var fileName: String = "",
        var contentType: String = "",
        var base64Bytes: String = ""

    ) {
        fun fileName(fileName: String) = apply { this.fileName = fileName }
        fun contentType(contentType: String) = apply { this.contentType = contentType }
        fun base64Bytes(base64Bytes: String) = apply { this.base64Bytes = base64Bytes }
        fun build() = FileBase64(fileName, contentType, base64Bytes)
    }

    companion object {
        val reader = _string("fileName") { fileName -> Builder().fileName(fileName) }
            .and(_string("contentType")) { b, contentType -> b.contentType(contentType) }
            .and(_string("base64Bytes")) { b, base64Bytes -> b.base64Bytes(base64Bytes) }
            .map { it.build() }
    }
}
