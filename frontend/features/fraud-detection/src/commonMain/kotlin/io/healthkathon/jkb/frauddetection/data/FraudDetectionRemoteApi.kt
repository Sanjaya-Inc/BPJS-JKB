package io.healthkathon.jkb.frauddetection.data

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.healthkathon.jkb.frauddetection.data.model.ActorAnalysisRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckAnswerData
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckRequest
import io.healthkathon.jkb.frauddetection.data.model.DoctorResponse
import io.healthkathon.jkb.frauddetection.data.model.HospitalResponse
import io.healthkathon.jkb.frauddetection.data.model.NewClaimRequest

interface FraudDetectionRemoteApi {

    @GET("hospitals")
    suspend fun getHospitals(): HospitalResponse

    @GET("doctors")
    suspend fun getDoctors(): DoctorResponse

    @POST("claim/check")
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
}
