package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimsResponse(
    @SerialName("data")
    val data: List<ClaimData?>?
)
