package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimFeedbackRequest(
    @SerialName("claim_id")
    val claimId: String,
    @SerialName("feedback_type")
    val feedbackType: String
)

@Serializable
data class ActorFeedbackRequest(
    @SerialName("actor_type")
    val actorType: String,
    @SerialName("actor_id")
    val actorId: String,
    @SerialName("feedback_type")
    val feedbackType: String
)

@Serializable
data class FeedbackResponse(
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String
)
