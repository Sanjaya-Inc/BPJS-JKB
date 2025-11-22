package io.healthkathon.jkb.frauddetection.data

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.healthkathon.jkb.frauddetection.data.model.ActorAnalysisRequest
import io.healthkathon.jkb.frauddetection.data.model.ActorFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckAnswerData
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimsResponse
import io.healthkathon.jkb.frauddetection.data.model.DiagnosisResponse
import io.healthkathon.jkb.frauddetection.data.model.DoctorResponse
import io.healthkathon.jkb.frauddetection.data.model.FeedbackResponse
import io.healthkathon.jkb.frauddetection.data.model.HospitalResponse
import io.healthkathon.jkb.frauddetection.data.model.NewClaimRequest
import io.healthkathon.jkb.frauddetection.data.model.NewClaimResponse

interface FraudDetectionRemoteApi {

    @GET("claims")
    suspend fun getClaims(
        @Query("status") status: String? = null,
        @Query("hospital_id") hospitalId: String? = null,
        @Query("doctor_id") doctorId: String? = null
    ): ClaimsResponse

    @GET("hospitals")
    suspend fun getHospitals(): HospitalResponse

    @GET("doctors")
    suspend fun getDoctors(): DoctorResponse

    @GET("diagnoses")
    suspend fun getDiagnoses(
        @Query("severity_level") severityLevel: String? = null,
        @Query("icd10_code") icd10Code: String? = null,
        @Query("name") name: String? = null,
        @Query("min_cost") minCost: Double? = null,
        @Query("max_cost") maxCost: Double? = null
    ): DiagnosisResponse

    @POST("claims/verify")
    suspend fun checkByClaimId(
        @Body claimCheckRequest: ClaimCheckRequest
    ): ClaimCheckAnswerData

    @POST("claims/verify-form")
    suspend fun checkNewClaim(
        @Body newClaimRequest: NewClaimRequest
    ): NewClaimResponse

    @POST("actor/analyze")
    suspend fun analyzeActor(
        @Body actorAnalysisRequest: ActorAnalysisRequest
    ): ClaimCheckAnswerData

    @POST("claim/feedback")
    suspend fun submitClaimFeedback(
        @Body feedbackRequest: ClaimFeedbackRequest
    ): FeedbackResponse

    @POST("actor/feedback")
    suspend fun submitActorFeedback(
        @Body feedbackRequest: ActorFeedbackRequest
    ): FeedbackResponse
}
