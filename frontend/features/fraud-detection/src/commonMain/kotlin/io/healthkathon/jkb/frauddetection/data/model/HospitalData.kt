package io.healthkathon.jkb.frauddetection.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HospitalData(
    @SerialName("class_type")
    val classType: String?,
    @SerialName("facilities")
    val facilities: List<String?>?,
    @SerialName("hospital_id")
    val hospitalId: String?,
    @SerialName("location")
    val location: Location?,
    @SerialName("name")
    val name: String?,
    @SerialName("specialties")
    val specialties: List<String?>?
) {
    @Serializable
    data class Location(
        @SerialName("latitude")
        val latitude: Double?,
        @SerialName("longitude")
        val longitude: Double?
    )
}
