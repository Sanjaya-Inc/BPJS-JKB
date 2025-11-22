package io.healthkathon.jkb.frauddetection.presentation

import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import io.healthkathon.jkb.frauddetection.data.model.DoctorData
import io.healthkathon.jkb.frauddetection.data.model.HospitalData
import io.healthkathon.jkb.frauddetection.domain.FraudDetectionRepository
import io.healthkathon.jkb.frauddetection.domain.model.MedicalResume
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.koin.android.annotation.KoinViewModel

data class FraudDetectionUiState(
    val currentTab: FraudDetectionTab = FraudDetectionTab.CLAIM_ID,
    val isLoading: Boolean = false,
    val result: String? = null,
    val hospitals: PersistentList<HospitalData> = persistentListOf(),
    val doctors: PersistentList<DoctorData> = persistentListOf(),
    val isLoadingData: Boolean = false,
    val dataError: String? = null,
    val currentClaimId: String? = null,
    val currentActorType: String? = null,
    val currentActorId: String? = null,
    val feedbackGiven: Boolean = false
) {
    val hospitalsName get() = hospitals.mapNotNull { it.name }.toPersistentList()
    val doctorsName get() = doctors.mapNotNull { it.name }.toPersistentList()
}

enum class FraudDetectionTab(val title: String, val icon: String) {
    CLAIM_ID("Klaim ID", "🔍"),
    NEW_CLAIM("Klaim Baru", "📝"),
    ACTOR("Analisis Aktor", "👤")
}

enum class ActorType(val displayName: String) {
    DOCTOR("Dokter"),
    HOSPITAL("Rumah Sakit")
}

sealed interface FraudDetectionIntent {
    data class NavigateToTab(val tab: FraudDetectionTab) : FraudDetectionIntent
    data class SubmitClaimId(val claimId: String) : FraudDetectionIntent
    data class SubmitNewClaim(
        val claimId: String,
        val hospitalId: String,
        val doctorId: String,
        val diagnosis: String,
        val totalCost: String,
        val symptoms: String,
        val treatment: String,
        val medications: String,
        val notes: String
    ) : FraudDetectionIntent
    data class SubmitActorAnalysis(val actorType: ActorType, val actorId: String) : FraudDetectionIntent
    data object LoadHospitals : FraudDetectionIntent
    data object LoadDoctors : FraudDetectionIntent
    data class SubmitFeedback(val isLike: Boolean) : FraudDetectionIntent
}

@KoinViewModel
class FraudDetectionViewModel(
    private val repository: FraudDetectionRepository
) : BaseViewModel<FraudDetectionUiState, Unit>(
    initialState = FraudDetectionUiState()
) {
    override fun onIntent(intent: Any) {
        when (intent) {
            is FraudDetectionIntent.NavigateToTab -> navigateToTab(intent.tab)
            is FraudDetectionIntent.SubmitClaimId -> submitClaimId(intent.claimId)
            is FraudDetectionIntent.SubmitNewClaim -> submitNewClaim(
                intent.claimId,
                intent.hospitalId,
                intent.doctorId,
                intent.diagnosis,
                intent.totalCost,
                intent.symptoms,
                intent.treatment,
                intent.medications,
                intent.notes
            )
            is FraudDetectionIntent.SubmitActorAnalysis -> submitActorAnalysis(
                intent.actorType,
                intent.actorId
            )
            is FraudDetectionIntent.LoadHospitals -> loadHospitals()
            is FraudDetectionIntent.LoadDoctors -> loadDoctors()
            is FraudDetectionIntent.SubmitFeedback -> submitFeedback(intent.isLike)
        }
    }

    private fun loadHospitals() = intent {
        reduce { state.copy(isLoadingData = true, dataError = null) }

        repository.getHospitals()
            .onSuccess { hospitals ->
                reduce { state.copy(hospitals = hospitals, isLoadingData = false) }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoadingData = false,
                        dataError = "Gagal memuat data rumah sakit: ${error.message}"
                    )
                }
            }
    }

    private fun loadDoctors() = intent {
        reduce { state.copy(isLoadingData = true, dataError = null) }

        repository.getDoctors()
            .onSuccess { doctors ->
                reduce { state.copy(doctors = doctors, isLoadingData = false) }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoadingData = false,
                        dataError = "Gagal memuat data dokter: ${error.message}"
                    )
                }
            }
    }

    private fun navigateToTab(tab: FraudDetectionTab) = intent {
        reduce {
            state.copy(
                currentTab = tab,
                result = null,
                feedbackGiven = false,
                currentClaimId = null,
                currentActorType = null,
                currentActorId = null
            )
        }
    }

    private fun submitClaimId(claimId: String) = intent {
        reduce { state.copy(isLoading = true, result = null, feedbackGiven = false) }

        repository.checkByClaimId(claimId)
            .onSuccess { response ->
                reduce {
                    state.copy(
                        isLoading = false,
                        result = response.answer,
                        currentClaimId = claimId,
                        currentActorType = null,
                        currentActorId = null,
                        feedbackGiven = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        result = "❌ Gagal menganalisis klaim: ${error.message}",
                        currentClaimId = null
                    )
                }
            }
    }

    private fun submitNewClaim(
        claimId: String,
        hospitalId: String,
        doctorId: String,
        diagnosis: String,
        totalCost: String,
        symptoms: String,
        treatment: String,
        medications: String,
        notes: String
    ) = intent {
        reduce { state.copy(isLoading = true, result = null, feedbackGiven = false) }

        val costValue = totalCost.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        val medicalResume = MedicalResume(
            symptoms = symptoms,
            treatment = treatment,
            medications = medications,
            notes = notes
        )

        repository.checkNewClaim(
            claimId = claimId,
            hospitalId = hospitalId,
            doctorId = doctorId,
            diagnosis = diagnosis,
            totalCost = costValue,
            medicalResume = medicalResume
        )
            .onSuccess { response ->
                reduce {
                    state.copy(
                        isLoading = false,
                        result = response.answer,
                        currentClaimId = claimId,
                        currentActorType = null,
                        currentActorId = null,
                        feedbackGiven = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        result = "❌ Gagal menganalisis klaim baru: ${error.message}",
                        currentClaimId = null
                    )
                }
            }
    }

    private fun submitActorAnalysis(actorType: ActorType, actorId: String) = intent {
        reduce { state.copy(isLoading = true, result = null, feedbackGiven = false) }

        repository.analyzeActor(actorType.name, actorId)
            .onSuccess { response ->
                reduce {
                    state.copy(
                        isLoading = false,
                        result = response.answer,
                        currentClaimId = null,
                        currentActorType = actorType.name,
                        currentActorId = actorId,
                        feedbackGiven = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        result = "❌ Gagal menganalisis aktor: ${error.message}",
                        currentActorType = null,
                        currentActorId = null
                    )
                }
            }
    }

    private fun submitFeedback(isLike: Boolean) = intent {
        val feedbackType = if (isLike) "LIKE" else "DISLIKE"

        when (state.currentTab) {
            FraudDetectionTab.CLAIM_ID, FraudDetectionTab.NEW_CLAIM -> {
                val claimId = state.currentClaimId ?: return@intent
                repository.submitClaimFeedback(claimId, feedbackType)
                    .onSuccess {
                        reduce { state.copy(feedbackGiven = true) }
                    }
                    .onFailure {
                        // Silent fail for feedback
                    }
            }
            FraudDetectionTab.ACTOR -> {
                val actorType = state.currentActorType ?: return@intent
                val actorId = state.currentActorId ?: return@intent
                repository.submitActorFeedback(actorType, actorId, feedbackType)
                    .onSuccess {
                        reduce { state.copy(feedbackGiven = true) }
                    }
                    .onFailure {
                        // Silent fail for feedback
                    }
            }
        }
    }
}
