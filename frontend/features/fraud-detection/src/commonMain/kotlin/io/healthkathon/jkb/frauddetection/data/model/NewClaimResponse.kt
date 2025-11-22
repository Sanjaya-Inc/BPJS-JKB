package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewClaimResponse(
    @SerialName("form_data_summary")
    val formDataSummary: String,
    @SerialName("validation_result")
    val validationResult: String,
    @SerialName("confidence_score")
    val confidenceScore: Int,
    @SerialName("detail_analysis")
    val detailAnalysis: String,
    @SerialName("explanation")
    val explanation: String,
    @SerialName("status")
    val status: String
)
