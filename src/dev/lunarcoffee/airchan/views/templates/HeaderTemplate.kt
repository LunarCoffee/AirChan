package dev.lunarcoffee.airchan.views.templates

import dev.lunarcoffee.airchan.model.Board
import io.ktor.html.*
import kotlinx.html.*

internal class HeaderTemplate(private val pageTitle: String) : Template<HTML> {
    val innerContent = Placeholder<HtmlBlockTag>()

    override fun HTML.apply() {
        insert(DefaultStyleTemplate(pageTitle)) {
            content {
                p(classes = "header") {
                    +"["
                    a(href = "/", classes = "nodecor-a") { +"Home" }
                    +"] "

                    // Sort boards by code length, then alphabetically.
                    val sections = Board.boards.groupBy { it.section }

                    for ((_, section) in sections) {
                        val boards = section
                            .groupBy { it.code.length }
                            .map { pair -> Pair(pair.key, pair.value.sortedBy { it.code }) }
                            .sortedBy { it.first }
                            .flatMap { it.second }

                        +"["
                        // List all board codes.
                        for (board in boards.dropLast(1)) {
                            a(href = "/${board.code}", classes = "nodecor-a") { +board.code }
                            +" / "
                        }
                        // Edge case to stop an extraneous slash at the end of the list.
                        val lastCode = boards.last().code
                        a(href = "/$lastCode", classes = "nodecor-a") { +lastCode }
                        +"] "
                    }
                }
                insert(innerContent)
            }
        }
    }
}
