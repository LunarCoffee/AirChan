package dev.lunarcoffee.airchan.model

import dev.lunarcoffee.airchan.formatDefault
import dev.lunarcoffee.airchan.services.IdGenerator
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.random.Random

internal class Thread(val code: String) {
    // This can't be private since Gson needs to set it for the author IDs to be consistent.
    @Suppress("MemberVisibilityCanBePrivate")
    val idSalt = abs(Random.nextLong())

    val created = LocalDateTime.now().formatDefault()
    val posts = mutableListOf<Post>()
    val id = idGen.next()

    fun createPost(text: String, images: List<String>, authorIp: String) {
        val authorId = getAuthorId(authorIp)
        val post = Post(text, images, authorId)
        posts += post
        Board.saveAll()
    }

    private fun getAuthorId(ip: String) = abs("$ip$idSalt".hashCode())

    companion object {
        val idGen = IdGenerator("Thread")
    }
}
