package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DoctorData(
    @SerialName("doctor_id")
    val doctorId: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("primary_hospital_id")
    val primaryHospitalId: String?,
    @SerialName("specialization")
    val specialization: String?
)
