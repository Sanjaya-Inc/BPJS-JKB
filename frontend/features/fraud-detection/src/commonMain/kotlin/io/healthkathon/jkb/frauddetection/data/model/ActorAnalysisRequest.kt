package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActorAnalysisRequest(
    @SerialName("actorType")
    val actorType: String,
    @SerialName("actorId")
    val actorId: String
)
