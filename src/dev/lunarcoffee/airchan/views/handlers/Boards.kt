package dev.lunarcoffee.airchan.views.handlers

import dev.lunarcoffee.airchan.model.Board
import dev.lunarcoffee.airchan.model.Thread
import dev.lunarcoffee.airchan.services.receiveMultipartForm
import dev.lunarcoffee.airchan.views.templates.BoardViewTemplate
import dev.lunarcoffee.airchan.views.templates.ThreadViewTemplate
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*

internal fun Routing.handleBoards() {
    route("/{code}") {
        get {
            val board = call.getBoard() ?: return@get call.notFound()
            call.respondHtmlTemplate(BoardViewTemplate(board)) {}
        }

        // Creates a new thread with a post.
        post("/p") {
            val form = call.receiveMultipartForm()
            if (form.file == null) {
                return@post call.notFound()
            }

            val subject = form["subject"]!!
            val comment = form["comment"]!!
            if (subject.length !in 1..100 || comment.length !in 1..1000) {
                return@post call.notFound()
            }

            val board = call.getBoard() ?: return@post call.notFound()
            if (subject.isNotBlank() && comment.isNotBlank()) {
                board.createThread(subject)
                board
                    .threads
                    .last()
                    .createPost(comment, listOf(form.file.name), call.request.origin.remoteHost)
            }

            // Send them back to the board page.
            call.respondRedirect("/${board.code}")
        }

        route("/{threadId}") {
            get {
                val thread = call.getThread() ?: return@get call.notFound()
                call.respondHtmlTemplate(ThreadViewTemplate(call.getBoard()!!, thread)) {}
            }

            post("/p") {
                val form = call.receiveMultipartForm()
                val files = if (form.file == null) emptyList() else listOf(form.file.name)

                val comment = form["comment"]!!
                if (comment.length !in 1..1000) {
                    return@post call.notFound()
                }

                val thread = call.getThread() ?: return@post call.notFound()
                if (comment.isNotBlank()) {
                    thread.createPost(comment, files, call.request.origin.remoteHost)
                }

                // Send them back to the thread page.
                call.respondRedirect("/${call.getBoard()!!.code}/${call.parameters["threadId"]}")
            }
        }
    }
}

// Tries to get the board referenced in the route.
private fun ApplicationCall.getBoard() = Board.boards.find { it.code == parameters["code"] }

// Tries to get the thread in the board referenced in the route.
private fun ApplicationCall.getThread(): Thread? {
    return getBoard()?.threads?.find { it.id == parameters["threadId"]?.toLongOrNull() }
}

private suspend fun ApplicationCall.notFound() = respond(HttpStatusCode.NotFound)
