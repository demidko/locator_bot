/**
 * Хранилище данных для сотрудников
 */
import Dialog.Unknown
import Dialog.WaitingForName
import org.mapdb.DB
import org.mapdb.HTreeMap
import org.mapdb.Serializer.LONG

/**
 * Хранилище данных о сотрудниках в Telegram. Свои данные в нем может изменить любой сотрудник
 */
typealias EmployeesStorage = HTreeMap<TelegramId, Employee>

/**
 * Открыть хранилище (id -> сотрудник) в базе данных
 */
@Suppress("UNCHECKED_CAST")
fun DB.openEmployeesStorage(name: String) = hashMap(name, LONG, EmployeeSerializer).createOrOpen() as EmployeesStorage

/**
 * Подождать пока сотрудник внесет свое имя
 */
fun EmployeesStorage.waitForName(id: TelegramId) {
  this[id] = this[id]?.copy(dialog = WaitingForName) ?: Employee(dialog = WaitingForName)
}

/**
 * Внести новое имя сотрудника
 */
fun EmployeesStorage.saveName(id: TelegramId, name: String): Employee {
  val employee = this[id]?.copy(name = name, dialog = Unknown) ?: Employee(name)
  this[id] = employee
  return employee
}

/**
 * Внести новый статус сотрудника
 * @return все имеющиеся данные о сотруднике
 */
fun EmployeesStorage.saveStatus(id: TelegramId, status: String): Employee {
  val employee = this[id]?.copy(status = status, date = vladivostokToday()) ?: Employee(status = status)
  this[id] = employee
  return employee
}

/**
 * Очистить статус сотрудника
 */
fun EmployeesStorage.clearStatus(id: TelegramId) {
  this[id] = this[id]?.copy(status = "") ?: Employee(status = "")
}

/**
 * @return актуальный отчет о статусе всех сотрудников
 */
fun EmployeesStorage.report() =
  values.filterNotNull().filter(Employee::hasActualStatus).joinToString(separator = "\n")