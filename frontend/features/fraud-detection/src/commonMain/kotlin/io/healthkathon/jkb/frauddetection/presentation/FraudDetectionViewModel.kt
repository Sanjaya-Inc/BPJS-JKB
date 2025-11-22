package io.healthkathon.jkb.frauddetection.presentation

import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import io.healthkathon.jkb.frauddetection.data.model.ClaimData
import io.healthkathon.jkb.frauddetection.data.model.DiagnosisData
import io.healthkathon.jkb.frauddetection.data.model.DoctorData
import io.healthkathon.jkb.frauddetection.data.model.HospitalData
import io.healthkathon.jkb.frauddetection.domain.FraudDetectionRepository
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
    val diagnoses: PersistentList<DiagnosisData> = persistentListOf(),
    val claims: PersistentList<ClaimData> = persistentListOf(),
    val filteredClaims: PersistentList<ClaimData> = persistentListOf(),
    val selectedClaimId: String? = null,
    val searchQuery: String = "",
    val isLoadingData: Boolean = false,
    val dataError: String? = null,
    val currentClaimId: String? = null,
    val currentActorType: String? = null,
    val currentActorId: String? = null,
    val feedbackGiven: Boolean = false
) {
    val hospitalsName get() = hospitals.mapNotNull { it.name }.toPersistentList()
    val doctorsName get() = doctors.mapNotNull { it.name }.toPersistentList()
    val diagnosesDisplay get() = diagnoses.map { "${it.name} (${it.icd10Code})" }.toPersistentList()
    val claimsIds get() = claims.map { it.claimId }.toPersistentList()
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
    data class SelectClaim(val claimId: String) : FraudDetectionIntent
    data object SubmitSelectedClaim : FraudDetectionIntent
    data class SearchClaims(val query: String) : FraudDetectionIntent
    data class SubmitNewClaim(
        val hospitalId: String,
        val doctorId: String,
        val diagnosisId: String,
        val totalCost: String,
        val primaryProcedure: String,
        val secondaryProcedure: String,
        val diagnosisText: String
    ) : FraudDetectionIntent
    data class SubmitActorAnalysis(val actorType: ActorType, val actorId: String) : FraudDetectionIntent
    data object LoadHospitals : FraudDetectionIntent
    data object LoadDoctors : FraudDetectionIntent
    data object LoadDiagnoses : FraudDetectionIntent
    data object LoadClaims : FraudDetectionIntent
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
            is FraudDetectionIntent.SelectClaim -> selectClaim(intent.claimId)
            is FraudDetectionIntent.SubmitSelectedClaim -> submitSelectedClaim()
            is FraudDetectionIntent.SearchClaims -> searchClaims(intent.query)
            is FraudDetectionIntent.SubmitNewClaim -> submitNewClaim(
                intent.hospitalId,
                intent.doctorId,
                intent.diagnosisId,
                intent.totalCost,
                intent.primaryProcedure,
                intent.secondaryProcedure,
                intent.diagnosisText
            )
            is FraudDetectionIntent.SubmitActorAnalysis -> submitActorAnalysis(
                intent.actorType,
                intent.actorId
            )
            is FraudDetectionIntent.LoadHospitals -> loadHospitals()
            is FraudDetectionIntent.LoadDoctors -> loadDoctors()
            is FraudDetectionIntent.LoadDiagnoses -> loadDiagnoses()
            is FraudDetectionIntent.LoadClaims -> loadClaims()
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

    private fun loadDiagnoses() = intent {
        reduce { state.copy(isLoadingData = true, dataError = null) }

        repository.getDiagnoses()
            .onSuccess { diagnoses ->
                reduce { state.copy(diagnoses = diagnoses, isLoadingData = false) }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoadingData = false,
                        dataError = "Gagal memuat data diagnosis: ${error.message}"
                    )
                }
            }
    }

    private fun loadClaims() = intent {
        reduce { state.copy(isLoadingData = true, dataError = null) }

        repository.getClaims()
            .onSuccess { claims ->
                reduce {
                    state.copy(
                        claims = claims,
                        filteredClaims = claims,
                        isLoadingData = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoadingData = false,
                        dataError = "Gagal memuat data klaim: ${error.message}"
                    )
                }
            }
    }

    private fun selectClaim(claimId: String) = intent {
        reduce {
            state.copy(selectedClaimId = claimId)
        }
    }

    private fun searchClaims(query: String) = intent {
        reduce {
            val filtered = if (query.isBlank()) {
                state.claims
            } else {
                state.claims.filter { claim ->
                    claim.claimId.contains(query, ignoreCase = true) ||
                        claim.diagnosis.contains(query, ignoreCase = true) ||
                        claim.label.contains(query, ignoreCase = true)
                }.toPersistentList()
            }
            state.copy(searchQuery = query, filteredClaims = filtered)
        }
    }

    private fun submitSelectedClaim() = intent {
        val claimId = state.selectedClaimId ?: return@intent

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
        hospitalId: String,
        doctorId: String,
        diagnosisId: String,
        totalCost: String,
        primaryProcedure: String,
        secondaryProcedure: String,
        diagnosisText: String
    ) = intent {
        reduce { state.copy(isLoading = true, result = null, feedbackGiven = false) }

        val costValue = totalCost.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

        repository.checkNewClaim(
            hospitalId = hospitalId,
            doctorId = doctorId,
            diagnosisId = diagnosisId,
            totalCost = costValue,
            primaryProcedure = primaryProcedure,
            secondaryProcedure = secondaryProcedure,
            diagnosisText = diagnosisText
        )
            .onSuccess { response ->
                val formattedResult = """
# 📊 Hasil Analisis Fraud - Klaim Baru

## Ringkasan Data
${response.formDataSummary}

---

## 🎯 Hasil Validasi
**Status**: ${if (response.validationResult == "FRAUD") "⚠️ FRAUD" else "✅ NORMAL"}  
**Confidence Score**: ${response.confidenceScore}%

---

${response.detailAnalysis}

---

${response.explanation}
                """.trimIndent()

                reduce {
                    state.copy(
                        isLoading = false,
                        result = formattedResult,
                        currentClaimId = "NEW_CLAIM",
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
                        reduce { state.copy(feedbackGiven = true) }
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
                        reduce { state.copy(feedbackGiven = true) }
                    }
            }
        }
    }
}
