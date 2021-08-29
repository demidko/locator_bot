/**
 * Предметная область для сообщений
 */

import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.Message

/**
 * Идентификатор чата в котором получено сообщение
 */
val Message.chatId get() = fromId(chat.id)

/**
 * Идентификатор сотрудника, от которого было получено сообщение
 */
val Message.authorId get() = from?.id