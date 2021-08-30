import Dialog.valueOf
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import org.mapdb.Serializer
import java.time.LocalDate

object EmployeeSerializer : Serializer<Employee> {

  override fun serialize(out: DataOutput2, value: Employee) {
    out.writeUTF(value.name)
    out.writeUTF(value.status)
    out.writeUTF(value.dialog.toString())
    out.writeLong(value.date.toEpochDay())
  }

  override fun deserialize(input: DataInput2, available: Int): Employee {
    val name = input.readUTF()
    val status = input.readUTF()
    val dialog = valueOf(input.readUTF())
    val date = LocalDate.ofEpochDay(input.readLong())
    return Employee(name, status, dialog, date)
  }
}