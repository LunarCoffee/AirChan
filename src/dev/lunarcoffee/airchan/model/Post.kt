package dev.lunarcoffee.airchan.model

import dev.lunarcoffee.airchan.formatDefault
import dev.lunarcoffee.airchan.services.IdGenerator
import java.time.LocalDateTime

internal class Post(val text: String, val images: List<String>, val authorId: Int) {
    val created = LocalDateTime.now().formatDefault()
    val id = idGen.next()

    companion object {
        val idGen = IdGenerator("Post")
    }
}
