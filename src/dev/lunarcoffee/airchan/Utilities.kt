import com.google.gson.Gson
import services.IdGenerator
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Wide em space.
const val emsp = "\u2001"
internal val gson = Gson()

internal fun fixVoodooBugs() {
    val idFile = File("resources/id.txt")
    val lines = idFile.readLines()
    val actualLatestPostId = lines.last().substringAfterLast(" ")

    idFile.writeText("Post $actualLatestPostId\n${lines.drop(1).joinToString("\n")}")
    IdGenerator("Post")
}

internal val formatter = DateTimeFormatter.ofPattern("(E) dd/MM/yyyy kk:mm:ss 'EST'")
internal fun LocalDateTime.formatDefault() = format(formatter)!!
