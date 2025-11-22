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
        delay(2000)

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

        val explanation = if (validationResult == "NORMAL") {
            """
# Hasil Analisis Fraud - Klaim ID: $claimId

## 📊 Ringkasan Analisis
**Status**: ✅ Klaim Normal  
**Tingkat Kepercayaan**: **$confidenceScore%**  
**Tanggal Analisis**: 21 November 2025, 14:30 WIB

---

## 🔍 Detail Klaim
- **ID Klaim**: $claimId
- **Rumah Sakit**: RS Harapan Sehat
- **Dokter**: Dr. Ahmad Wijaya, Sp.PD
- **Diagnosis**: Diabetes Mellitus Type 2
- **Biaya Total**: Rp 13.200.000
- **Tanggal Klaim**: 15 November 2025

---

## ✅ Validasi Berhasil

### 1. Konsistensi Biaya
- Biaya sesuai dengan standar tarif INA-CBG untuk diagnosis ini
- Tidak ada item biaya yang mencurigakan
- Biaya administrasi dalam batas normal (8%)

### 2. Pola Temporal Normal
- Waktu perawatan sesuai dengan standar medis (36 jam)
- Tidak ada clustering klaim yang mencurigakan
- Dokumentasi lengkap dan tepat waktu

### 3. Profil Pasien Normal
- Riwayat klaim konsisten dengan kondisi medis
- Tidak ada pola kunjungan yang tidak wajar
- Dokumentasi medis lengkap dan valid

---

## 💡 Rekomendasi

**Proses Klaim**: Klaim dapat diproses untuk pembayaran sesuai prosedur standar.

**Monitoring**: Lanjutkan monitoring rutin tanpa tindakan khusus.

---

## 📈 Analisis Komparatif

| Metrik | Klaim Ini | Rata-rata Normal | Status |
|--------|-----------|------------------|--------|
| Biaya Total | Rp 13.2 jt | Rp 13.2 jt | ✅ Normal |
| Durasi Rawat | 36 jam | 36 jam | ✅ Normal |
| Jumlah Obat | 4 item | 4 item | ✅ Normal |
| Biaya Admin | 8% | 8% | ✅ Normal |

---

**Catatan**: Analisis ini dihasilkan oleh sistem AI dengan tingkat kepercayaan $confidenceScore%. Klaim telah melewati semua validasi otomatis.
            """.trimIndent()
        } else {
            """
# Hasil Analisis Fraud - Klaim ID: $claimId

## 📊 Ringkasan Analisis
**Status**: ⚠️ Potensi Fraud Terdeteksi  
**Tingkat Risiko**: **TINGGI** ($confidenceScore%)  
**Tanggal Analisis**: 21 November 2025, 14:30 WIB

---

## 🔍 Detail Klaim
- **ID Klaim**: $claimId
- **Rumah Sakit**: RS Harapan Sehat
- **Dokter**: Dr. Ahmad Wijaya, Sp.PD
- **Diagnosis**: Diabetes Mellitus Type 2
- **Biaya Total**: Rp 45.750.000
- **Tanggal Klaim**: 15 November 2025

---

## ⚠️ Indikator Fraud Terdeteksi

### 1. Pola Biaya Tidak Wajar
- Biaya pengobatan **3.5x lebih tinggi** dari rata-rata kasus serupa
- Terdapat **15 item obat** yang tidak relevan dengan diagnosis
- Biaya administrasi mencapai **25%** dari total (normal: 5-10%)

### 2. Pola Temporal Mencurigakan
- Klaim diajukan pada **hari yang sama** dengan 8 klaim lain dari RS yang sama
- Waktu perawatan hanya **2 jam** untuk diagnosis yang memerlukan observasi minimal 24 jam

### 3. Anomali Data Pasien
- Pasien tercatat melakukan **12 kunjungan** dalam 30 hari terakhir
- Riwayat klaim menunjukkan pola **diagnosis berulang** yang tidak konsisten

---

## 💡 Rekomendasi Tindakan

1. **Investigasi Mendalam**
   - Verifikasi keberadaan pasien dan validitas dokumen medis
   - Audit rekam medis lengkap dari rumah sakit

2. **Tindakan Preventif**
   - Tahan pembayaran klaim hingga investigasi selesai
   - Lakukan wawancara dengan dokter dan pasien terkait

3. **Monitoring Berkelanjutan**
   - Tambahkan RS Harapan Sehat ke daftar pengawasan khusus
   - Monitor pola klaim dari Dr. Ahmad Wijaya

---

## 📈 Analisis Komparatif

| Metrik | Klaim Ini | Rata-rata Normal | Deviasi |
|--------|-----------|------------------|---------|
| Biaya Total | Rp 45.7 jt | Rp 13.2 jt | +246% |
| Durasi Rawat | 2 jam | 36 jam | -94% |
| Jumlah Obat | 15 item | 4 item | +275% |
| Biaya Admin | 25% | 8% | +213% |

---

**Catatan**: Analisis ini dihasilkan oleh sistem AI dan memerlukan verifikasi manual oleh tim investigasi fraud.
            """.trimIndent()
        }

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

        val hospitalName = newClaimRequest.hospitalId
        val doctorName = newClaimRequest.doctorId
        val costValue = newClaimRequest.totalCost
        val formattedCost =
            "Rp ${costValue.toString().reversed().chunked(3).joinToString(".").reversed()}"

        val formDataSummary = """
Hospital ID: $hospitalName
Doctor ID: $doctorName
Diagnosis ID: ${newClaimRequest.diagnosisId}
Total Cost: $formattedCost
Primary Procedure: ${newClaimRequest.primaryProcedure}
Secondary Procedure: ${newClaimRequest.secondaryProcedure}
Diagnosis Text: ${newClaimRequest.diagnosisText}
        """.trimIndent()

        val detailAnalysis = """
# 📊 Analisis Detail Klaim

## Informasi Klaim
- **Hospital ID**: $hospitalName
- **Doctor ID**: $doctorName
- **Diagnosis ID**: ${newClaimRequest.diagnosisId}
- **Biaya Total**: $formattedCost
- **Prosedur Utama**: ${newClaimRequest.primaryProcedure}
- **Prosedur Sekunder**: ${newClaimRequest.secondaryProcedure}

---

## ✅ Validasi Berhasil

### 1. Konsistensi Data
- Biaya sesuai dengan standar tarif INA-CBG untuk diagnosis ini
- Tindakan medis relevan dengan diagnosis yang diberikan
- Tidak ada duplikasi klaim dalam 30 hari terakhir

### 2. Profil Provider
- Rumah sakit memiliki **track record baik** (compliance rate: 98%)
- Dokter terdaftar dan memiliki **sertifikasi aktif**
- Tidak ada riwayat fraud dari provider ini

### 3. Pola Klaim Normal
- Frekuensi klaim dalam batas wajar
- Tidak ada anomali temporal terdeteksi
- Dokumentasi lengkap dan valid

---

## 📈 Skor Kredibilitas

| Aspek | Skor | Status |
|-------|------|--------|
| Konsistensi Biaya | 95/100 | ✅ Excellent |
| Validitas Provider | 98/100 | ✅ Excellent |
| Kelengkapan Data | 92/100 | ✅ Excellent |
| Pola Historis | 88/100 | ✅ Good |

**Skor Total**: **93/100** - Klaim Valid
        """.trimIndent()

        val explanation = """
# 💡 Penjelasan Hasil Validasi

## Kesimpulan
Klaim ini telah melewati semua tahap validasi dengan hasil **NORMAL**. Tidak ditemukan indikasi fraud atau anomali yang mencurigakan.

## Rekomendasi Tindakan

### ✅ Proses Klaim
- Klaim dapat diproses untuk pembayaran
- Tidak diperlukan investigasi tambahan
- Dokumentasi lengkap dan sesuai standar

### 📊 Monitoring Rutin
- Lanjutkan monitoring standar
- Update database pola klaim normal
- Catat sebagai referensi klaim valid

---

**Catatan**: Analisis ini dihasilkan oleh sistem AI dengan tingkat kepercayaan 85%. Klaim telah melewati semua validasi otomatis dan dapat diproses sesuai prosedur standar.
        """.trimIndent()

        return NewClaimResponse(
            formDataSummary = formDataSummary,
            validationResult = "NORMAL",
            confidenceScore = 85,
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
1. **Diabetes Mellitus** - ${if (validationResult == "SUSPICIOUS") "45" else "22"} klaim (${if (validationResult == "SUSPICIOUS") "18" else "15"}%)
2. **Hipertensi** - ${if (validationResult == "SUSPICIOUS") "38" else "20"} klaim (${if (validationResult == "SUSPICIOUS") "15" else "14"}%)
3. **ISPA** - ${if (validationResult == "SUSPICIOUS") "32" else "18"} klaim (${if (validationResult == "SUSPICIOUS") "13" else "12"}%)
4. **Gastritis** - ${if (validationResult == "SUSPICIOUS") "28" else "15"} klaim (${if (validationResult == "SUSPICIOUS") "11" else "10"}%)
5. **Lainnya** - ${if (validationResult == "SUSPICIOUS") "104" else "70"} klaim (${if (validationResult == "SUSPICIOUS") "43" else "49"}%)

---

${if (validationResult == "SUSPICIOUS") {
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
        }}

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

${if (validationResult == "SUSPICIOUS") {
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
        }}
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
