package services.formatting

import kotlinx.html.*
import model.Thread

internal fun HtmlBlockTag.formatPostText(text: String, thread: Thread) {
    val tokens = PostTextLexer(text).lex()
    for (token in tokens) {
        when (token) {
            is Text -> +token.content
            is PostMention -> a(classes = "post-reply-a") {
                +token.content
                if (token.content.drop(2).toLong() == thread.posts[1].id) {
                    +" (OP)"
                }
            }
            is Quote -> span {
                style = "color:#789922;"
                +token.content
            }
            is Red -> span {
                style = "color:#c93830;"
                +token.content
            }
            is Newline -> br()
        }
    }
}
