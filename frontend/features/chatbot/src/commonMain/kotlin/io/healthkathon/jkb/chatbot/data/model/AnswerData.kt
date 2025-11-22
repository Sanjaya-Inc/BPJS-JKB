package io.healthkathon.jkb.chatbot.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerData(
    @SerialName("answer")
    val answer: String?,
    @SerialName("status")
    val status: String?
)
