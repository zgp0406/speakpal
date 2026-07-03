package com.zgp.speakpal.data

data class PhonemeGuidance(
    val target: String,
    val mistakenAs: String,
    val title: String,
    val instruction: String,
    val airflow: String,
    val tonguePosition: Float,
)

data class PronunciationEvaluation(
    val totalScore: Int,
    val accuracyScore: Int,
    val fluencyScore: Int,
    val integrityScore: Int,
    val guidance: PhonemeGuidance,
)

object MockPronunciationEvaluator {
    private val guidanceByWord = mapOf(
        "think" to PhonemeGuidance("/θ/", "/s/", "舌尖需要向前", "舌尖轻放在上下门牙之间，不要抵住牙齿。", "保持声带不振动，让气流从舌尖周围持续通过。", 0.82f),
        "this" to PhonemeGuidance("/ð/", "/d/", "保持舌尖并振动", "舌尖轻放在上下门牙之间，位置与 /θ/ 相同。", "打开声带振动，让气流平稳通过。", 0.82f),
        "cat" to PhonemeGuidance("/æ/", "/e/", "嘴巴需要张得更开", "舌位低而靠前，下巴自然下沉。", "发音短促，嘴唇不要收圆。", 0.35f),
        "ship" to PhonemeGuidance("/ʃ/", "/s/", "舌尖不要贴近牙齿", "舌尖略微后缩，舌面抬向硬腭。", "嘴唇稍微前突，让气流从舌面中央通过。", 0.58f),
        "red" to PhonemeGuidance("/r/", "/l/", "舌尖向后卷起", "舌尖抬起但不要接触上颚。", "嘴唇略微收圆，声音保持连续。", 0.66f),
        "light" to PhonemeGuidance("/l/", "/r/", "舌尖轻触上齿龈", "舌尖接触上门牙后方的齿龈。", "让声音和气流从舌头两侧通过。", 0.62f),
    )

    fun evaluate(word: SampleWord): PronunciationEvaluation {
        // 固定结果保证离线演示稳定，同时保留与真实评测接口一致的数据边界。
        val seed = word.text.sumOf { it.code }
        val total = 80 + seed % 9
        val guidance = guidanceByWord[word.id] ?: guidanceByWord.getValue("think")
        return PronunciationEvaluation(
            totalScore = total,
            accuracyScore = (total + 2).coerceAtMost(96),
            fluencyScore = (total - 4).coerceAtLeast(70),
            integrityScore = (total + 1).coerceAtMost(98),
            guidance = guidance,
        )
    }
}
