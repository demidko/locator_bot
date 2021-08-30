import Dialog.Unknown
import Dialog.WaitingForName
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.apache.commons.lang3.RandomStringUtils.random
import org.junit.jupiter.api.RepeatedTest
import org.mapdb.DBMaker.fileDB
import java.time.LocalDate.now
import java.time.LocalDate.ofEpochDay
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

class StorageTest {

  @RepeatedTest(10)
  fun serializationTest() {
    val originalEmployees = randomEmployees()
    val dbName = "employees.test"
    val storageName = "employees"

    fun saveEmployeesToDb() {
      fileDB(dbName).fileMmapEnableIfSupported().make().use {
        val storage = it.openEmployeesStorage(storageName)
        for ((id, employee) in originalEmployees) {
          storage[id] = employee
        }
      }
    }

    fun restoreEmployeesFromDb() {
      fileDB(dbName).fileMmapEnableIfSupported().make().use {
        val storage = it.openEmployeesStorage(storageName)
        for ((id, originalEmployee) in originalEmployees) {
          assertThat(storage[id]).isEqualTo(originalEmployee)
        }
        storage.clear()
      }
    }

    saveEmployeesToDb()
    restoreEmployeesFromDb()
  }


  private fun randomEmployees() =
    (0..nextInt(1, 100))
      .associate { nextLong() to randomEmployee() }

  private fun randomEmployee() =
    Employee(
      random(nextInt(0, 120)),
      random(nextInt(0, 120)),
      ofEpochDay(nextLong(0, now().toEpochDay())),
      arrayOf(Unknown, WaitingForName)[nextInt(0, 2)]
    )
}