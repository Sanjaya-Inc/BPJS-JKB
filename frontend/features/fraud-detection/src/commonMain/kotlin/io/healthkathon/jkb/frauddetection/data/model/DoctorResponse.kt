package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DoctorResponse(
    @SerialName("data")
    val data: List<DoctorData?>?
)
