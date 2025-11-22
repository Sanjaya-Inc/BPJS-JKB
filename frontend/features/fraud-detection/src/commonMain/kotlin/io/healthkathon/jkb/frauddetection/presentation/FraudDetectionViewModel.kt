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
    val filteredHospitals: PersistentList<HospitalData> = persistentListOf(),
    val selectedHospitalId: String? = null,
    val hospitalSearchQuery: String = "",
    val doctors: PersistentList<DoctorData> = persistentListOf(),
    val diagnoses: PersistentList<DiagnosisData> = persistentListOf(),
    val claims: PersistentList<ClaimData> = persistentListOf(),
    val filteredClaims: PersistentList<ClaimData> = persistentListOf(),
    val selectedClaimId: String? = null,
    val searchQuery: String = "",
    val isLoadingData: Boolean = false,
    val dataError: String? = null,
    val currentClaimId: String? = null,
    val currentHospitalId: String? = null,
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
    HOSPITAL_ANALYSIS("Analisis RS", "🏥")
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
    data class SelectHospital(val hospitalId: String) : FraudDetectionIntent
    data class SearchHospitals(val query: String) : FraudDetectionIntent
    data object SubmitHospitalAnalysis : FraudDetectionIntent
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
            is FraudDetectionIntent.SelectHospital -> selectHospital(intent.hospitalId)
            is FraudDetectionIntent.SearchHospitals -> searchHospitals(intent.query)
            is FraudDetectionIntent.SubmitHospitalAnalysis -> submitHospitalAnalysis()
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
                reduce {
                    state.copy(
                        hospitals = hospitals,
                        filteredHospitals = hospitals,
                        isLoadingData = false
                    )
                }
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
                        result = response.explanation,
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

    private fun selectHospital(hospitalId: String) = intent {
        reduce {
            state.copy(selectedHospitalId = hospitalId)
        }
    }

    private fun searchHospitals(query: String) = intent {
        reduce {
            val filtered = if (query.isBlank()) {
                state.hospitals
            } else {
                state.hospitals.filter { hospital ->
                    hospital.name?.contains(query, ignoreCase = true) == true ||
                        hospital.hospitalId?.contains(query, ignoreCase = true) == true ||
                        hospital.classType?.contains(query, ignoreCase = true) == true
                }.toPersistentList()
            }
            state.copy(hospitalSearchQuery = query, filteredHospitals = filtered)
        }
    }

    private fun submitHospitalAnalysis() = intent {
        val hospitalId = state.selectedHospitalId ?: return@intent

        reduce { state.copy(isLoading = true, result = null, feedbackGiven = false) }

        repository.analyzeHospital(hospitalId)
            .onSuccess { response ->
                val formattedResult = formatHospitalAnalysisResult(response)
                reduce {
                    state.copy(
                        isLoading = false,
                        result = formattedResult,
                        currentHospitalId = hospitalId,
                        currentClaimId = null,
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
                        result = "❌ Gagal menganalisis rumah sakit: ${error.message}",
                        currentHospitalId = null
                    )
                }
            }
    }

    private fun formatDouble(value: Double, decimals: Int = 2): String {
        val multiplier = when (decimals) {
            1 -> 10.0
            2 -> 100.0
            3 -> 1000.0
            else -> 100.0
        }
        val rounded = (value * multiplier).toLong().toDouble() / multiplier
        return rounded.toString()
    }

    private fun formatHospitalAnalysisResult(
        response: io.healthkathon.jkb.frauddetection.data.model.HospitalAnalysisResponse
    ): String {
        val diagnosisData = response.data ?: emptyList()

        val highRiskDiagnoses = diagnosisData.filter { it.zScore > 2.0 }
        val moderateRiskDiagnoses = diagnosisData.filter { it.zScore > 1.0 && it.zScore <= 2.0 }
        val normalDiagnoses = diagnosisData.filter { it.zScore <= 1.0 }

        return buildString {
            appendLine("# 🏥 Analisis Pola Klaim Rumah Sakit")
            appendLine()
            appendLine("## 📊 Informasi Rumah Sakit")
            appendLine("**Nama**: ${response.hospitalName}")
            appendLine("**ID**: ${response.hospitalId}")
            appendLine(
                "**Tipe Analisis**: ${
                    response.analysisType.replace("_", " ")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }"
            )
            appendLine()
            appendLine("---")
            appendLine()

            if (highRiskDiagnoses.isNotEmpty()) {
                appendLine("## 🔴 Diagnosis Risiko Tinggi (Z-Score > 2.0)")
                appendLine()
                appendLine("Diagnosis dengan pola klaim yang **sangat tidak normal**:")
                appendLine()
                highRiskDiagnoses.forEach { diagnosis ->
                    appendLine("### 🚨 ${diagnosis.diagnosisName}")
                    appendLine("- **Kode ICD-10**: ${diagnosis.diagnosisId}")
                    appendLine("- **Total Klaim**: ${diagnosis.totalClaims}")
                    appendLine("- **Z-Score**: **${formatDouble(diagnosis.zScore)}** ⚠️")
                    appendLine("- **Status**: Memerlukan investigasi segera")
                    appendLine()
                }
                appendLine("---")
                appendLine()
            }

            if (moderateRiskDiagnoses.isNotEmpty()) {
                appendLine("## 🟠 Diagnosis Risiko Sedang (Z-Score 1.0 - 2.0)")
                appendLine()
                appendLine("Diagnosis dengan pola klaim yang **perlu perhatian**:")
                appendLine()
                moderateRiskDiagnoses.forEach { diagnosis ->
                    appendLine("### ⚠️ ${diagnosis.diagnosisName}")
                    appendLine("- **Kode ICD-10**: ${diagnosis.diagnosisId}")
                    appendLine("- **Total Klaim**: ${diagnosis.totalClaims}")
                    appendLine("- **Z-Score**: **${formatDouble(diagnosis.zScore)}**")
                    appendLine("- **Status**: Monitoring diperlukan")
                    appendLine()
                }
                appendLine("---")
                appendLine()
            }

            if (normalDiagnoses.isNotEmpty()) {
                appendLine("## ✅ Diagnosis Normal (Z-Score ≤ 1.0)")
                appendLine()
                appendLine("Diagnosis dengan pola klaim yang **normal**:")
                appendLine()
                normalDiagnoses.forEach { diagnosis ->
                    appendLine("### ${diagnosis.diagnosisName}")
                    appendLine("- **Kode ICD-10**: ${diagnosis.diagnosisId}")
                    appendLine("- **Total Klaim**: ${diagnosis.totalClaims}")
                    appendLine("- **Z-Score**: ${formatDouble(diagnosis.zScore)}")
                    appendLine()
                }
                appendLine("---")
                appendLine()
            }

            appendLine("## 📈 Ringkasan Analisis")
            appendLine()
            appendLine("| Kategori | Jumlah Diagnosis |")
            appendLine("|----------|------------------|")
            appendLine("| 🔴 Risiko Tinggi | ${highRiskDiagnoses.size} |")
            appendLine("| 🟠 Risiko Sedang | ${moderateRiskDiagnoses.size} |")
            appendLine("| ✅ Normal | ${normalDiagnoses.size} |")
            appendLine("| **Total** | **${diagnosisData.size}** |")
            appendLine()
            appendLine("---")
            appendLine()

            appendLine("## 💡 Interpretasi Z-Score")
            appendLine()
            appendLine("**Z-Score** mengukur seberapa jauh pola klaim rumah sakit menyimpang dari rata-rata normal:")
            appendLine()
            appendLine("- **Z-Score > 2.0**: Pola klaim sangat tidak normal, memerlukan investigasi segera")
            appendLine("- **Z-Score 1.0 - 2.0**: Pola klaim sedikit tidak normal, perlu monitoring")
            appendLine("- **Z-Score ≤ 1.0**: Pola klaim dalam batas normal")
            appendLine("- **Z-Score negatif**: Klaim lebih rendah dari rata-rata (bisa jadi baik)")
            appendLine()

            if (highRiskDiagnoses.isNotEmpty() || moderateRiskDiagnoses.isNotEmpty()) {
                appendLine("---")
                appendLine()
                appendLine("## 🎯 Rekomendasi Tindakan")
                appendLine()
                if (highRiskDiagnoses.isNotEmpty()) {
                    appendLine("### Prioritas Tinggi")
                    appendLine("1. Lakukan audit mendalam untuk diagnosis dengan Z-Score > 2.0")
                    appendLine("2. Verifikasi dokumentasi medis dan klaim terkait")
                    appendLine("3. Wawancara dengan pihak rumah sakit")
                    appendLine()
                }
                if (moderateRiskDiagnoses.isNotEmpty()) {
                    appendLine("### Prioritas Sedang")
                    appendLine("1. Monitor pola klaim untuk diagnosis dengan Z-Score 1.0-2.0")
                    appendLine("2. Review sampling klaim secara berkala")
                    appendLine("3. Edukasi standar klaim kepada rumah sakit")
                    appendLine()
                }
            }

            appendLine("---")
            appendLine()
            appendLine(
                "**Catatan**: Analisis ini menggunakan distribusi normal untuk " +
                    "mendeteksi anomali dalam pola klaim rumah sakit."
            )
        }
    }

    private fun navigateToTab(tab: FraudDetectionTab) = intent {
        reduce {
            state.copy(
                currentTab = tab,
                result = null,
                feedbackGiven = false,
                currentClaimId = null,
                currentHospitalId = null,
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
                        result = response.explanation,
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
                        result = response.explanation,
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
            FraudDetectionTab.HOSPITAL_ANALYSIS -> {
                val hospitalId = state.currentHospitalId ?: return@intent
                repository.submitActorFeedback("HOSPITAL", hospitalId, feedbackType)
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
