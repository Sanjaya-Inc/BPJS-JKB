package io.healthkathon.jkb.chatbot.data

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.healthkathon.jkb.chatbot.data.model.AnswerData
import io.healthkathon.jkb.chatbot.data.model.Question

interface ChatbotRemoteApi {

    @POST("chatbot/ask")
    suspend fun ask(
        @Body question: Question
    ): AnswerData
}
