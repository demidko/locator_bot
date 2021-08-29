import java.time.LocalDate.now
import java.time.ZoneId.of

/**
 * @return текущая дата во Владивостоке
 */
fun today() = now(of("Asia/Vladivostok"))