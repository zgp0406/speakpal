package com.zgp.speakpal.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class PracticeRecord(
    val id: Long,
    val wordId: String,
    val wordText: String,
    val phonetic: String,
    val recognizedText: String,
    val recognitionProvider: String,
    val score: Int,
    val accuracyScore: Int,
    val fluencyScore: Int,
    val integrityScore: Int,
    val issuePhoneme: String,
    val mistakenAs: String,
    val feedbackTitle: String,
    val feedback: String,
    val airflow: String,
    val tonguePosition: Float,
    val practicedAt: String,
    val audioPath: String?,
)

class PracticeRecordStore(context: Context) {
    private val preferences = context.getSharedPreferences("practice_history", Context.MODE_PRIVATE)

    fun add(
        word: SampleWord,
        analysis: PracticeAnalysis,
        audioPath: String,
    ): PracticeRecord {
        val evaluation = analysis.evaluation
        val record = PracticeRecord(
            id = System.currentTimeMillis(),
            wordId = word.id,
            wordText = word.text,
            phonetic = word.phonetic,
            recognizedText = analysis.recognition.recognizedText,
            recognitionProvider = analysis.recognition.provider,
            score = evaluation.totalScore,
            accuracyScore = evaluation.accuracyScore,
            fluencyScore = evaluation.fluencyScore,
            integrityScore = evaluation.integrityScore,
            issuePhoneme = evaluation.guidance.target,
            mistakenAs = evaluation.guidance.mistakenAs,
            feedbackTitle = evaluation.guidance.title,
            feedback = evaluation.guidance.instruction,
            airflow = evaluation.guidance.airflow,
            tonguePosition = evaluation.guidance.tonguePosition,
            practicedAt = chinaDateFormatter().format(Date()),
            audioPath = audioPath,
        )
        val records = listOf(record) + getAll()
        save(records.take(MAX_RECORDS))
        return record
    }

    fun getAll(): List<PracticeRecord> {
        val raw = preferences.getString(KEY_RECORDS, null) ?: return sampleRecords
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    add(array.getJSONObject(index).toRecord())
                }
            }
        }.getOrElse { sampleRecords }
    }

    fun latestForWord(wordId: String): PracticeRecord? {
        return getAll().firstOrNull { it.wordId == wordId }
    }

    private fun save(records: List<PracticeRecord>) {
        val array = JSONArray()
        records.forEach { array.put(it.toJson()) }
        preferences.edit().putString(KEY_RECORDS, array.toString()).apply()
    }

    private fun PracticeRecord.toJson() = JSONObject().apply {
        put("id", id)
        put("wordId", wordId)
        put("wordText", wordText)
        put("phonetic", phonetic)
        put("recognizedText", recognizedText)
        put("recognitionProvider", recognitionProvider)
        put("score", score)
        put("accuracyScore", accuracyScore)
        put("fluencyScore", fluencyScore)
        put("integrityScore", integrityScore)
        put("issuePhoneme", issuePhoneme)
        put("mistakenAs", mistakenAs)
        put("feedbackTitle", feedbackTitle)
        put("feedback", feedback)
        put("airflow", airflow)
        put("tonguePosition", tonguePosition.toDouble())
        put("practicedAt", practicedAt)
        put("audioPath", audioPath)
    }

    private fun JSONObject.toRecord() = PracticeRecord(
        id = optLong("id"),
        wordId = optString("wordId"),
        wordText = optString("wordText"),
        phonetic = optString("phonetic"),
        recognizedText = optString("recognizedText", optString("wordText")),
        recognitionProvider = optString("recognitionProvider", "legacy_demo"),
        score = optInt("score"),
        accuracyScore = optInt("accuracyScore"),
        fluencyScore = optInt("fluencyScore"),
        integrityScore = optInt("integrityScore"),
        issuePhoneme = optString("issuePhoneme"),
        mistakenAs = optString("mistakenAs"),
        feedbackTitle = optString("feedbackTitle"),
        feedback = optString("feedback"),
        airflow = optString("airflow"),
        tonguePosition = optDouble("tonguePosition", 0.5).toFloat(),
        practicedAt = optString("practicedAt"),
        audioPath = optString("audioPath").takeIf { it.isNotBlank() && it != "null" },
    )

    companion object {
        private const val KEY_RECORDS = "records"
        private const val MAX_RECORDS = 100

        private val sampleRecords = listOf(
            PracticeRecord(3, "cat", "cat", "/kæt/", "cat", "sample", 86, 88, 81, 89, "/æ/", "/e/", "嘴巴需要张得更开", "舌位低而靠前，下巴自然下沉。", "发音短促，嘴唇不要收圆。", 0.35f, "2026.06.04", null),
            PracticeRecord(2, "ship", "ship", "/ʃɪp/", "ship", "sample", 82, 84, 78, 83, "/ʃ/", "/s/", "舌尖不要贴近牙齿", "舌尖略微后缩，舌面抬向硬腭。", "嘴唇稍微前突，让气流从舌面中央通过。", 0.58f, "2026.06.03", null),
            PracticeRecord(1, "red", "red", "/red/", "red", "sample", 90, 91, 86, 92, "/r/", "/l/", "舌尖向后卷起", "舌尖抬起但不要接触上颚。", "嘴唇略微收圆，声音保持连续。", 0.66f, "2026.06.02", null),
        )

        private fun chinaDateFormatter(): SimpleDateFormat {
            // 演示数据统一按项目所在地时区记录，避免模拟器使用 UTC 导致日期偏差。
            return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA).apply {
                timeZone = TimeZone.getTimeZone("Asia/Shanghai")
            }
        }
    }
}
