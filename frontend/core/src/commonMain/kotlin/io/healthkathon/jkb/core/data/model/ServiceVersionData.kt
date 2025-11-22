package io.healthkathon.jkb.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceVersionData(
    @SerialName("message")
    val message: String?,
    @SerialName("version")
    val version: String?
)
