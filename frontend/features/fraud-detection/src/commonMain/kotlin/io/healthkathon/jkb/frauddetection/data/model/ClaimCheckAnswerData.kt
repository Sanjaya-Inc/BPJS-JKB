package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ClaimCheckAnswerData(
    @SerialName("claim_id")
    val claimId: String,
    @SerialName("validation_result")
    val validationResult: String,
    @SerialName("confidence_score")
    val confidenceScore: Int,
    @SerialName("detail_claim_data")
    val detailClaimData: JsonObject? = null,
    @SerialName("explanation")
    val explanation: String,
    @SerialName("status")
    val status: String
)
