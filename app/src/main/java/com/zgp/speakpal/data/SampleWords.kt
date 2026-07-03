package com.zgp.speakpal.data

data class SampleWord(
    val id: String,
    val text: String,
    val phonetic: String,
    val hint: String,
)

object SampleWords {
    val all = listOf(
        SampleWord("think", "think", "/θɪŋk/", "舌尖轻触上下门牙之间，让气流持续通过。"),
        SampleWord("this", "this", "/ðɪs/", "保持舌尖位置并振动声带，注意与 /θ/ 区分。"),
        SampleWord("cat", "cat", "/kæt/", "嘴巴张开一点，注意 /æ/ 的发音位置。"),
        SampleWord("ship", "ship", "/ʃɪp/", "嘴唇稍微前收，舌尖不要太靠前。"),
        SampleWord("red", "red", "/red/", "舌尖向后卷起但不要接触上颚。"),
        SampleWord("light", "light", "/laɪt/", "舌尖轻触上齿龈，声音从舌头两侧通过。"),
    )

    fun findById(id: String): SampleWord {
        return all.firstOrNull { it.id == id } ?: all.first()
    }
}
