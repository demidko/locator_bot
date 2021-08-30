import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.extensions.filters.Filter.Text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.mapdb.DBMaker.fileDB
import java.lang.System.getenv

fun main() {
  val calendar = GoogleCalendar(getenv("CALENDAR_URLS").split(","))
  val database = fileDB(getenv("DB_PATH")).fileMmapEnableIfSupported().make()
  val storage = database.openStorage("employees")
  bot {
    logLevel = Error
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
      message(Text) {
        bot.resumeDialog(message, storage)
      }
    }
  }.startPolling()

}


