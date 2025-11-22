package io.healthkathon.jkb.frauddetection.domain.model

data class MedicalResume(
    val symptoms: String,
    val treatment: String,
    val medications: String,
    val notes: String
)
