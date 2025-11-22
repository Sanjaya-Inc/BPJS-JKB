package io.healthkathon.jkb.chatbot.data

import io.healthkathon.jkb.chatbot.data.model.AnswerData
import io.healthkathon.jkb.chatbot.data.model.Question
import kotlinx.coroutines.delay

class ChatMockedRemoteApi : ChatbotRemoteApi {
    override suspend fun ask(question: Question): AnswerData {
        delay(3000)
        return AnswerData(
            answer = generateBotResponse(question.question),
            status = "success"
        )
    }

    private fun generateBotResponse(userMessage: String): String {
        // Simulate API-style response format
        return "Jumlah klaim untuk penyakit serangan jantung " +
            "(heart attack/myocardial infarction) adalah **4**.\n\n" +
            "Penjelasan:\n" +
            "1. Kami mencari semua klaim yang dikodekan sebagai diagnosis \"serangan jantung\" " +
            "menggunakan relasi CODED_AS dari node Claim ke node Diagnosis\n" +
            "2. Filter dilakukan berdasarkan nama diagnosis yang mengandung kata kunci " +
            "\"Heart Attack\" atau \"Myocardial Infarction\"\n" +
            "3. Hasil menunjukkan terdapat 4 klaim yang memenuhi kriteria ini dalam database"
    }
}
