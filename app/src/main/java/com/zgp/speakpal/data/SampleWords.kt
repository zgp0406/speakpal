package com.zgp.speakpal.data

data class SampleWord(
    val id: String,
    val text: String,
    val phonetic: String,
    val hint: String,
)

object SampleWords {
    val all = listOf(
        SampleWord("cat", "cat", "/kæt/", "嘴巴张开一点，注意 /æ/ 的发音位置。"),
        SampleWord("dog", "dog", "/dɒɡ/", "发音时嘴唇自然放松，注意尾音 /g/。"),
        SampleWord("ship", "ship", "/ʃɪp/", "嘴唇稍微前收，舌尖不要太靠前。"),
        SampleWord("tree", "tree", "/triː/", "注意 /t/ 和 /r/ 的连读动作。"),
    )

    fun findById(id: String): SampleWord {
        return all.firstOrNull { it.id == id } ?: all.first()
    }
}
