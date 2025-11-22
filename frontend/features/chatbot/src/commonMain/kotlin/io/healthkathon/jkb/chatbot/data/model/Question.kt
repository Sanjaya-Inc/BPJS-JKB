package io.healthkathon.jkb.chatbot.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    @SerialName("question")
    val question: String
)
