package com.zgp.speakpal.data

data class PracticeRecord(
    val wordText: String,
    val phonetic: String,
    val score: Int,
    val issuePhoneme: String,
    val practicedAt: String,
)

// 练习记录数据模型后续可替换为数据库实体。
object SamplePracticeRecords {
    // 本地演示记录用于先跑通学习记录页面。
    val all = listOf(
        PracticeRecord("cat", "/kæt/", 86, "/æ/", "2026.06.04"),
        PracticeRecord("ship", "/ʃɪp/", 82, "/ʃ/", "2026.06.03"),
        PracticeRecord("tree", "/triː/", 90, "/r/", "2026.06.02"),
    )

    // 平均分用于学习报告页的趋势摘要展示。
    val averageScore: Int = all.map { it.score }.average().toInt()
}
