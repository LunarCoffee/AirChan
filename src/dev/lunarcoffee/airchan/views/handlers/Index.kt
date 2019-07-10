package dev.lunarcoffee.airchan.views.handlers

import dev.lunarcoffee.airchan.model.Board
import dev.lunarcoffee.airchan.views.templates.DefaultStyleTemplate
import io.ktor.application.call
import io.ktor.html.respondHtmlTemplate
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.html.*

internal fun Routing.handleIndex() {
    get("/") {
        call.respondHtmlTemplate(DefaultStyleTemplate("AirChan")) {
            content {
                div {
                    id = "home-div"
                    div {
                        style = "text-align:center;"
                        a(href = "/") {
                            img(src = "/files/airchan.png", alt = "AirChan") { id = "airchan-img" }
                        }
                    }
                    div(classes = "welcome-card") {
                        id = "card-welcome"
                        p { +"Welcome to AirChan." }
                        p {
                            +"You can easily create an image thread, post content, and converse "
                            +"anonymously. Please note that copyrighted or content illegal under "
                            +"Canadian law will be deleted. Some boards may have adult material "
                            +"or content that can be considered offensive."
                        }
                    }
                    // List major boards and codes.
                    div(classes = "card") {
                        p(classes = "card-header") { +"Boards" }
                        div(classes = "card-content") {
                            for ((section, boards) in Board.boards.groupBy { it.section }) {
                                ul(classes = "board-list") {
                                    li(classes = "board-li") { +section }
                                    for (board in boards) {
                                        li {
                                            a(href = "/${board.code}", classes = "nodecor-a") {
                                                +"${board.name} - /${board.code}/"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
