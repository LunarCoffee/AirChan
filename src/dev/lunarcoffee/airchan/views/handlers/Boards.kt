package views.handlers

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Board
import model.Thread
import services.*
import views.templates.BoardViewTemplate
import views.templates.ThreadViewTemplate
import java.nio.file.Files
import java.nio.file.Paths

private val noImageIdGen = IdGenerator("NoImage")

internal fun Routing.handleBoards() {
    route("/{code}") {
        get {
            val board = call.getBoard() ?: return@get call.notFound()
            call.respondHtmlTemplate(BoardViewTemplate(board)) {}
        }

        // Creates a new thread with a post.
        post("/p") {
            val form = call.receiveMultipartForm()
            val file = form.file?.name
                ?: withContext(Dispatchers.IO) {
                    Files.copy(
                        Paths.get("$UPLOAD_DIR/0-no-image.png"),
                        Paths.get("$UPLOAD_DIR/${noImageIdGen.next()}-no-image.png")
                    ).toFile().name
                }

            val subject = form["subject"]!!.trim().ifEmpty { " " }
            val comment = form["comment"]!!.trim()
            if (subject.length !in 1..100 || comment.length !in 1..1000) {
                return@post call.backToBoard()
            }

            val board = call.getBoard() ?: return@post call.backToBoard()
            if (subject.isNotEmpty() && comment.isNotBlank()) {
                board.createThread(subject)
                board
                    .threads
                    .last()
                    .createPost(comment, listOf(file), call.request.origin.remoteHost)
            }

            // Send them back to the board page.
            call.backToBoard()
        }

        route("/{threadId}") {
            get {
                val thread = call.getThread() ?: return@get call.notFound()
                call.respondHtmlTemplate(ThreadViewTemplate(call.getBoard()!!, thread)) {}
            }

            post("/p") {
                val form = call.receiveMultipartForm()
                val files = if (form.file == null) emptyList() else listOf(form.file.name)

                val comment = form["comment"]!!.trim()
                if (comment.length !in 1..1000) {
                    return@post call.backToThread()
                }

                val thread = call.getThread() ?: return@post call.backToThread()
                if (comment.isNotBlank()) {
                    thread.createPost(comment, files, call.request.origin.remoteHost)
                }

                // Send them back to the thread page.
                call.backToThread()
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

private suspend fun ApplicationCall.backToThread() {
    respondRedirect("/${getBoard()!!.code}/${parameters["threadId"]}")
}

private suspend fun ApplicationCall.backToBoard() = respondRedirect("/${getBoard()!!.code}")
