package dev.lunarcoffee.airchan.services

import kotlinx.html.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

private val noImageFileRegex = """\d+-no-image.png""".toRegex()

internal fun HtmlBlockTag.showImages(images: List<String>, op: Boolean = false) {
    if (images.isEmpty()) {
        return
    }

    for (image in images) {
        val relativePath = "/files/uploads/$image"
        val minMaxClassOrId = image.replace("""\s+""".toRegex(), "")

        p(classes = "image-info") {
            // Put a minimize thread button to the left of OP's first image.
            if (op) {
                img(
                    alt = "Minimize thread",
                    src = "/files/thread-min.png",
                    classes = "thread-toggle-button"
                ) {
                    onClick = """
                        var e = document.getElementsByClassName("1$minMaxClassOrId");
                        for (var i = 0; i < e.length; i++) {
                            e[i].style.display = "none";
                        }
                    """
                }
            }

            div(classes = "1$minMaxClassOrId") {
                style = "margin-bottom:-8px;"

                // The user did not manually upload a photo here.
                +"File: "
                if (image.matches(noImageFileRegex)) {
                    +"(none)"
                    return@div
                }

                // Display original filename.
                a(href = relativePath, target = "_blank", classes = "reply-a") {
                    +image.substringAfter("-")
                }

                // Display image size and dimensions.
                val filePath = "resources/static$relativePath"
                val size = humanSize(Files.size(Paths.get(filePath)))
                val bufferedImage = ImageIO.read(File(filePath)) ?: return@div
                +" ($size, ${bufferedImage.width}x${bufferedImage.height})"
            }
        }

        // Show the actual image, and toggle zoom on click.
        div(classes = "image-div 1$minMaxClassOrId") {
            img(alt = image, src = relativePath) {
                id = minMaxClassOrId
                onClick = """
                    var e = document.getElementById("$id");
                    e.style.maxWidth = e.style.maxWidth === "97vw" ? "30vh" : "97vw";
                    e.style.width = e.style.width === "97vw" ? "unset" : "97vw";
                """
            }
        }
    }
}

private fun humanSize(bytes: Long): String {
    return when {
        bytes < 1_000 -> "$bytes B"
        bytes < 1_000_000 -> "${bytes / 1_000} KB"
        else -> "${roundToTwoPlaces(bytes.toDouble() / 1_000_000)} MB"
    }
}

private fun roundToTwoPlaces(double: Double): String {
    val string = double.toString()
    return "${string.substringBefore(".")}.${string.substringAfter(".").take(2)}"
}
