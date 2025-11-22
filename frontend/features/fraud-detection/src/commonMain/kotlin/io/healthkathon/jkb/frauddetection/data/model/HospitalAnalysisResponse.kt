package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HospitalAnalysisResponse(
    @SerialName("data")
    val data: List<HospitalAnalysisData>?,
    @SerialName("hospital_id")
    val hospitalId: String,
    @SerialName("hospital_name")
    val hospitalName: String,
    @SerialName("analysis_type")
    val analysisType: String
)
