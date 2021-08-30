import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.DateProperty
import net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_PARSING
import net.fortuna.ical4j.util.CompatibilityHints.setHintEnabled
import org.slf4j.LoggerFactory.getLogger
import java.net.URL
import java.time.LocalDate
import java.time.LocalDate.ofInstant

/**
 * Данные в Google календаре доступны только для чтения и сотрудники не могут их менять
 */
class GoogleCalendar(private val urls: Collection<URL>) {

  private val log = getLogger("Google Calendar")

  /**
   * @return актуальный отчет о статусе всех сотрудников
   */
  fun report() = buildString {
    setHintEnabled(KEY_RELAXED_PARSING, true)
    for (url in urls) {
      val connection = url.openConnection().apply {
        readTimeout = 15000
        connectTimeout = 15000
      }
      connection.getInputStream().use {
        for (component in CalendarBuilder().build(it).components) {
          try {
            component.parseEmployee()?.let(this::appendLine)
          } catch (e: RuntimeException) {
            log.warn(e.message)
          }
        }
      }
    }
  }

  private fun Component.parseEmployee(): Employee? {
    if (!name.equals("VEVENT", ignoreCase = true)) {
      return null
    }
    val startDate = getProperty<DateProperty>("DTSTART").date
    val startLocal = LocalDate.ofInstant(startDate.toInstant(), vladivostokZoneId).minusDays(1)
    if (startLocal > vladivostokToday()) {
      return null
    }
    val endDate = getProperty<DateProperty>("DTEND").date
    val endLocal = ofInstant(endDate.toInstant(), vladivostokZoneId).minusDays(1)
    if (endLocal < vladivostokToday()) {
      return null
    }
    val summary = getProperty<Property>("SUMMARY").value
    if (summary.isNullOrBlank()) {
      return null
    }
    val (name, status) = summary.parseNameAndStatus()
    return Employee(name, status, endLocal)
  }

  private fun String.parseNameAndStatus(): Pair<String, String> {
    val surnameSeparator = indexOf(' ')
    val statusSeparator = indexOfAny(charArrayOf('.', ' '), surnameSeparator + 1)
    if (statusSeparator == -1) {
      return substring(0..surnameSeparator) to substring(surnameSeparator until length)
    }
    return substring(0..statusSeparator) to substring(statusSeparator until length)
  }
}