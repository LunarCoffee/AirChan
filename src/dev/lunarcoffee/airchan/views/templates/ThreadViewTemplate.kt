package dev.lunarcoffee.airchan.views.templates

import dev.lunarcoffee.airchan.model.Board
import dev.lunarcoffee.airchan.model.Thread
import dev.lunarcoffee.airchan.services.showImages
import io.ktor.html.Template
import io.ktor.html.insert
import kotlinx.html.*

internal class ThreadViewTemplate(
    private val board: Board,
    private val thread: Thread
) : Template<HTML> {

    override fun HTML.apply() {
        insert(HeaderTemplate("/${board.code}/ - #${thread.id} - AirChan")) {
            innerContent {
                div(classes = "center") {
                    img(alt = "Header", src = "/files/header.png")
                    h1 { +"/${board.code}/ - ${board.name}" }
                    hr { style = "width:91%;margin-bottom:-9px;" }
                    form(
                        action = "/${board.code}/${thread.id}/p",
                        encType = FormEncType.multipartFormData,
                        method = FormMethod.post,
                        classes = "no-center"
                    ) {
                        br()
                        textArea(rows = "4", cols = "32") {
                            name = "comment"
                            placeholder = "Type here..."
                        }
                        br()
                        input(type = InputType.file, name = "file")
                        br()
                        input(type = InputType.submit) {
                            style = "margin-top:4px;margin-bottom:6px;"
                            value = "Post"
                        }
                    }
                }
                hr(classes = "thread-sep")
                // Show OP's first post that started the threat without a card.
                p(classes = "post-content") {
                    val opPost = thread.posts[1]
                    div(classes = "op-image") {
                        showImages(opPost.images)
                    }

                    p(classes = "post-text first-post-text") {
                        // Post information header.
                        b(classes = "subject") { +thread.posts[0].text }
                        +" "
                        b(classes = "green") { +"Anonymous " }
                        i { +thread.created }
                        span(classes = "id") {
                            +" T${thread.id} $"
                            a(classes = "reply-id-a") { +opPost.id.toString() }
                            +" A${opPost.authorId}"
                        }
                        p { style = "font-size:11px;" }

                        // Actual post content.
                        p {
                            for (line in opPost.text.lines()) {
                                +line
                                br()
                            }
                        }
                    }
                }
                // Show the most recent five posts.
                div(classes = "post") {
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
                                        +" $"
                                        a(href = "#", classes = "reply-id-a") {
                                            onClick = """
                                                var e = document.getElementsByTagName("textarea");
                                                e = e[0];
                                                e.value = ">>${post.id}\n" + e.value;                                                  
                                            """
                                            +post.id.toString()
                                        }
                                        +" A${post.authorId}"
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
                hr()
            }
        }
    }
}
