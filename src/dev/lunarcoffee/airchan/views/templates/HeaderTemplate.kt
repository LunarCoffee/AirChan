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
                    +"] ["
                    // List all board codes.
                    for (board in Board.boards.dropLast(1)) {
                        a(href = "/${board.code}", classes = "nodecor-a") { +board.code }
                        +" / "
                    }
                    // Edge case to stop an extraneous slash at the end of the list.
                    val lastCode = Board.boards.last().code
                    a(href = "/$lastCode", classes = "nodecor-a") { +lastCode }
                    +"]"
                }
                insert(innerContent)
            }
        }
    }
}
