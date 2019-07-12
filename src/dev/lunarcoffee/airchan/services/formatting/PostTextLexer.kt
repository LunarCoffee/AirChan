package services.formatting

internal class PostTextLexer(val text: String) {
    private var pos = 0
    private var curChar: String? = text[0].toString()

    fun lex(): List<Token> {
        return mutableListOf<Token>().apply {
            while (true) {
                add(getNextToken() ?: return@apply)
            }
        }
    }

    private fun getNextToken(): Token? {
        while (curChar != null) {
            return when (curChar) {
                "\n" -> {
                    advance()
                    Newline()
                }
                "<" -> {
                    val red = consume("<")
                    if (red.length == 1) Red(red + consume("[^\n]")) else Text(red)
                }
                ">" -> {
                    val mention = consume(">")
                    when (mention.length) {
                        1 -> Quote(mention + consume("[^\n]"))
                        2 -> PostMention(mention + consume("""\d"""))
                        else -> Text(mention)
                    }
                }
                else -> {
                    val token = if (curChar == null) null else Text(curChar!!)
                    advance()
                    token
                }
            }
        }
        return null
    }

    private fun consume(c: String): String {
        val regex = c.toRegex()
        var res = ""
        while (curChar != null && curChar!!.matches(regex)) {
            res += curChar
            advance()
        }
        return res
    }

    private fun advance() {
        pos++
        curChar = if (pos > text.length - 1) null else text[pos].toString()
    }
}
