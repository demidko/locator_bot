import Dialog.NewName
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import org.mapdb.DBMaker.fileDB
import org.slf4j.LoggerFactory.getLogger
import java.lang.System.getenv

fun main() {
  val calendar = GoogleCalendar(getenv("CALENDARS").split(","))
  val database = fileDB(getenv("DB_PATH"))
    .fileMmapEnableIfSupported()
    .make()
  database.use {
    val storage = database.openStorage("employees")
    bot {
      token = getenv("TOKEN")
      dispatch {
        command("start") {
          bot.sendReport(message, storage, calendar)
        }
        command("name") {
          bot.waitForName(message, storage)
        }
        command("clear") {
          bot.clearStatus(message, storage)
        }
        text {
          getLogger("text").info(text)

          val id = message.authorId ?: return@text
          when (states[id]) {
            NewName -> {

            }
            else -> {

            }
          }
        }

      }
    }.startPolling()
  }
}


