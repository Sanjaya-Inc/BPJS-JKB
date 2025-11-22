package io.healthkathon.jkb.chatbot.domain

import io.healthkathon.jkb.chatbot.data.ChatbotRemoteApi
import io.healthkathon.jkb.chatbot.data.model.Question
import org.koin.core.annotation.Single

@Single
class ChatbotRepository(
    private val chatbotRemoteApi: ChatbotRemoteApi
) {
    suspend fun ask(question: String): Result<String> {
        return chatbotRemoteApi.runCatching {
            ask(Question(question)).answer.orEmpty()
        }
    }
}
