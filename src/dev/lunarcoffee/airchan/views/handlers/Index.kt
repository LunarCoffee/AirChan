package dev.lunarcoffee.airchan.views.handlers

import dev.lunarcoffee.airchan.formatter
import dev.lunarcoffee.airchan.model.Board
import dev.lunarcoffee.airchan.services.noImageFile
import dev.lunarcoffee.airchan.views.templates.DefaultStyleTemplate
import io.ktor.application.call
import io.ktor.html.respondHtmlTemplate
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.html.*
import java.time.LocalDateTime

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
                    // Lists the last ten threads which have been posted to.
                    div(classes = "card") {
                        p(classes = "card-header") { +"Recent Threads" }
                        div(classes = "card-content") {
                            style = "padding-top:20px;padding-bottom:16px;"

                            val threads = Board
                                .boards
                                .flatMap { it.threads }
                                .associateWith {
                                    LocalDateTime.parse(it.posts.last().created, formatter)
                                }
                                .toList()
                                .sortedBy { it.second }
                                .map { it.first }
                                .take(10)
                                .reversed()

                            for (thread in threads) {
                                a(href = "/${thread.code}/${thread.id}", classes = "nodecor-a") {
                                    style = "text-decoration:underline;"
                                    +thread.id.toString()
                                }
                                +" in "
                                a(href = "/${thread.code}", classes = "nodecor-a") {
                                    style = "text-decoration:underline;"
                                    +"/${thread.code}/"
                                }

                                val posts = thread.posts.size - 2
                                val images = thread.posts.sumBy {
                                    if (it.images.firstOrNull()?.matches(noImageFile) == true) {
                                        0
                                    } else {
                                        it.images.size
                                    }
                                }

                                val replySOP = if (posts == 1) "reply" else "replies"
                                val imageSOP = if (images == 1) "image" else "images"

                                +" with $posts $replySOP and $images $imageSOP. "
                                +"Last reply ${thread.posts.last().created}."
                                br()
                            }
                        }
                    }
                }
            }
        }
    }
}
