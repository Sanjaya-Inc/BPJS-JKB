package io.healthkathon.jkb.frauddetection.domain

import io.healthkathon.jkb.frauddetection.data.FraudDetectionRemoteApi
import io.healthkathon.jkb.frauddetection.data.model.ActorAnalysisRequest
import io.healthkathon.jkb.frauddetection.data.model.ActorFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckAnswerData
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimData
import io.healthkathon.jkb.frauddetection.data.model.ClaimFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.DoctorData
import io.healthkathon.jkb.frauddetection.data.model.FeedbackResponse
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

    suspend fun submitClaimFeedback(
        claimId: String,
        feedbackType: String
    ): Result<FeedbackResponse> {
        return remoteApi.runCatching {
            submitClaimFeedback(
                ClaimFeedbackRequest(
                    claimId = claimId,
                    feedbackType = feedbackType
                )
            )
        }
    }

    suspend fun submitActorFeedback(
        actorType: String,
        actorId: String,
        feedbackType: String
    ): Result<FeedbackResponse> {
        return remoteApi.runCatching {
            submitActorFeedback(
                ActorFeedbackRequest(
                    actorType = actorType,
                    actorId = actorId,
                    feedbackType = feedbackType
                )
            )
        }
    }

    suspend fun getClaims(
        status: String? = null,
        hospitalId: String? = null,
        doctorId: String? = null
    ): Result<PersistentList<ClaimData>> {
        return remoteApi.runCatching {
            getClaims(
                status = status,
                hospitalId = hospitalId,
                doctorId = doctorId
            ).data?.filterNotNull()?.toPersistentList()
                ?: persistentListOf()
        }
    }
}
