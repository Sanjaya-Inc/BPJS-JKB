package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimCheckAnswerData(
    @SerialName("answer")
    val answer: String?,
    @SerialName("status")
    val status: String?
)
