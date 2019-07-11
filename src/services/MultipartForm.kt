package services

import io.ktor.application.ApplicationCall
import io.ktor.http.content.*
import io.ktor.request.receiveMultipart
import kotlinx.coroutines.*
import java.io.*
import kotlin.math.abs

private val supportedFormats = setOf("png", "jpg", "jpeg", "tif", "gif")
internal const val UPLOAD_DIR = "resources/static/files/uploads"

internal class MultipartForm(private val formItems: Map<String, String>, val file: File?) {
    operator fun get(key: String) = formItems[key]
}

internal suspend fun ApplicationCall.receiveMultipartForm(): MultipartForm {
    val multipart = receiveMultipart()
    val formItemMap = mutableMapOf<String, String>()
    var file: File? = null

    multipart.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> formItemMap[part.name!!] = part.value
            is PartData.FileItem -> {
                val originalName = part.originalFileName!!.substringBeforeLast(".")
                val extension = part.originalFileName!!.substringAfterLast(".")
                if (extension !in supportedFormats) {
                    return@forEachPart
                }

                val fileHash = abs(System.currentTimeMillis() * originalName.hashCode())
                file = File(UPLOAD_DIR, "$fileHash-$originalName.$extension")

                part.streamProvider().use { input ->
                    file!!.outputStream().buffered().use {
                        // The file was too big (above 8 MB raw).
                        if (!input.copyToSuspend(it)) {
                            file = null
                            return@forEachPart
                        }
                    }
                }
            }
        }
        part.dispose()
    }
    return MultipartForm(formItemMap, file)
}

private suspend fun InputStream.copyToSuspend(out: OutputStream): Boolean {
    val yieldSize = 4_194_304
    return withContext(Dispatchers.IO) {
        val buffer = ByteArray(4_194_304)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
            if (bytesCopied > 8_000_000) {
                return@withContext false
            }
        }
        true
    }
}
