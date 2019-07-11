import views.handlers.handleBoards
import views.handlers.handleIndex
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import org.slf4j.event.Level
import java.io.File

internal fun main(args: Array<String>) {
    fixVoodooBugs()
    EngineMain.main(args)
}

@Suppress("unused")
internal fun Application.module() {
    install(AutoHeadResponse)
    install(CallLogging) { level = Level.INFO }
    install(CORS)

    install(CachingHeaders) {
        // Don't cache vectors.
        val imageCacheTypes = ContentType.Image.run { setOf(PNG, JPEG, GIF) }

        options {
            // Cache images to reduce flickering, especially in the background tile image.
            if (it.contentType in imageCacheTypes) {
                CachingOptions(CacheControl.MaxAge(2_678_400))
            } else {
                null
            }
        }
    }

    install(StatusPages) {
        mapOf(
            HttpStatusCode.NotFound to "We couldn't find that content.",
            HttpStatusCode.InternalServerError to "Something went very wrong on our end."
        ).forEach { (code, message) -> status(code) { call.respondText(message) } }
    }

    routing {
        static {
            staticRootFolder = File("resources/static")
            file("favicon.ico", "files/favicon.ico")

            static("css") { files("css") }
            static("js") { files("js") }
            static("files") { files("files") }
        }

        handleIndex()
        handleBoards()
    }
}
