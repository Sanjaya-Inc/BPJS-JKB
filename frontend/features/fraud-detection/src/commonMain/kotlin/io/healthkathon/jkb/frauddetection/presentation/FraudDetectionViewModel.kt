package io.healthkathon.jkb.frauddetection.presentation

import io.healthkathon.jkb.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel

data class FraudDetectionUiState(
    val currentTab: FraudDetectionTab = FraudDetectionTab.CLAIM_ID,
    val isLoading: Boolean = false,
    val result: String? = null
)

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
        val hospital: String,
        val doctor: String,
        val diagnosis: String,
        val cost: String,
        val action: String
    ) : FraudDetectionIntent
    data class SubmitActorAnalysis(val actorType: ActorType, val actorId: String) : FraudDetectionIntent
}

@KoinViewModel
class FraudDetectionViewModel : BaseViewModel<FraudDetectionUiState, Unit>(
    initialState = FraudDetectionUiState()
) {
    override fun onIntent(intent: Any) {
        when (intent) {
            is FraudDetectionIntent.NavigateToTab -> navigateToTab(intent.tab)
            is FraudDetectionIntent.SubmitClaimId -> submitClaimId(intent.claimId)
            is FraudDetectionIntent.SubmitNewClaim -> submitNewClaim(
                intent.hospital,
                intent.doctor,
                intent.diagnosis,
                intent.cost,
                intent.action
            )
            is FraudDetectionIntent.SubmitActorAnalysis -> submitActorAnalysis(
                intent.actorType,
                intent.actorId
            )
        }
    }

    private fun navigateToTab(tab: FraudDetectionTab) = intent {
        reduce { state.copy(currentTab = tab, result = null) }
    }

    private fun submitClaimId(claimId: String) = intent {
        reduce { state.copy(isLoading = true, result = null) }

        delay(2000)

        val mockResult = """
# Hasil Analisis Fraud - Klaim ID: $claimId

## 📊 Ringkasan Analisis
**Status**: ⚠️ Potensi Fraud Terdeteksi  
**Tingkat Risiko**: **TINGGI** (85%)  
**Tanggal Analisis**: ${getCurrentDate()}

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

        reduce { state.copy(isLoading = false, result = mockResult) }
    }

    private fun submitNewClaim(
        hospital: String,
        doctor: String,
        diagnosis: String,
        cost: String,
        action: String
    ) = intent {
        reduce { state.copy(isLoading = true, result = null) }

        delay(2500)

        val mockResult = """
# Hasil Analisis Fraud - Klaim Baru

## 📊 Ringkasan Analisis
**Status**: ✅ Klaim Normal  
**Tingkat Risiko**: **RENDAH** (15%)  
**Tanggal Analisis**: ${getCurrentDate()}

---

## 🔍 Detail Klaim
- **Rumah Sakit**: $hospital
- **Dokter**: $doctor
- **Diagnosis**: $diagnosis
- **Biaya Total**: $cost
- **Tindakan**: $action

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

        reduce { state.copy(isLoading = false, result = mockResult) }
    }

    private fun submitActorAnalysis(actorType: ActorType, actorId: String) = intent {
        reduce { state.copy(isLoading = true, result = null) }

        delay(3000)

        val actorName = if (actorType == ActorType.DOCTOR) "Dr. $actorId" else actorId
        val mockResult = """
# Hasil Analisis Fraud - ${actorType.displayName}

## 📊 Profil ${actorType.displayName}
**Nama**: $actorName  
**Status Analisis**: ⚠️ Memerlukan Perhatian  
**Tingkat Risiko**: **SEDANG** (55%)  
**Tanggal Analisis**: ${getCurrentDate()}

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

| Metrik | ${actorType.displayName} Ini | Rata-rata Peer | Status |
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

**Kesimpulan**: ${actorType.displayName} ini menunjukkan beberapa pola yang memerlukan investigasi lebih lanjut. Disarankan untuk melakukan audit dan monitoring intensif dalam 30 hari ke depan.

**Tindak Lanjut**: Tim investigasi akan menghubungi dalam 3 hari kerja untuk proses audit.
        """.trimIndent()

        reduce { state.copy(isLoading = false, result = mockResult) }
    }

    @Suppress("FunctionOnlyReturningConstant")
    private fun getCurrentDate(): String {
        return "21 November 2025, 14:30 WIB"
    }
}
