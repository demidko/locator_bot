import Dialog.Unknown
import java.time.LocalDate

/**
 * Все имеющиеся на руках сведения о сотруднике в данный момент
 * @param name имя сообщенное сотрудником о себе
 * @param status статус сообщенный сотрудником о себе
 * @param dialog состояние диалога с сотрудником
 * @param date дата, включительно до которой, статус будет актуален
 */
data class Employee(
  val name: String = "",
  val status: String = "",
  val dialog: Dialog = Unknown,
  val date: LocalDate = today()
) {

  /**
   * Есть ли у сотрудника актуальный статус на сегодня?
   */
  val hasActualStatus get() = today() <= date && name.isNotEmpty() && status.isNotEmpty()

  /**
   * Читаемый вывод статуса сотрудника в виде строки
   */
  override fun toString() = "$name — $status (до $date включительно)"
}