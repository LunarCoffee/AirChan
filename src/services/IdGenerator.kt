package services

import kotlinx.atomicfu.atomic
import java.io.File

internal class IdGenerator(private val name: String) {
    init {
        active += this
    }

    private val counter = atomic(loadPrev(name) ?: 0)

    fun next() = counter.getAndIncrement().also { save() }

    private fun save() {
        file.writeText(active.joinToString("\n") { "${it.name} ${it.counter.value}" })
    }

    companion object {
        private val active = mutableListOf<IdGenerator>()
        private val file = File("resources/id.txt")

        private fun loadPrev(name: String): Long? {
            return file
                .readLines()
                .find { it.startsWith("$name ") }!!
                .substringAfter(" ")
                .toLong()
        }
    }
}
