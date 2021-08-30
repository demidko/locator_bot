import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId

/**
 * Идентификатор сотрудника в Telegram
 */
typealias TelegramId = Long

/**
 * Идентификатор чата с сотрудником
 */
val TelegramId.chatId get() = fromId(this)