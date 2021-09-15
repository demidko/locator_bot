import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.extensions.filters.Filter.Text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.mapdb.DBMaker.fileDB
import org.slf4j.LoggerFactory.getLogger
import java.lang.Runtime.getRuntime
import java.lang.System.getenv
import java.net.URL

val DATABASE_PATH = getenv("DATABASE_PATH")
val CALENDAR_URLS = getenv("CALENDAR_URLS").split(",").map(::URL)
val TELEGRAM_TOKEN = getenv("TELEGRAM_TOKEN")

fun main() {
  val log = getLogger("Locator");
  val database = fileDB(DATABASE_PATH).fileMmapEnableIfSupported().make()
  val storage = database.openEmployeesStorage("employees")
  val calendar = GoogleCalendar(CALENDAR_URLS)
  val bot = bot {
    logLevel = Error
    token = TELEGRAM_TOKEN
    dispatch {
      command("start") {
        log.info("${message.from} - ${this.message.text}")
        bot.sendReport(message, storage, calendar)
      }
      command("name") {
        log.info("${message.from} - ${this.message.text}")
        bot.waitForName(message, storage)
      }
      command("clear") {
        log.info("${message.from} - ${this.message.text}")
        bot.clearStatus(message, storage)
      }
      message(Text) {
        log.info("${message.from} - ${this.message.text}")
        bot.resumeDialog(message, storage)
      }
    }
  }
  getRuntime().addShutdownHook(Thread {
    storage.close()
    database.close()
    log.info("MapDB was successfully closed")
  })
  bot.startPolling()
  log.info("Telegram bot was successfully started")
}


