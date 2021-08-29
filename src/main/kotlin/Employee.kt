import Employee.Dialog.Unknown
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
   * Состояние диалога с сотрудником в данный момент
   */
  enum class Dialog {

    /**
     * Состояние диалога не представляет для нас значения
     */
    Unknown,

    /**
     * Ждем пока сотрудник внесет свое имя.
     * Обычно, в этом состоянии диалога, уже известны другие данные, например, статус.
     */
    WaitingForName
  }

  /**
   * Есть ли у сотрудника актуальный статус на сегодня?
   */
  val hasActualStatus get() = date <= today() && name.isNotEmpty() && status.isNotEmpty()

  /**
   * Читаемый вывод статуса сотрудника в виде строки
   */
  override fun toString() = "$name — $status (до $date включительно)"
}