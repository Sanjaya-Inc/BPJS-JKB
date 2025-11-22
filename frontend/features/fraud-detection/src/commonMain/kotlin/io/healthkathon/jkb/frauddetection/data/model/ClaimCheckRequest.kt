package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimCheckRequest(
    @SerialName("claimId")
    val claimId: String
)
