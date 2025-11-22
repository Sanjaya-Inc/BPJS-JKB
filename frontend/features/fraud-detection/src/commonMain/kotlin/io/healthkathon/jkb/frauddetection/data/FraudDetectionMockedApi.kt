package io.healthkathon.jkb.frauddetection.data

import io.healthkathon.jkb.frauddetection.data.model.ActorAnalysisRequest
import io.healthkathon.jkb.frauddetection.data.model.ActorFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckAnswerData
import io.healthkathon.jkb.frauddetection.data.model.ClaimCheckRequest
import io.healthkathon.jkb.frauddetection.data.model.ClaimFeedbackRequest
import io.healthkathon.jkb.frauddetection.data.model.DoctorData
import io.healthkathon.jkb.frauddetection.data.model.DoctorResponse
import io.healthkathon.jkb.frauddetection.data.model.FeedbackResponse
import io.healthkathon.jkb.frauddetection.data.model.HospitalData
import io.healthkathon.jkb.frauddetection.data.model.HospitalResponse
import io.healthkathon.jkb.frauddetection.data.model.NewClaimRequest
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

    override suspend fun checkByClaimId(claimCheckRequest: ClaimCheckRequest): ClaimCheckAnswerData {
        delay(2000)

        val claimId = claimCheckRequest.claimId
        val mockAnswer = """
# Hasil Analisis Fraud - Klaim ID: $claimId

## 📊 Ringkasan Analisis
**Status**: ⚠️ Potensi Fraud Terdeteksi  
**Tingkat Risiko**: **TINGGI** (85%)  
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

        return ClaimCheckAnswerData(
            answer = mockAnswer,
            status = "success"
        )
    }

    override suspend fun checkNewClaim(newClaimRequest: NewClaimRequest): ClaimCheckAnswerData {
        delay(2500)

        val hospitalName = newClaimRequest.hospitalId
        val doctorName = newClaimRequest.doctorId
        val costValue = newClaimRequest.totalCost.toLong()
        val formattedCost = "Rp ${costValue.toString().reversed().chunked(3).joinToString(".").reversed()}"

        val mockAnswer = """
# Hasil Analisis Fraud - Klaim Baru

## 📊 Ringkasan Analisis
**Status**: ✅ Klaim Normal  
**Tingkat Risiko**: **RENDAH** (15%)  
**Tanggal Analisis**: 21 November 2025, 14:30 WIB

---

## 🔍 Detail Klaim
- **ID Klaim**: ${newClaimRequest.claimId}
- **Rumah Sakit**: $hospitalName
- **Dokter**: $doctorName
- **Diagnosis**: ${newClaimRequest.diagnosis}
- **Biaya Total**: $formattedCost
- **Label**: ${newClaimRequest.label}

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

## 💡 Rekomendasi

1. **Proses Klaim**
   - ✅ Klaim dapat diproses untuk pembayaran
   - Tidak diperlukan investigasi tambahan

2. **Monitoring Rutin**
   - Lanjutkan monitoring standar
   - Update database pola klaim normal

---

## 📈 Skor Kredibilitas

| Aspek | Skor | Status |
|-------|------|--------|
| Konsistensi Biaya | 95/100 | ✅ Excellent |
| Validitas Provider | 98/100 | ✅ Excellent |
| Kelengkapan Data | 92/100 | ✅ Excellent |
| Pola Historis | 88/100 | ✅ Good |

**Skor Total**: **93/100** - Klaim Valid

---

**Catatan**: Klaim ini telah melewati semua validasi otomatis dan dapat diproses sesuai prosedur standar.
        """.trimIndent()

        return ClaimCheckAnswerData(
            answer = mockAnswer,
            status = "success"
        )
    }

    override suspend fun analyzeActor(actorAnalysisRequest: ActorAnalysisRequest): ClaimCheckAnswerData {
        delay(3000)

        val actorType = actorAnalysisRequest.actorType
        val actorId = actorAnalysisRequest.actorId
        val actorName = if (actorType == "DOCTOR") "Dr. $actorId" else actorId
        val actorDisplayName = if (actorType == "DOCTOR") "Dokter" else "Rumah Sakit"

        val mockAnswer = """
# Hasil Analisis Fraud - $actorDisplayName

## 📊 Profil $actorDisplayName
**Nama**: $actorName  
**Status Analisis**: ⚠️ Memerlukan Perhatian  
**Tingkat Risiko**: **SEDANG** (55%)  
**Tanggal Analisis**: 21 November 2025, 14:30 WIB

---

## 🔍 Statistik Klaim (90 Hari Terakhir)

### Volume Klaim
- **Total Klaim**: 247 klaim
- **Rata-rata per Hari**: 2.7 klaim
- **Nilai Total**: Rp 1.2 Miliar
- **Rata-rata Nilai Klaim**: Rp 4.8 Juta

### Distribusi Diagnosis
1. **Diabetes Mellitus** - 45 klaim (18%)
2. **Hipertensi** - 38 klaim (15%)
3. **ISPA** - 32 klaim (13%)
4. **Gastritis** - 28 klaim (11%)
5. **Lainnya** - 104 klaim (43%)

---

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

## 📊 Analisis Komparatif

### Perbandingan dengan Peer Group

| Metrik | $actorDisplayName Ini | Rata-rata Peer | Status |
|--------|------------------------------|----------------|--------|
| Volume Klaim/Bulan | 82 | 45 | ⚠️ +82% |
| Nilai Rata-rata Klaim | Rp 4.8 jt | Rp 3.2 jt | ⚠️ +50% |
| Rejection Rate | 3% | 5% | ✅ Normal |
| Kompleksitas Kasus | 6.2/10 | 5.8/10 | ✅ Normal |

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

---

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
        """.trimIndent()

        return ClaimCheckAnswerData(
            answer = mockAnswer,
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
}
