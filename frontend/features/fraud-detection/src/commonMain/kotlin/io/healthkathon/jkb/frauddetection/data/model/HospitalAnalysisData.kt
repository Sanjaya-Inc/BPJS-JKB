package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HospitalAnalysisData(
    @SerialName("diagnosis_id")
    val diagnosisId: String,
    @SerialName("diagnosis_name")
    val diagnosisName: String,
    @SerialName("total_claims")
    val totalClaims: Int,
    @SerialName("z_score")
    val zScore: Double
)
