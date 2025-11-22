package io.healthkathon.jkb.chatbot.presentation

import io.healthkathon.jkb.chatbot.domain.ChatbotRepository
import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class ChatbotUiState(
    val messages: PersistentList<ChatMessage> = persistentListOf(),
    val isTyping: Boolean = false
)

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: String
)

sealed interface ChatbotIntent {
    data class SendMessage(val message: String) : ChatbotIntent
    data object ClearChat : ChatbotIntent
}

@KoinViewModel
class ChatbotViewModel(
    private val chatbotRepository: ChatbotRepository
) : BaseViewModel<ChatbotUiState, Unit>(
    initialState = ChatbotUiState()
) {
    override fun onIntent(intent: Any) {
        when (intent) {
            is ChatbotIntent.SendMessage -> sendMessage(intent.message)
            is ChatbotIntent.ClearChat -> clearChat()
        }
    }

    private fun sendMessage(message: String) = intent {
        val userMessage = ChatMessage(
            content = message,
            isUser = true,
            timestamp = getCurrentTime()
        )

        reduce {
            state.copy(
                messages = (state.messages + userMessage).toPersistentList(),
                isTyping = true
            )
        }

        chatbotRepository.ask(message).onFailure {
            val errorMessage = ChatMessage(
                content = "⚠️ Maaf, terjadi kesalahan saat menghubungi server. Silakan coba lagi.",
                isUser = false,
                timestamp = getCurrentTime()
            )
            reduce {
                state.copy(
                    messages = (state.messages + errorMessage).toPersistentList(),
                    isTyping = false
                )
            }
        }.onSuccess { botResponse ->
            val botMessage = ChatMessage(
                content = botResponse,
                isUser = false,
                timestamp = getCurrentTime()
            )
            reduce {
                state.copy(
                    messages = (state.messages + botMessage).toPersistentList(),
                    isTyping = false
                )
            }
        }
    }

    private fun clearChat() = intent {
        reduce { state.copy(messages = persistentListOf()) }
    }

    @OptIn(ExperimentalTime::class)
    private fun getCurrentTime(): String {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.hour.toString().padStart(2, '0')}:${
            localDateTime.minute.toString().padStart(2, '0')
        }"
    }
}
