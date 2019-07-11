package dev.lunarcoffee.airchan.views.templates

import dev.lunarcoffee.airchan.emsp
import dev.lunarcoffee.airchan.model.Board
import dev.lunarcoffee.airchan.services.showImages
import io.ktor.html.Template
import io.ktor.html.insert
import kotlinx.html.*

internal class BoardViewTemplate(private val board: Board) : Template<HTML> {
    override fun HTML.apply() {
        insert(HeaderTemplate("/${board.code}/ - ${board.name} - AirChan")) {
            innerContent {
                div(classes = "center") {
                    img(alt = "Header", src = "/files/header.png")
                    h1 { +"/${board.code}/ - ${board.name}" }
                    hr { style = "width:91%;margin-bottom:10px;" }
                    form(
                        action = "/${board.code}/p",
                        encType = FormEncType.multipartFormData,
                        method = FormMethod.post,
                        classes = "no-center"
                    ) {
                        input(type = InputType.text, name = "subject") { placeholder = "Subject" }
                        br()
                        textArea(rows = "4", cols = "32") {
                            name = "comment"
                            placeholder = "Type here..."
                        }
                        br()
                        input(type = InputType.file, name = "file")
                        br()
                        input(type = InputType.submit) {
                            style = "margin-top:4px;"
                            value = "New Thread"
                        }
                        span {
                            style = "font-size:10px;color:red;"
                            +"$emsp* Only a comment is required!"
                        }
                    }
                }
                ul(classes = "posts") {
                    for (thread in board.threads) {
                        val strippedId = thread
                            .posts[1]
                            .images
                            .firstOrNull()
                            ?.replace("""\s+""".toRegex(), "")
                        val minMaxClass = "1$strippedId"

                        hr(classes = "thread-sep")
                        // Show OP's first post that started the thread without a card.
                        p(classes = "post-content") {
                            val opPost = thread.posts[1]
                            div(classes = "op-image") {
                                showImages(opPost.images, true)
                            }

                            p(classes = "post-text first-post-text") {
                                // Post information header.
                                b(classes = "subject") { +thread.posts[0].text }
                                +" "
                                b(classes = "green") { +"Anonymous " }
                                i { +thread.created }
                                span(classes = "id") {
                                    +" T${thread.id} $${opPost.id} A${opPost.authorId}"
                                }
                                +"$emsp["
                                a(href = "/${board.code}/${thread.id}", classes = "reply-a") {
                                    +"Reply"
                                }
                                +"]"
                                p { style = "font-size:11px;" }

                                // Actual post content.
                                p(classes = minMaxClass) {
                                    for (line in opPost.text.lines()) {
                                        +line
                                        br()
                                    }
                                }
                            }
                        }
                        // Show the most recent five posts.
                        li(classes = "post $minMaxClass") {
                            // If there are hidden replies then show a message about it.
                            val count = thread.posts.size
                            if (count > 7) {
                                val hiddenPosts = count - 7
                                val hiddenImages = thread
                                    .posts
                                    .slice(2 until count - 5)
                                    .sumBy { it.images.size }

                                val replySOP = if (hiddenPosts == 1) "reply" else "replies"
                                val imageSOP = if (hiddenImages == 1) "image" else "images"

                                p(classes = "id") {
                                    style = "padding-left:20px;"
                                    +"$hiddenPosts $replySOP and $hiddenImages $imageSOP omitted. "
                                    a(href = "/${board.code}/${thread.id}") { +"Click here" }
                                    +" to view."
                                }
                            }
                            for (post in thread.posts.drop(2).takeLast(5)) {
                                div(classes = "left-ar") {
                                    style = "float:left;"
                                    p { +">>" }
                                }
                                div(classes = "post-card width-child") {
                                    div(classes = "card-content") {
                                        p(classes = "post-info") {
                                            b(classes = "green") { +"Anonymous " }
                                            i { +post.created }
                                            span(classes = "id") {
                                                +" $${post.id} A${post.authorId}"
                                            }
                                        }
                                        div(classes = "post-content") {
                                            showImages(post.images)
                                            // Actual post content.
                                            p(classes = "post-text") {
                                                for (line in post.text.lines()) {
                                                    +line
                                                    br()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                hr()
            }
        }
    }
}
