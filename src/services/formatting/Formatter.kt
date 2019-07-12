package services.formatting

import kotlinx.html.*
import model.Thread

internal fun HtmlBlockTag.formatPostText(text: String, thread: Thread) {
    val tokens = PostTextLexer(text).lex()
    for (token in tokens) {
        when (token) {
            is Text -> +token.content
            is PostMention -> a(classes = "post-reply-a") {
                val rawId = token.content.drop(2)
                val isOp = rawId.toLong() == thread.posts[1].id

                if (!isOp) {
                    onMouseOver = """
                        var e = document.getElementById("p-$rawId");
                        e.style.background = "#f0c0b0";
                        e.style.borderRight = "1px solid #d99f91";
                        e.style.borderBottom = "1px solid #d99f91";
                    """
                    onMouseOut = """
                        var e = document.getElementById("p-$rawId");
                        e.style.background = "#f0e0d6";
                        e.style.borderRight = "1px solid #d9bfb7";
                        e.style.borderBottom = "1px solid #d9bfb7";
                    """
                }

                +token.content
                if (isOp) {
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
