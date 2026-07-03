package com.zgp.speakpal.data

import com.zgp.speakpal.audio.AudioRecorder
import java.io.File
import java.io.RandomAccessFile

data class SpeechRecognitionResult(
    val recognizedText: String,
    val confidence: Float?,
    val provider: String,
)

data class PracticeAnalysis(
    val recognition: SpeechRecognitionResult,
    val evaluation: PronunciationEvaluation,
)

interface SpeechRecognitionService {
    suspend fun recognize(audioPath: String, expectedText: String): Result<SpeechRecognitionResult>
}

interface PronunciationEvaluationService {
    suspend fun evaluate(
        audioPath: String,
        word: SampleWord,
        recognition: SpeechRecognitionResult,
    ): Result<PronunciationEvaluation>
}

class PracticeAnalysisPipeline(
    private val recognitionService: SpeechRecognitionService,
    private val evaluationService: PronunciationEvaluationService,
) {
    suspend fun analyze(audioPath: String, word: SampleWord): Result<PracticeAnalysis> {
        return recognitionService.recognize(audioPath, word.text).fold(
            onSuccess = { recognition ->
                evaluationService.evaluate(audioPath, word, recognition).map { evaluation ->
                    PracticeAnalysis(recognition, evaluation)
                }
            },
            onFailure = { Result.failure(it) },
        )
    }
}

object LocalDemoSpeechRecognitionService : SpeechRecognitionService {
    override suspend fun recognize(
        audioPath: String,
        expectedText: String,
    ): Result<SpeechRecognitionResult> = runCatching {
        // 当前仅验证音频格式与有效时长；接入 IAT 后由真实转写文本替换 expectedText。
        WavAudioInspector.validateForXfyun(audioPath)
        SpeechRecognitionResult(
            recognizedText = expectedText.trim().lowercase(),
            confidence = null,
            provider = "local_demo",
        )
    }
}

object LocalDemoPronunciationEvaluationService : PronunciationEvaluationService {
    override suspend fun evaluate(
        audioPath: String,
        word: SampleWord,
        recognition: SpeechRecognitionResult,
    ): Result<PronunciationEvaluation> = runCatching {
        WavAudioInspector.validateForXfyun(audioPath)
        MockPronunciationEvaluator.evaluate(word)
    }
}

object WavAudioInspector {
    fun validateForXfyun(path: String) {
        val file = File(path)
        check(file.exists() && file.length() > AudioRecorder.WAV_HEADER_SIZE) { "录音文件无效" }

        RandomAccessFile(file, "r").use { input ->
            val riff = ByteArray(4).also { input.readFully(it) }.decodeToString()
            input.seek(8)
            val wave = ByteArray(4).also { input.readFully(it) }.decodeToString()
            input.seek(22)
            val channels = input.readLittleEndianShort()
            val sampleRate = input.readLittleEndianInt()
            input.seek(34)
            val bitsPerSample = input.readLittleEndianShort()

            check(riff == "RIFF" && wave == "WAVE") { "录音不是有效 WAV 文件" }
            check(channels == AudioRecorder.CHANNEL_COUNT) { "录音必须为单声道" }
            check(sampleRate == AudioRecorder.SAMPLE_RATE) { "录音采样率必须为 16kHz" }
            check(bitsPerSample == AudioRecorder.BITS_PER_SAMPLE) { "录音位深必须为 16bit" }
        }
    }

    private fun RandomAccessFile.readLittleEndianShort(): Int {
        val low = readUnsignedByte()
        val high = readUnsignedByte()
        return low or (high shl 8)
    }

    private fun RandomAccessFile.readLittleEndianInt(): Int {
        return readUnsignedByte() or
            (readUnsignedByte() shl 8) or
            (readUnsignedByte() shl 16) or
            (readUnsignedByte() shl 24)
    }
}
