package views.templates

import io.ktor.html.*
import kotlinx.html.*

internal open class DefaultStyleTemplate(private val pageTitle: String) : Template<HTML> {
    val content = Placeholder<HtmlBlockTag>()

    override fun HTML.apply() {
        head {
            meta(charset = "UTF-8")
            title(pageTitle)
            link(href = "/css/style.css", rel = "stylesheet")
        }
        body {
            insert(content)
            // Footer to state developer and legal notices.
            div(classes = "center") {
                p {
                    +"© 2019 "
                    a(
                        href = "https://github.com/LunarCoffee",
                        target = "_blank",
                        classes = "reply-a"
                    ) { +"LunarCoffee" }
                    +". The code is open source under the "
                    a(
                        href = "https://opensource.org/licenses/MIT",
                        target = "_blank",
                        classes = "reply-a"
                    ) { +"MIT" }
                    +" license."
                    br()
                    +"Posts on AirChan are not the responsibility of any site "
                    +"administration, but of the posters themselves."
                }
            }
        }
    }
}
