package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaimData(
    @SerialName("claim_id")
    val claimId: String,
    @SerialName("doctor_id")
    val doctorId: String,
    @SerialName("hospital_id")
    val hospitalId: String,
    @SerialName("diagnosis")
    val diagnosis: String,
    @SerialName("total_cost")
    val totalCost: Double,
    @SerialName("label")
    val label: String,
    @SerialName("medical_resume_json")
    val medicalResumeJson: String? = null
)
