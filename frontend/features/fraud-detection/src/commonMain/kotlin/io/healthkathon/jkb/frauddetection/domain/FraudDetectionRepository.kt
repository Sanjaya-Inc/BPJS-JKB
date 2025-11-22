package io.healthkathon.jkb.frauddetection.domain

import io.healthkathon.jkb.frauddetection.data.FraudDetectionRemoteApi
import io.healthkathon.jkb.frauddetection.data.model.ActorAnalysisRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckAnswerData
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckRequest
import io.healthkathon.jkb.frauddetection.data.model.DoctorData
import io.healthkathon.jkb.frauddetection.data.model.HospitalData
import io.healthkathon.jkb.frauddetection.data.model.NewClaimRequest
import io.healthkathon.jkb.frauddetection.domain.model.MedicalResume
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class FraudDetectionRepository(
    private val remoteApi: FraudDetectionRemoteApi
) {
    suspend fun getDoctors(): Result<PersistentList<DoctorData>> {
        return remoteApi.runCatching {
            getDoctors().data?.filterNotNull()?.toPersistentList()
                ?: persistentListOf()
        }
    }

    suspend fun getHospitals(): Result<PersistentList<HospitalData>> {
        return remoteApi.runCatching {
            getHospitals().data?.filterNotNull()?.toPersistentList()
                ?: persistentListOf()
        }
    }

    suspend fun checkByClaimId(claimId: String): Result<ClaimCheckAnswerData> {
        return remoteApi.runCatching {
            checkByClaimId(ClaimCheckRequest(claimId))
        }
    }

    suspend fun checkNewClaim(
        claimId: String,
        hospitalId: String,
        doctorId: String,
        diagnosis: String,
        totalCost: Double,
        medicalResume: MedicalResume
    ): Result<ClaimCheckAnswerData> {
        return remoteApi.runCatching {
            val medicalResumeJson = Json.encodeToString(
                mapOf(
                    "symptoms" to medicalResume.symptoms,
                    "treatment" to medicalResume.treatment,
                    "medications" to medicalResume.medications,
                    "notes" to medicalResume.notes
                )
            )

            checkNewClaim(
                NewClaimRequest(
                    claimId = claimId,
                    hospitalId = hospitalId,
                    doctorId = doctorId,
                    diagnosis = diagnosis,
                    totalCost = totalCost,
                    label = "NORMAL",
                    medicalResumeJson = medicalResumeJson
                )
            )
        }
    }

    suspend fun analyzeActor(actorType: String, actorId: String): Result<ClaimCheckAnswerData> {
        return remoteApi.runCatching {
            analyzeActor(
                ActorAnalysisRequest(
                    actorType = actorType,
                    actorId = actorId
                )
            )
        }
    }
}
