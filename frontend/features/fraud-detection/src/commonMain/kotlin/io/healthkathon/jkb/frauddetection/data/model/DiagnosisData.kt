package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiagnosisData(
    @SerialName("diagnosis_id")
    val diagnosisId: String,
    @SerialName("icd10_code")
    val icd10Code: String,
    @SerialName("name")
    val name: String,
    @SerialName("avg_cost")
    val avgCost: Double,
    @SerialName("severity_level")
    val severityLevel: String
)
