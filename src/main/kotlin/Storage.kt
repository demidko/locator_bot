/**
 * Хранилище данных для сотрудников
 */

import Employee.Dialog.Unknown
import Employee.Dialog.WaitingForName
import org.mapdb.DB
import java.util.concurrent.ConcurrentMap

/**
 * Идентификатор сотрудника в Telegram
 */
typealias TelegramId = Long

/**
 * Хранилище данных о сотрудниках в Telegram. Свои данные в нем может изменить любой сотрудник
 */
typealias Storage = ConcurrentMap<TelegramId, Employee>

/**
 * Открыть хранилище в базе данных
 */
@Suppress("UNCHECKED_CAST")
fun DB.openStorage(name: String) = hashMap(name).createOrOpen() as Storage

/**
 * Подождать пока сотрудник внесет свое имя
 */
fun Storage.waitForName(id: TelegramId) {
  this[id] = this[id]?.copy(dialog = WaitingForName) ?: Employee(dialog = WaitingForName)
}

/**
 * Внести новое имя сотрудника
 */
fun Storage.saveName(id: TelegramId, name: String) {
  this[id] = this[id]?.copy(name = name, dialog = Unknown) ?: Employee(name)
}

/**
 * Внести новый статус сотрудника
 * @return true если все хорошо, false, если статус нужно дополнить именем
 */
fun Storage.saveStatus(id: TelegramId, status: String): Boolean {
  val employee = this[id]?.copy(status = status) ?: Employee(status = status)
  this[id] = employee
  return employee.name.isNotEmpty()
}

/**
 * Очистить статус сотрудника
 */
fun Storage.clearStatus(id: TelegramId) {
  this[id] = this[id]?.copy(status = "") ?: Employee(status = "")
}

/**
 * @return актуальный отчет о статусе всех сотрудников
 */
fun Storage.statusReport() = values.filter(Employee::hasActualStatus).joinToString(separator = "\n")