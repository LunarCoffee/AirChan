package services.formatting

internal sealed class Token(val content: String)

internal class Text(content: String) : Token(content)
internal class Newline : Token("")

internal class PostMention(content: String) : Token(content)
internal class Quote(content: String) : Token(content)
internal class Red(content: String) : Token(content)
