package model

import gson
import java.io.File

internal class Board(val code: String, val name: String, val section: String) {
    val threads = mutableListOf<Thread>()

    fun createThread(subject: String) {
        val thread = Thread(code)
        thread.posts += Post(subject, emptyList(), -1)
        threads += thread
        saveAll()
    }

    fun save() = File("$BOARD_DIR/$code.json").writeText(gson.toJson(this))

    companion object {
        private const val BOARD_DIR = "resources/boards"

        // Deserialize JSON boards.
        val boards = File(BOARD_DIR)
            .listFiles()!!
            .map { gson.fromJson(it.readText(), Board::class.java) }

        fun saveAll() {
            for (board in boards) {
                board.save()
            }
        }
    }
}
