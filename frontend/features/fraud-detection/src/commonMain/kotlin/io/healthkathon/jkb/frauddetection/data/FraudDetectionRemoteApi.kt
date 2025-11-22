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
import io.healthkathon.jkb.frauddetection.data.model.DoctorResponse
import io.healthkathon.jkb.frauddetection.data.model.FeedbackResponse
import io.healthkathon.jkb.frauddetection.data.model.HospitalResponse
import io.healthkathon.jkb.frauddetection.data.model.NewClaimRequest

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

    @POST("claims/verify")
    suspend fun checkByClaimId(
        @Body claimCheckRequest: ClaimCheckRequest
    ): ClaimCheckAnswerData

    @POST("claim/new")
    suspend fun checkNewClaim(
        @Body newClaimRequest: NewClaimRequest
    ): ClaimCheckAnswerData

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
