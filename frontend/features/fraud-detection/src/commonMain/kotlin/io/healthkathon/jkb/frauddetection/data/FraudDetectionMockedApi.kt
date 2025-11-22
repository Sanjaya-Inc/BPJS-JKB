package io.healthkathon.jkb.frauddetection.data

import io.healthkathon.jkb.frauddetection.data.model.ActorAnalysisRequest
import io.healthkathon.jkb.frauddetection.data.model.ActorFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckAnswerData
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimData
import io.healthkathon.jkb.frauddetection.data.model.ClaimFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimsResponse
import io.healthkathon.jkb.frauddetection.data.model.DiagnosisData
import io.healthkathon.jkb.frauddetection.data.model.DiagnosisResponse
import io.healthkathon.jkb.frauddetection.data.model.DoctorData
import io.healthkathon.jkb.frauddetection.data.model.DoctorResponse
import io.healthkathon.jkb.frauddetection.data.model.FeedbackResponse
import io.healthkathon.jkb.frauddetection.data.model.HospitalAnalysisData
import io.healthkathon.jkb.frauddetection.data.model.HospitalAnalysisResponse
import io.healthkathon.jkb.frauddetection.data.model.HospitalData
import io.healthkathon.jkb.frauddetection.data.model.HospitalResponse
import io.healthkathon.jkb.frauddetection.data.model.NewClaimRequest
import io.healthkathon.jkb.frauddetection.data.model.NewClaimResponse
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class FraudDetectionMockedApi : FraudDetectionRemoteApi {
    override suspend fun getHospitals(): HospitalResponse {
        delay(800)
        return HospitalResponse(
            data = listOf(
                HospitalData(
                    hospitalId = "H001",
                    name = "RS Harapan Sehat",
                    classType = "A",
                    location = HospitalData.Location(latitude = -6.200000, longitude = 106.816666),
                    facilities = listOf("ICU", "Emergency", "Laboratory"),
                    specialties = listOf("Cardiology", "Neurology", "Pediatrics")
                ),
                HospitalData(
                    hospitalId = "H002",
                    name = "RS Mitra Keluarga",
                    classType = "A",
                    location = HospitalData.Location(latitude = -6.175000, longitude = 106.827777),
                    facilities = listOf("ICU", "Emergency", "Laboratory", "Radiology"),
                    specialties = listOf("Orthopedics", "Surgery", "Internal Medicine")
                ),
                HospitalData(
                    hospitalId = "H003",
                    name = "RS Siloam",
                    classType = "A",
                    location = HospitalData.Location(latitude = -6.225000, longitude = 106.800000),
                    facilities = listOf("ICU", "Emergency", "Laboratory", "CT Scan"),
                    specialties = listOf("Oncology", "Cardiology", "Neurosurgery")
                ),
                HospitalData(
                    hospitalId = "H004",
                    name = "RS Hermina",
                    classType = "B",
                    location = HospitalData.Location(latitude = -6.195000, longitude = 106.850000),
                    facilities = listOf("Emergency", "Laboratory", "Maternity"),
                    specialties = listOf("Obstetrics", "Gynecology", "Pediatrics")
                ),
                HospitalData(
                    hospitalId = "H005",
                    name = "RSUD Kota",
                    classType = "B",
                    location = HospitalData.Location(latitude = -6.210000, longitude = 106.830000),
                    facilities = listOf("Emergency", "Laboratory"),
                    specialties = listOf("General Medicine", "Surgery")
                ),
                HospitalData(
                    hospitalId = "H006",
                    name = "RS Medika Permata",
                    classType = "B",
                    location = HospitalData.Location(latitude = -6.180000, longitude = 106.840000),
                    facilities = listOf("ICU", "Emergency", "Laboratory"),
                    specialties = listOf("Internal Medicine", "Pulmonology")
                ),
                HospitalData(
                    hospitalId = "H007",
                    name = "RS Graha Medika",
                    classType = "C",
                    location = HospitalData.Location(latitude = -6.230000, longitude = 106.820000),
                    facilities = listOf("Emergency", "Laboratory"),
                    specialties = listOf("General Medicine")
                ),
                HospitalData(
                    hospitalId = "H008",
                    name = "RS Bunda Sejahtera",
                    classType = "B",
                    location = HospitalData.Location(latitude = -6.190000, longitude = 106.860000),
                    facilities = listOf("Emergency", "Laboratory", "Maternity"),
                    specialties = listOf("Obstetrics", "Pediatrics")
                )
            )
        )
    }

    override suspend fun getDoctors(): DoctorResponse {
        delay(800)
        return DoctorResponse(
            data = listOf(
                DoctorData(
                    doctorId = "D001",
                    name = "Ahmad Wijaya, Sp.PD",
                    specialization = "Internal Medicine",
                    primaryHospitalId = "H001"
                ),
                DoctorData(
                    doctorId = "D002",
                    name = "Siti Nurhaliza, Sp.A",
                    specialization = "Pediatrics",
                    primaryHospitalId = "H001"
                ),
                DoctorData(
                    doctorId = "D003",
                    name = "Budi Santoso, Sp.B",
                    specialization = "Surgery",
                    primaryHospitalId = "H002"
                ),
                DoctorData(
                    doctorId = "D004",
                    name = "Rina Kusuma, Sp.OG",
                    specialization = "Obstetrics & Gynecology",
                    primaryHospitalId = "H004"
                ),
                DoctorData(
                    doctorId = "D005",
                    name = "Hendra Gunawan, Sp.JP",
                    specialization = "Cardiology",
                    primaryHospitalId = "H003"
                ),
                DoctorData(
                    doctorId = "D006",
                    name = "Dewi Lestari, Sp.M",
                    specialization = "Ophthalmology",
                    primaryHospitalId = "H002"
                ),
                DoctorData(
                    doctorId = "D007",
                    name = "Agus Setiawan, Sp.THT",
                    specialization = "ENT",
                    primaryHospitalId = "H006"
                ),
                DoctorData(
                    doctorId = "D008",
                    name = "Maya Sari, Sp.KK",
                    specialization = "Dermatology",
                    primaryHospitalId = "H001"
                )
            )
        )
    }

    override suspend fun getDiagnoses(
        severityLevel: String?,
        icd10Code: String?,
        name: String?,
        minCost: Double?,
        maxCost: Double?
    ): DiagnosisResponse {
        delay(800)

        val allDiagnoses = listOf(
            DiagnosisData(
                diagnosisId = "DX001",
                icd10Code = "E11",
                name = "Diabetes Mellitus Type 2",
                avgCost = 13200000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX002",
                icd10Code = "J20",
                name = "Acute Bronchitis",
                avgCost = 3250000.0,
                severityLevel = "Low"
            ),
            DiagnosisData(
                diagnosisId = "DX003",
                icd10Code = "K35",
                name = "Appendicitis",
                avgCost = 28500000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX004",
                icd10Code = "I25",
                name = "Coronary Artery Disease",
                avgCost = 51000000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX005",
                icd10Code = "O80",
                name = "Normal Delivery",
                avgCost = 8500000.0,
                severityLevel = "Medium"
            ),
            DiagnosisData(
                diagnosisId = "DX006",
                icd10Code = "I10",
                name = "Hypertension",
                avgCost = 4200000.0,
                severityLevel = "Medium"
            ),
            DiagnosisData(
                diagnosisId = "DX007",
                icd10Code = "H25",
                name = "Cataract",
                avgCost = 15000000.0,
                severityLevel = "Medium"
            ),
            DiagnosisData(
                diagnosisId = "DX008",
                icd10Code = "J32",
                name = "Chronic Sinusitis",
                avgCost = 4200000.0,
                severityLevel = "Low"
            ),
            DiagnosisData(
                diagnosisId = "DX009",
                icd10Code = "S72",
                name = "Fracture of Femur",
                avgCost = 45000000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX010",
                icd10Code = "L20",
                name = "Atopic Dermatitis (Eczema)",
                avgCost = 1500000.0,
                severityLevel = "Low"
            ),
            DiagnosisData(
                diagnosisId = "DX011",
                icd10Code = "J18",
                name = "Pneumonia",
                avgCost = 18500000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX012",
                icd10Code = "K29",
                name = "Gastritis",
                avgCost = 2800000.0,
                severityLevel = "Low"
            ),
            DiagnosisData(
                diagnosisId = "DX013",
                icd10Code = "M54",
                name = "Dorsalgia (Back Pain)",
                avgCost = 3500000.0,
                severityLevel = "Low"
            ),
            DiagnosisData(
                diagnosisId = "DX014",
                icd10Code = "N18",
                name = "Chronic Kidney Disease",
                avgCost = 65000000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX015",
                icd10Code = "I21",
                name = "Acute Myocardial Infarction",
                avgCost = 95000000.0,
                severityLevel = "High"
            ),
            DiagnosisData(
                diagnosisId = "DX016",
                icd10Code = "A09",
                name = "Gastroenteritis",
                avgCost = 2500000.0,
                severityLevel = "Low"
            ),
            DiagnosisData(
                diagnosisId = "DX017",
                icd10Code = "O82",
                name = "Cesarean Section",
                avgCost = 18000000.0,
                severityLevel = "Medium"
            ),
            DiagnosisData(
                diagnosisId = "DX018",
                icd10Code = "E78",
                name = "Hyperlipidemia",
                avgCost = 3800000.0,
                severityLevel = "Medium"
            ),
            DiagnosisData(
                diagnosisId = "DX019",
                icd10Code = "J45",
                name = "Asthma",
                avgCost = 5200000.0,
                severityLevel = "Medium"
            ),
            DiagnosisData(
                diagnosisId = "DX020",
                icd10Code = "I63",
                name = "Cerebral Infarction (Stroke)",
                avgCost = 78000000.0,
                severityLevel = "High"
            )
        )

        val filteredDiagnoses = allDiagnoses.filter { diagnosis ->
            val severityMatch = severityLevel == null ||
                diagnosis.severityLevel.equals(severityLevel, ignoreCase = true)
            val icd10Match = icd10Code == null ||
                diagnosis.icd10Code.contains(icd10Code, ignoreCase = true)
            val nameMatch = name == null ||
                diagnosis.name.contains(name, ignoreCase = true)
            val minCostMatch = minCost == null || diagnosis.avgCost >= minCost
            val maxCostMatch = maxCost == null || diagnosis.avgCost <= maxCost

            severityMatch && icd10Match && nameMatch && minCostMatch && maxCostMatch
        }

        return DiagnosisResponse(data = filteredDiagnoses)
    }

    override suspend fun checkByClaimId(claimCheckRequest: ClaimCheckRequest): ClaimCheckAnswerData {
        delay(3000)

        val claimId = claimCheckRequest.claimId

        val validationResult = if (claimId.contains("001") ||
            claimId.contains("004") ||
            claimId.contains("006") ||
            claimId.contains("009")
        ) {
            "FRAUD"
        } else {
            "NORMAL"
        }

        val confidenceScore = if (validationResult == "FRAUD") 85 else 100

        val detailClaimDataText = if (validationResult == "NORMAL") {
            """{"c.id": "$claimId", "c.total_cost": 13200000.0, "c.status": "NORMAL", """ +
                """"patient_name": "Budi Santoso", "hospital_name": "RS Harapan Sehat", """ +
                """"doctor_name": "Dr. Ahmad Wijaya, Sp.PD", "diagnosis_name": "Diabetes Mellitus Type 2", """ +
                """"primary_procedures": ["Insulin Therapy", "Blood Sugar Monitoring"], """ +
                """"secondary_procedures": ["Dietary Counseling", "Exercise Program"], """ +
                """"note.primary_diagnosis_text": "Diabetes Mellitus Type 2", """ +
                """"note.secondary_diagnosis_text": "Patient requires regular monitoring and medication adjustment"}"""
        } else {
            """{"c.id": "$claimId", "c.total_cost": 45750000.0, "c.status": "FRAUD", """ +
                """"patient_name": "Budi Santoso", "hospital_name": "RS Harapan Sehat", """ +
                """"doctor_name": "Dr. Ahmad Wijaya, Sp.PD", "diagnosis_name": "Diabetes Mellitus Type 2", """ +
                """"primary_procedures": ["Insulin Therapy", "Blood Sugar Monitoring"], """ +
                """"secondary_procedures": ["Dietary Counseling", "Exercise Program", "Multiple 
                    |Unnecessary Tests"], 
                """.trimMargin() +
                """"note.primary_diagnosis_text": "Diabetes Mellitus Type 2", """ +
                """"note.secondary_diagnosis_text": "Excessive medication costs and unnecess
                    |ary procedures detected. 
                """.trimMargin() +
                """Patient has 12 visits in 30 days with inconsistent diagnosis patterns"}"""
        }

        val explanationText = if (validationResult == "NORMAL") {
            "The claim status is validated as \"NORMAL\". All checks passed successfully. " +
                "The claim shows consistent billing patterns, appropriate treatment costs, " +
                "and normal temporal patterns. No fraud indicators detected."
        } else {
            "The claim status is flagged as \"FRAUD\". Multiple fraud indicators detected including: " +
                "excessive costs (3.5x higher than average), suspicious temporal patterns " +
                "(multiple claims on same day), and abnormal patient visit frequency " +
                "(12 visits in 30 days). Requires immediate investigation."
        }

        val explanation = """Claim ID: $claimId  
Validation Result: $validationResult  
Confidence Score: $confidenceScore%  
Detail Claim Data: $detailClaimDataText  
Explanation: $explanationText"""

        return ClaimCheckAnswerData(
            claimId = claimId,
            validationResult = validationResult,
            confidenceScore = confidenceScore,
            detailClaimData = null,
            explanation = explanation,
            status = "success"
        )
    }

    override suspend fun checkNewClaim(newClaimRequest: NewClaimRequest): NewClaimResponse {
        delay(2500)

        val hospitalId = newClaimRequest.hospitalId
        val doctorId = newClaimRequest.doctorId
        val diagnosisId = newClaimRequest.diagnosisId
        val costValue = newClaimRequest.totalCost
        val primaryProcedure = newClaimRequest.primaryProcedure
        val secondaryProcedure = newClaimRequest.secondaryProcedure
        val diagnosisText = newClaimRequest.diagnosisText

        // Determine if this is a fraud case based on diagnosis ID or cost
        val isFraud = diagnosisId == "I63" || costValue < 20000000.0

        val validationResult = if (isFraud) "FRAUD" else "NORMAL"
        val confidenceScore = if (isFraud) 95 else 85

        val formattedCost = costValue.toString()

        val formDataSummary = """
Hospital ID: $hospitalId
Doctor ID: $doctorId
Diagnosis ID: $diagnosisId
Total Cost: $formattedCost
Primary Procedure: $primaryProcedure
Secondary Procedure: $secondaryProcedure
Diagnosis Text: $diagnosisText
        """.trimIndent()

        val claimId = "CLM-${Clock.System.now().epochSeconds}"

        val detailAnalysis = if (isFraud) {
            """Claim ID: $claimId  
Validation Result: FRAUD  
Confidence Score: $confidenceScore%  
Detail Claim Data: {Hospital ID: $hospitalId, Doctor ID: $doctorId, Diagnosis ID: $diagnosisId, 
Total Cost: $formattedCost, Primary Procedure: $primaryProcedure, Secondary 
Procedure: $secondaryProcedure, Diagnosis Text: $diagnosisText}  
Explanation: The claim failed all validation steps. 1) No relationships found 
between diagnosis $diagnosisId and procedures $primaryProcedure/$secondaryProcedure in the 
graph database. 2) No diagnosis node found with code $diagnosisId. 3) No procedure nodes found 
for $primaryProcedure/$secondaryProcedure. 4) No doctor node found with ID $doctorId. 5) 
No hospital node found with ID $hospitalId.  This indicates the claim data is not present in 
the graph database, making it impossible to perform any validation.  The absence of data in the 
graph database suggests potential fraud or data entry errors."""
        } else {
            """Claim ID: $claimId  
Validation Result: NORMAL  
Confidence Score: $confidenceScore%  
Detail Claim Data: {Hospital ID: $hospitalId, Doctor ID: $doctorId, 
Diagnosis ID: $diagnosisId, Total Cost: $formattedCost, 
Primary Procedure: $primaryProcedure, Secondary Procedure: $secondaryProcedure, 
Diagnosis Text: $diagnosisText}  
Explanation: The claim passed all validation steps. 1) Relationships 
found between diagnosis $diagnosisId and procedures $primaryProcedure/$secondaryProcedure 
in the graph database. 2) Diagnosis node found with code $diagnosisId.
3) Procedure nodes found for $primaryProcedure/$secondaryProcedure. 
4) Doctor node found with ID $doctorId. 
5) Hospital node found with ID $hospitalId. 
All data is consistent and valid."""
        }

        val explanation = if (isFraud) {
            """Claim ID: $claimId  
Validation Result: FRAUD  
Confidence Score: $confidenceScore%  
Detail Claim Data: {Hospital ID: $hospitalId, Doctor ID: $doctorId, Diagnosis ID: $diagnosisId, 
Total Cost: $formattedCost, Primary Procedure: $primaryProcedure, Secondary 
Procedure: $secondaryProcedure, Diagnosis Text: $diagnosisText}  
Explanation: The claim failed all validation steps. 1) No relationships found between 
diagnosis $diagnosisId and procedures $primaryProcedure/$secondaryProcedure in the graph 
database. 2) No diagnosis node found with code $diagnosisId. 3) No procedure nodes found 
for $primaryProcedure/$secondaryProcedure. 4) No doctor node found with ID $doctorId. 5) 
No hospital node found with ID $hospitalId.  This indicates the claim data is not present 
in the graph database, making it impossible to perform any validation.  The absence of data 
in the graph database suggests potential fraud or data entry errors."""
        } else {
            """Claim ID: $claimId  
Validation Result: NORMAL  
Confidence Score: $confidenceScore%  
Detail Claim Data: {Hospital ID: $hospitalId, Doctor ID: $doctorId, 
Diagnosis ID: $diagnosisId, Total Cost: $formattedCost, 
Primary Procedure: $primaryProcedure, Secondary Procedure: $secondaryProcedure, 
Diagnosis Text: $diagnosisText}  
Explanation: The claim passed all validation steps successfully. 
All data is present in the graph database and relationships are valid."""
        }

        return NewClaimResponse(
            formDataSummary = formDataSummary,
            validationResult = validationResult,
            confidenceScore = confidenceScore,
            detailAnalysis = detailAnalysis,
            explanation = explanation,
            status = "success"
        )
    }

    override suspend fun analyzeActor(actorAnalysisRequest: ActorAnalysisRequest): ClaimCheckAnswerData {
        delay(3000)

        val actorType = actorAnalysisRequest.actorType
        val actorId = actorAnalysisRequest.actorId
        val actorName = if (actorType == "DOCTOR") "Dr. $actorId" else actorId
        val actorDisplayName = if (actorType == "DOCTOR") "Dokter" else "Rumah Sakit"

        val validationResult =
            if (actorId.contains("001") || actorId.contains("D001") || actorId.contains("H001")) {
                "SUSPICIOUS"
            } else {
                "NORMAL"
            }

        val confidenceScore = if (validationResult == "SUSPICIOUS") 55 else 95

        val explanation = """
# Hasil Analisis Fraud - $actorDisplayName

## 📊 Profil $actorDisplayName
**Nama**: $actorName  
**Status Analisis**: ${if (validationResult == "SUSPICIOUS") "⚠️ Memerlukan Perhatian" else "✅ Normal"}  
**Tingkat Risiko**: **${if (validationResult == "SUSPICIOUS") "SEDANG" else "RENDAH"}** ($confidenceScore%)  
**Tanggal Analisis**: 21 November 2025, 14:30 WIB

---

## 🔍 Statistik Klaim (90 Hari Terakhir)

### Volume Klaim
- **Total Klaim**: ${if (validationResult == "SUSPICIOUS") "247" else "145"} klaim
- **Rata-rata per Hari**: ${if (validationResult == "SUSPICIOUS") "2.7" else "1.6"} klaim
- **Nilai Total**: Rp ${if (validationResult == "SUSPICIOUS") "1.2" else "0.6"} Miliar
- **Rata-rata Nilai Klaim**: Rp ${if (validationResult == "SUSPICIOUS") "4.8" else "4.1"} Juta

### Distribusi Diagnosis
1. **Diabetes Mellitus** - ${if (validationResult == "SUSPICIOUS") "45" else "22"} 
klaim (${if (validationResult == "SUSPICIOUS") "18" else "15"}%)
2. **Hipertensi** - ${if (validationResult == "SUSPICIOUS") "38" else "20"} 
klaim (${if (validationResult == "SUSPICIOUS") "15" else "14"}%)
3. **ISPA** - ${if (validationResult == "SUSPICIOUS") "32" else "18"} 
klaim (${if (validationResult == "SUSPICIOUS") "13" else "12"}%)
4. **Gastritis** - ${if (validationResult == "SUSPICIOUS") "28" else "15"} 
klaim (${if (validationResult == "SUSPICIOUS") "11" else "10"}%)
5. **Lainnya** - ${if (validationResult == "SUSPICIOUS") "104" else "70"} 
klaim (${if (validationResult == "SUSPICIOUS") "43" else "49"}%)

---

${
            if (validationResult == "SUSPICIOUS") {
                """
## ⚠️ Pola Mencurigakan Terdeteksi

### 1. Anomali Biaya
- **15 klaim** dengan biaya >200% dari rata-rata
- Kecenderungan penggunaan obat-obatan mahal yang tidak selalu diperlukan
- Biaya administrasi konsisten tinggi (18-22%)

### 2. Pola Temporal
- Lonjakan klaim pada **akhir bulan** (3x lipat dari normal)
- Clustering klaim pada **hari Jumat** (35% dari total)
- Waktu pemrosesan klaim sangat cepat (<2 jam)

### 3. Pola Pasien
- **12 pasien** dengan kunjungan berulang >10x dalam 90 hari
- Beberapa pasien memiliki **alamat yang sama**
- Pola diagnosis berulang pada pasien yang sama

---

## 🎯 Rekomendasi Tindakan

### Prioritas Tinggi
1. **Audit Mendalam**
   - Review 20 klaim dengan nilai tertinggi
   - Verifikasi keberadaan dan validitas pasien
   - Audit dokumentasi medis

2. **Investigasi Pola**
   - Analisis hubungan antar pasien dengan kunjungan berulang
   - Verifikasi alamat dan identitas pasien
   - Cross-check dengan database fraud nasional

### Prioritas Sedang
3. **Monitoring Intensif**
   - Tambahkan ke daftar pengawasan khusus
   - Review otomatis untuk setiap klaim >Rp 5 juta
   - Laporan bulanan wajib

4. **Edukasi & Komunikasi**
   - Sosialisasi standar klaim yang benar
   - Klarifikasi pola klaim yang mencurigakan
   - Peringatan formal jika diperlukan

"""
            } else {
                """
## ✅ Profil Normal

### 1. Pola Biaya Normal
- Biaya klaim konsisten dengan standar INA-CBG
- Tidak ada anomali biaya yang signifikan
- Biaya administrasi dalam batas normal (8-10%)

### 2. Pola Temporal Normal
- Distribusi klaim merata sepanjang bulan
- Tidak ada clustering yang mencurigakan
- Waktu pemrosesan sesuai standar

### 3. Pola Pasien Normal
- Tidak ada pasien dengan kunjungan berulang yang tidak wajar
- Dokumentasi lengkap dan konsisten
- Pola diagnosis sesuai dengan kondisi medis

---

## 💡 Rekomendasi

**Monitoring Rutin**: Lanjutkan monitoring standar tanpa tindakan khusus.

**Status**: $actorDisplayName ini memiliki profil klaim yang normal dan tidak memerlukan investigasi tambahan.

"""
            }
        }

---

## 📊 Analisis Komparatif

### Perbandingan dengan Peer Group

| Metrik | $actorDisplayName Ini | Rata-rata Peer | Status |
|--------|------------------------------|----------------|--------|
| Volume Klaim/Bulan | ${if (validationResult == "SUSPICIOUS") "82" else "48"} | 45 | ${if (validationResult == "SUSPICIOUS") "⚠️ +82%" else "✅ +7%"} |
| Nilai Rata-rata Klaim | Rp ${if (validationResult == "SUSPICIOUS") "4.8" else "4.1"} jt | Rp 3.2 jt | ${if (validationResult == "SUSPICIOUS") "⚠️ +50%" else "✅ +28%"} |
| Rejection Rate | ${if (validationResult == "SUSPICIOUS") "3" else "4"}% | 5% | ✅ Normal |
| Kompleksitas Kasus | ${if (validationResult == "SUSPICIOUS") "6.2" else "5.9"}/10 | 5.8/10 | ✅ Normal |

---

${
            if (validationResult == "SUSPICIOUS") {
                """
## 📈 Trend Analysis (6 Bulan)

```
Volume Klaim:
Jun: ████████░░ 65
Jul: ██████████ 78
Aug: ████████████ 92
Sep: ██████████░ 75
Oct: ████████████░ 88
Nov: ███████████████ 112 ⚠️
```

**Observasi**: Tren peningkatan volume klaim yang signifikan, terutama dalam 2 bulan terakhir.

---

## 🔔 Alert & Notifikasi

- 🔴 **3 klaim** dalam 24 jam terakhir melebihi threshold otomatis
- 🟡 **8 pasien** dengan pola kunjungan tidak wajar
- 🟡 Peningkatan **45%** volume klaim dibanding bulan lalu

---

**Kesimpulan**: $actorDisplayName ini menunjukkan beberapa pola yang memerlukan investigasi lebih lanjut. Disarankan untuk melakukan audit dan monitoring intensif dalam 30 hari ke depan.

**Tindak Lanjut**: Tim investigasi akan menghubungi dalam 3 hari kerja untuk proses audit.

"""
            } else {
                """
**Kesimpulan**: $actorDisplayName ini memiliki profil klaim yang sehat dan tidak menunjukkan indikasi fraud. Lanjutkan monitoring rutin.

"""
            }
        }
        """.trimIndent()

        return ClaimCheckAnswerData(
            claimId = actorId,
            validationResult = validationResult,
            confidenceScore = confidenceScore,
            detailClaimData = null,
            explanation = explanation,
            status = "success"
        )
    }

    override suspend fun submitClaimFeedback(feedbackRequest: ClaimFeedbackRequest): FeedbackResponse {
        delay(500)
        return FeedbackResponse(
            status = "success",
            message = "Terima kasih atas feedback Anda!"
        )
    }

    override suspend fun submitActorFeedback(feedbackRequest: ActorFeedbackRequest): FeedbackResponse {
        delay(500)
        return FeedbackResponse(
            status = "success",
            message = "Terima kasih atas feedback Anda!"
        )
    }

    override suspend fun getClaims(
        status: String?,
        hospitalId: String?,
        doctorId: String?
    ): ClaimsResponse {
        delay(1000)

        val allClaims = listOf(
            ClaimData(
                claimId = "CLM001",
                doctorId = "D001",
                hospitalId = "H001",
                diagnosis = "Diabetes Mellitus Type 2",
                totalCost = 45750000.0,
                label = "FRAUD",
                medicalResumeJson = """{"symptoms":"Frequent urination, excessive thirst",""" +
                    """"treatment":"Insulin therapy","medications":"Metformin, Insulin",""" +
                    """"notes":"Patient requires regular monitoring"}"""
            ),
            ClaimData(
                claimId = "CLM002",
                doctorId = "D002",
                hospitalId = "H001",
                diagnosis = "Acute Bronchitis",
                totalCost = 3250000.0,
                label = "NORMAL",
                medicalResumeJson = """{"symptoms":"Cough, fever, chest discomfort",""" +
                    """"treatment":"Antibiotics, rest","medications":"Amoxicillin, 
                        |Paracetamol",
                    """.trimMargin() +
                    """"notes":"Follow-up in 1 week"}"""
            ),
            ClaimData(
                claimId = "CLM003",
                doctorId = "D003",
                hospitalId = "H002",
                diagnosis = "Appendicitis",
                totalCost = 28500000.0,
                label = "NORMAL",
                medicalResumeJson = """{"symptoms":"Abdominal pain, nausea, vomiting",
                    |"treatment":"Appendectomy",
                """.trimMargin() +
                    """"medications":"Ceftriaxone, Ketorolac",
                        |"notes":"Surgery successful, recovery normal"}
                    """.trimMargin()
            ),
            ClaimData(
                claimId = "CLM004",
                doctorId = "D005",
                hospitalId = "H003",
                diagnosis = "Coronary Artery Disease",
                totalCost = 125000000.0,
                label = "FRAUD",
                medicalResumeJson = """{"symptoms":"Chest pain, shortness of breath",""" +
                    """"treatment":"Angioplasty",
                        |"medications":"Aspirin, Clopidogrel, Atorvastatin",
                    """.trimMargin() +
                    """"notes":"Suspicious billing patterns detected"}"""
            ),
            ClaimData(
                claimId = "CLM005",
                doctorId = "D004",
                hospitalId = "H004",
                diagnosis = "Normal Delivery",
                totalCost = 8500000.0,
                label = "NORMAL",
                medicalResumeJson = """{"symptoms":"Labor
                    | contractions","treatment":"Normal 
                    | vaginal delivery",
                """.trimMargin() +
                    """"medications":"Oxytocin, Vitamin K",
                        |"notes":"Mother and baby healthy"}
                    """.trimMargin()
            ),
            ClaimData(
                claimId = "CLM006",
                doctorId = "D001",
                hospitalId = "H001",
                diagnosis = "Hypertension",
                totalCost = 52000000.0,
                label = "FRAUD",
                medicalResumeJson = """{"symptoms":"High blood pressure, headache",""" +
                    """"treatment":"Medication management","medications":"Amlodipine, Losartan",""" +
                    """"notes":"Excessive medication costs flagged"}"""
            ),
            ClaimData(
                claimId = "CLM007",
                doctorId = "D006",
                hospitalId = "H002",
                diagnosis = "Cataract Surgery",
                totalCost = 15000000.0,
                label = "NORMAL",
                medicalResumeJson = """{"symptoms":"Blurred vision, difficulty seeing",""" +
                    """"treatment":"Phacoemulsification","medications":"Eye drops, Antibiotics",""" +
                    """"notes":"Routine procedure, no complications"}"""
            ),
            ClaimData(
                claimId = "CLM008",
                doctorId = "D007",
                hospitalId = "H006",
                diagnosis = "Chronic Sinusitis",
                totalCost = 4200000.0,
                label = "NORMAL",
                medicalResumeJson = """{"symptoms":"Nasal congestion, facial pain",""" +
                    """"treatment":"Medication, nasal irrigation","medications":"Amoxicillin, Nasal spray",""" +
                    """"notes":"Patient improving"}"""
            ),
            ClaimData(
                claimId = "CLM009",
                doctorId = "D003",
                hospitalId = "H002",
                diagnosis = "Fracture Femur",
                totalCost = 95000000.0,
                label = "FRAUD",
                medicalResumeJson = """{"symptoms":"Severe leg pain, inability to walk","treatment":"ORIF surgery",""" +
                    """"medications":"Morphine, Cefazolin","notes":"Billing irregularities detected"}"""
            ),
            ClaimData(
                claimId = "CLM010",
                doctorId = "D008",
                hospitalId = "H001",
                diagnosis = "Eczema",
                totalCost = 1500000.0,
                label = "NORMAL",
                medicalResumeJson = """{"symptoms":"Itchy skin, rash","treatment":"Topical steroids",""" +
                    """"medications":"Hydrocortisone cream","notes":"Mild case, good prognosis"}"""
            )
        )

        val filteredClaims = allClaims.filter { claim ->
            val statusMatch = status == null || claim.label.equals(status, ignoreCase = true)
            val hospitalMatch = hospitalId == null || claim.hospitalId == hospitalId
            val doctorMatch = doctorId == null || claim.doctorId == doctorId
            statusMatch && hospitalMatch && doctorMatch
        }

        return ClaimsResponse(data = filteredClaims)
    }

    override suspend fun analyzeHospital(hospitalId: String): HospitalAnalysisResponse {
        delay(1200)

        val hospitalAnalysisMap = mapOf(
            "HOS001" to HospitalAnalysisResponse(
                data = listOf(
                    HospitalAnalysisData(
                        diagnosisId = "K35.80",
                        diagnosisName = "Acute Appendicitis",
                        totalClaims = 1,
                        zScore = 1.499225489832609
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "K40.9",
                        diagnosisName = "Inguinal Hernia",
                        totalClaims = 1,
                        zScore = 0.7071067811865475
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "Z00.0",
                        diagnosisName = "General Medical Exam",
                        totalClaims = 1,
                        zScore = 0.7071067811865475
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "I21.9",
                        diagnosisName = "Acute Myocardial Infarction (Heart Attack)",
                        totalClaims = 3,
                        zScore = 0.4993445076308957
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "G43.9",
                        diagnosisName = "Migraine",
                        totalClaims = 1,
                        zScore = 0.0
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "R07.9",
                        diagnosisName = "Chest Pain Unspecified",
                        totalClaims = 1,
                        zScore = -0.7071067811865476
                    )
                ),
                hospitalId = "HOS001",
                hospitalName = "RSUP Dr. Hasan Sadikin (RSHS)",
                analysisType = "claiming_behavior_normal_distribution"
            ),
            "HOS002" to HospitalAnalysisResponse(
                data = listOf(
                    HospitalAnalysisData(
                        diagnosisId = "N39.0",
                        diagnosisName = "Urinary Tract Infection (UTI)",
                        totalClaims = 1,
                        zScore = 0.0
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "O82",
                        diagnosisName = "Caesarean Section Delivery",
                        totalClaims = 2,
                        zScore = 0.0
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "I10",
                        diagnosisName = "Essential Hypertension",
                        totalClaims = 1,
                        zScore = 0.0
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "J18.9",
                        diagnosisName = "Pneumonia Unspecified",
                        totalClaims = 1,
                        zScore = 0.0
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "I63.9",
                        diagnosisName = "Cerebral Infarction (Stroke)",
                        totalClaims = 2,
                        zScore = -0.45561165797767156
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "K35.80",
                        diagnosisName = "Acute Appendicitis",
                        totalClaims = 2,
                        zScore = -0.5111019224618589
                    )
                ),
                hospitalId = "HOS002",
                hospitalName = "Santosa Hospital Bandung Central",
                analysisType = "claiming_behavior_normal_distribution"
            ),
            "H001" to HospitalAnalysisResponse(
                data = listOf(
                    HospitalAnalysisData(
                        diagnosisId = "E11",
                        diagnosisName = "Diabetes Mellitus Type 2",
                        totalClaims = 5,
                        zScore = 0.25
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "J20",
                        diagnosisName = "Acute Bronchitis",
                        totalClaims = 3,
                        zScore = -0.15
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "I10",
                        diagnosisName = "Hypertension",
                        totalClaims = 4,
                        zScore = 0.0
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "K29",
                        diagnosisName = "Gastritis",
                        totalClaims = 2,
                        zScore = -0.35
                    )
                ),
                hospitalId = "H001",
                hospitalName = "RS Harapan Sehat",
                analysisType = "claiming_behavior_normal_distribution"
            ),
            "H002" to HospitalAnalysisResponse(
                data = listOf(
                    HospitalAnalysisData(
                        diagnosisId = "K35",
                        diagnosisName = "Appendicitis",
                        totalClaims = 3,
                        zScore = 0.45
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "S72",
                        diagnosisName = "Fracture of Femur",
                        totalClaims = 2,
                        zScore = -0.25
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "H25",
                        diagnosisName = "Cataract",
                        totalClaims = 4,
                        zScore = 0.15
                    )
                ),
                hospitalId = "H002",
                hospitalName = "RS Mitra Keluarga",
                analysisType = "claiming_behavior_normal_distribution"
            ),
            "H003" to HospitalAnalysisResponse(
                data = listOf(
                    HospitalAnalysisData(
                        diagnosisId = "I25",
                        diagnosisName = "Coronary Artery Disease",
                        totalClaims = 6,
                        zScore = 0.85
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "I21",
                        diagnosisName = "Acute Myocardial Infarction",
                        totalClaims = 3,
                        zScore = 0.55
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "N18",
                        diagnosisName = "Chronic Kidney Disease",
                        totalClaims = 2,
                        zScore = 0.0
                    )
                ),
                hospitalId = "H003",
                hospitalName = "RS Siloam",
                analysisType = "claiming_behavior_normal_distribution"
            ),
            "H004" to HospitalAnalysisResponse(
                data = listOf(
                    HospitalAnalysisData(
                        diagnosisId = "O80",
                        diagnosisName = "Normal Delivery",
                        totalClaims = 8,
                        zScore = 0.65
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "O82",
                        diagnosisName = "Cesarean Section",
                        totalClaims = 5,
                        zScore = 0.35
                    ),
                    HospitalAnalysisData(
                        diagnosisId = "J45",
                        diagnosisName = "Asthma",
                        totalClaims = 2,
                        zScore = -0.15
                    )
                ),
                hospitalId = "H004",
                hospitalName = "RS Hermina",
                analysisType = "claiming_behavior_normal_distribution"
            )
        )

        return hospitalAnalysisMap[hospitalId] ?: HospitalAnalysisResponse(
            data = listOf(
                HospitalAnalysisData(
                    diagnosisId = "UNKNOWN",
                    diagnosisName = "No Data Available",
                    totalClaims = 0,
                    zScore = 0.0
                )
            ),
            hospitalId = hospitalId,
            hospitalName = "Unknown Hospital",
            analysisType = "claiming_behavior_normal_distribution"
        )
    }
}
