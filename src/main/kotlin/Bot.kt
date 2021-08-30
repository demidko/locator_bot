/**
 * Здесь мы описываем все взаимодействия локатора с сотрудниками
 */
import Dialog.WaitingForName
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import org.slf4j.LoggerFactory.getLogger

private val log = getLogger("Telegram Bot")

/**
 * Подождать пока сотрудник введет свое имя
 * @param m сообщение содержит метаданные о сотруднике
 */
fun Bot.waitForName(m: Message, storage: EmployeesStorage) {
  val employeeId = m.authorId ?: return
  sendMessage(
    m.chatId,
    "Как запомнить твоё имя, чтобы все коллеги поняли чьи это статусы?",
    replyToMessageId = m.messageId
  )
  storage.waitForName(employeeId)
}

/**
 * Сохранить статус сотрудника
 * @param m сообщение содержит статус сотрудника
 */
fun Bot.saveStatus(m: Message, storage: EmployeesStorage) {
  val telegramId = m.authorId ?: return
  val status = m.text ?: return
  val employee = storage.saveStatus(telegramId, status)
  sendMessage(m.chatId, "Твой новый статус успешно сохранён.", replyToMessageId = m.messageId)
  if (employee.name.isEmpty()) {
    waitForName(m, storage)
  } else {
    notifyAll(storage, telegramId, employee)
  }
}

/**
 * Очистить статус сотрудника
 * @param m сообщение содержит метаданные о сотруднике
 */
fun Bot.clearStatus(m: Message, storage: EmployeesStorage) {
  val employeeId = m.authorId ?: return
  storage.clearStatus(employeeId)
  sendMessage(m.chatId, "Твой статус успешно вычеркнут из списка", replyToMessageId = m.messageId)
}

/**
 * Продолжить диалог с нового сообщения пользователя
 */
fun Bot.resumeDialog(m: Message, storage: EmployeesStorage) {
  val telegramId = m.authorId ?: return
  val employee = storage[telegramId]
  when (employee?.dialog) {
    WaitingForName -> saveName(m, storage)
    else -> saveStatus(m, storage)
  }
}

/**
 * Показать полный отчет за сегодня
 * @param m сообщение содержит метаданные сотрудника, чтобы мы поняли, кому именно показать отчет
 */
fun Bot.sendReport(m: Message, storage: EmployeesStorage, calendar: GoogleCalendar) {

  sendMessage(
    m.chatId,
    "Вот кто сам отметился сегодня:\n${storage.report()}",
    replyToMessageId = m.messageId
  )
  sendMessage(
    m.chatId,
    "А ещё у меня есть данные из Google Календаря:\n${calendar.report()}",
    replyToMessageId = m.messageId
  )
}

/**
 * Сохранить новое имя сотрудника
 * @param m сообщение содержит имя сотрудника
 */
fun Bot.saveName(m: Message, storage: EmployeesStorage) {
  val telegramId = m.authorId ?: return
  val name = m.text ?: return
  val employee = storage.saveName(telegramId, name)
  sendMessage(m.chatId, "Теперь коллеги будут знать что ты $name", replyToMessageId = m.messageId)
  if (employee.hasActualStatus) {
    notifyAll(storage, telegramId, employee)
  }
}

/**
 * Сообщить всем сотрудникам об обновлении статуса
 */
fun Bot.notifyAll(storage: EmployeesStorage, employeeId: TelegramId, employee: Employee) {
  storage.keys
    .filter { it != employeeId }
    .forEach { sendMessage(it.chatId, employee.toString()) }
}