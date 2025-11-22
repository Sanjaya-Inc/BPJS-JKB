package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HospitalResponse(
    @SerialName("data")
    val data: List<HospitalData?>?
)
