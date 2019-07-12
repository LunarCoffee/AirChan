package model

import formatDefault
import services.IdGenerator
import java.time.LocalDateTime

internal class Post(val text: String, val images: List<String>, val authorId: Int) {
    val created = LocalDateTime.now().formatDefault()
    val id = idGen.next()

    companion object {
        val idGen = IdGenerator("Post")
    }
}
