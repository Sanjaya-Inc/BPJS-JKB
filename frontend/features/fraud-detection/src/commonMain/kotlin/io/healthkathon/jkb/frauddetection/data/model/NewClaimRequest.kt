package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewClaimRequest(
    @SerialName("hospital_id")
    val hospitalId: String,
    @SerialName("doctor_id")
    val doctorId: String,
    @SerialName("diagnosa_id")
    val diagnosisId: String,
    @SerialName("total_cost")
    val totalCost: Int,
    @SerialName("primary_procedure")
    val primaryProcedure: String,
    @SerialName("secondary_procedure")
    val secondaryProcedure: String,
    @SerialName("diagnosis_text")
    val diagnosisText: String
)
