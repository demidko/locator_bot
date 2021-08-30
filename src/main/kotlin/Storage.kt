/**
 * Хранилище данных для сотрудников
 */

import Dialog.Unknown
import Dialog.WaitingForName
import org.mapdb.DB
import org.mapdb.Serializer.LONG
import java.util.concurrent.ConcurrentMap

/**
 * Хранилище данных о сотрудниках в Telegram. Свои данные в нем может изменить любой сотрудник
 */
typealias Storage = ConcurrentMap<TelegramId, Employee>

/**
 * Открыть хранилище в базе данных
 */
@Suppress("UNCHECKED_CAST")
fun DB.openStorage(name: String) = hashMap(name, LONG, EmployeeSerializer).createOrOpen() as Storage

/**
 * Подождать пока сотрудник внесет свое имя
 */
fun Storage.waitForName(id: TelegramId) {
  this[id] = this[id]?.copy(dialog = WaitingForName) ?: Employee(dialog = WaitingForName)
}

/**
 * Внести новое имя сотрудника
 */
fun Storage.saveName(id: TelegramId, name: String): Employee {
  val employee = this[id]?.copy(name = name, dialog = Unknown) ?: Employee(name)
  this[id] = employee
  return employee
}

/**
 * Внести новый статус сотрудника
 * @return все имеющиеся данные о сотруднике
 */
fun Storage.saveStatus(id: TelegramId, status: String): Employee {
  val employee = this[id]?.copy(status = status) ?: Employee(status = status)
  this[id] = employee
  return employee
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