package io.healthkathon.jkb.chatbot.data

import io.healthkathon.jkb.chatbot.data.model.AnswerData
import io.healthkathon.jkb.chatbot.data.model.Question
import kotlinx.coroutines.delay

class ChatMockedRemoteApi : ChatbotRemoteApi {
    override suspend fun ask(question: Question): AnswerData {
        delay(1500)
        return AnswerData(
            answer = generateBotResponse(question.question),
            status = "success"
        )
    }

    private fun generateBotResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()

        return when {
            lowerMessage.contains("clm-") || lowerMessage.contains("klaim") -> {
                """
                📋 **Status Klaim**
                
                Saya telah mengecek klaim yang Anda maksud. Berikut informasinya:
                
                • **Status**: Dalam Proses Review
                • **Tingkat Risiko**: Rendah (15%)
                • **Estimasi Selesai**: 2-3 hari kerja
                
                Apakah ada yang ingin Anda tanyakan lebih lanjut?
                """.trimIndent()
            }

            lowerMessage.contains("fraud") || lowerMessage.contains("indikator") -> {
                """
                ⚠️ **Indikator Fraud Umum**
                
                Berikut beberapa indikator fraud yang kami deteksi:
                
                1. **Biaya Tidak Wajar** - Biaya >200% dari rata-rata
                2. **Pola Temporal** - Clustering klaim pada waktu tertentu
                3. **Duplikasi** - Klaim berulang dalam periode singkat
                4. **Anomali Data** - Ketidaksesuaian informasi pasien
                
                Butuh analisis lebih detail untuk klaim tertentu?
                """.trimIndent()
            }

            lowerMessage.contains("statistik") || lowerMessage.contains("laporan") -> {
                """
                📊 **Statistik Fraud - November 2025**
                
                • Total Klaim Dianalisis: 1,247
                • Potensi Fraud Terdeteksi: 89 (7.1%)
                • Klaim Ditolak: 23 (1.8%)
                • Total Nilai Dicegah: Rp 2.3 Miliar
                
                Tingkat deteksi meningkat 15% dari bulan lalu! 🎯
                """.trimIndent()
            }

            lowerMessage.contains("rumah sakit") || lowerMessage.contains("rs") -> {
                """
                🏥 **Informasi Provider**
                
                Saya dapat membantu Anda menganalisis:
                
                • Track record rumah sakit
                • Pola klaim dari provider tertentu
                • Perbandingan dengan peer group
                • Riwayat investigasi
                
                Silakan sebutkan nama rumah sakit yang ingin Anda cek.
                """.trimIndent()
            }

            lowerMessage.contains("dokter") || lowerMessage.contains("dr.") -> {
                """
                👨‍⚕️ **Analisis Dokter**
                
                Untuk analisis dokter, saya dapat memberikan:
                
                • Volume klaim 90 hari terakhir
                • Pola diagnosis dan tindakan
                • Perbandingan dengan dokter sejawat
                • Alert jika ada anomali
                
                Mohon berikan nama lengkap dokter yang ingin dianalisis.
                """.trimIndent()
            }

            lowerMessage.contains("halo") || lowerMessage.contains("hai") || lowerMessage.contains("hello") -> {
                """
                👋 Halo! Selamat datang di BPJS JKB Assistant.
                
                Saya dapat membantu Anda dengan:
                
                • Cek status klaim
                • Analisis potensi fraud
                • Informasi provider (RS/Dokter)
                • Statistik dan laporan
                
                Silakan tanyakan apa yang Anda butuhkan!
                """.trimIndent()
            }

            lowerMessage.contains("terima kasih") || lowerMessage.contains("thanks") -> {
                """
                🙏 Sama-sama! Senang bisa membantu.
                
                Jika ada pertanyaan lain tentang deteksi fraud atau klaim, jangan ragu untuk bertanya kapan saja.
                
                Semoga hari Anda menyenangkan! 😊
                """.trimIndent()
            }

            lowerMessage.contains("bantuan") || lowerMessage.contains("help") -> {
                """
                💡 **Panduan Penggunaan**
                
                Anda dapat bertanya tentang:
                
                1. **Status Klaim** - "Cek klaim CLM-2025-001234"
                2. **Analisis Fraud** - "Apa indikator fraud?"
                3. **Provider** - "Analisis RS Harapan Sehat"
                4. **Statistik** - "Tampilkan statistik bulan ini"
                
                Ketik pertanyaan Anda dengan bahasa natural!
                """.trimIndent()
            }

            else -> {
                """
                🤔 Maaf, saya belum sepenuhnya memahami pertanyaan Anda.
                
                Saya dapat membantu dengan:
                • Status klaim
                • Deteksi fraud
                • Analisis provider
                • Statistik dan laporan
                
                Bisa tolong perjelas pertanyaan Anda?
                """.trimIndent()
            }
        }
    }
}
