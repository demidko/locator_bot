/**
 * Здесь мы описываем все взаимодействия локатора с сотрудниками
 */
import Employee.Dialog.WaitingForName
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message

/**
 * Подождать пока сотрудник введет свое имя
 * @param m сообщение содержит метаданные о сотруднике
 */
fun Bot.waitForName(m: Message, storage: Storage) {
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
fun Bot.saveStatus(m: Message, storage: Storage) {
  val employeeId = m.authorId ?: return
  val status = m.text ?: return
  val needForName = storage.saveStatus(employeeId, status)
  sendMessage(m.chatId, "Твой новый статус успешно сохранён.", replyToMessageId = m.messageId)
  if (needForName) {
    waitForName(m, storage)
  }

}

/**
 * Очистить статус сотрудника
 * @param m сообщение содержит метаданные о сотруднике
 */
fun Bot.clearStatus(m: Message, storage: Storage) {
  val employeeId = m.authorId ?: return
  storage.clearStatus(employeeId)
  sendMessage(m.chatId, "Твой статус успешно вычеркнут из списка", replyToMessageId = m.messageId)
}

/**
 * Продолжить диалог с нового сообщения пользователя
 */
fun Bot.resumeDialog(m: Message, storage: Storage) {
  val employee = m.authorId ?: return
  when (storage[employee]?.dialog) {
    /**
     * Если мы до этого ждали имя, то сохраняем его
     */
    WaitingForName -> saveName(m, storage)
    /**
     * По умолчанию, считаем что пользователь отправил нам статус
     */
    else -> saveStatus(m, storage)
  }
}

/**
 * Показать полный отчет за сегодня
 * @param m сообщение содержит метаданные сотрудника, чтобы мы поняли, кому именно показать отчет
 */
fun Bot.sendReport(m: Message, storage: Storage, calendar: GoogleCalendar) {
  sendMessage(m.chatId, storage.statusReport(), replyToMessageId = m.messageId)
}

/**
 * Сохранить новое имя сотрудника
 * @param m сообщение содержит имя сотрудника
 */
fun Bot.saveName(m: Message, storage: Storage) {
  val employeeId = m.authorId ?: return
  val name = m.text ?: return
  storage.saveName(employeeId, name)
  sendMessage(m.chatId, "Теперь коллеги будут знать что ты $name", replyToMessageId = m.messageId)
}

/**
 * Сообщить всем сотрудникам об обновлении статуса
 * @param employees список все
 */
fun Bot.notifyAll(storage: Storage, about: Employee) {

}