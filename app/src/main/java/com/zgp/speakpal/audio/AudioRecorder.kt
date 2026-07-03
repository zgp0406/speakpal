package com.zgp.speakpal.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.math.max

class AudioRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var pcmFile: File? = null
    private var wavFile: File? = null
    private val recordingError = AtomicReference<Throwable?>(null)

    @Volatile
    private var isRecording = false

    @SuppressLint("MissingPermission")
    fun start(): Result<String> = runCatching {
        check(!isRecording) { "录音已经开始" }

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        check(minBufferSize > 0) { "设备不支持 16kHz 单声道录音" }

        val directory = File(context.filesDir, "recordings").apply { mkdirs() }
        val timestamp = System.currentTimeMillis()
        val rawFile = File(directory, "practice_$timestamp.pcm")
        val targetFile = File(directory, "practice_$timestamp.wav")
        val bufferSize = max(minBufferSize * 2, SAMPLE_RATE)
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
        )
        check(recorder.state == AudioRecord.STATE_INITIALIZED) { "麦克风初始化失败" }

        pcmFile = rawFile
        wavFile = targetFile
        audioRecord = recorder
        recordingError.set(null)
        isRecording = true
        recorder.startRecording()
        recordingThread = thread(name = "SpeakPalPcmRecorder") {
            writePcmStream(recorder, rawFile, bufferSize)
        }
        targetFile.absolutePath
    }

    fun stop(): Result<String> = runCatching {
        val recorder = checkNotNull(audioRecord) { "当前没有正在进行的录音" }
        val rawFile = checkNotNull(pcmFile)
        val targetFile = checkNotNull(wavFile)

        isRecording = false
        runCatching { recorder.stop() }
        recordingThread?.join(RECORDING_THREAD_TIMEOUT_MS)
        recorder.release()
        audioRecord = null
        recordingThread = null

        recordingError.getAndSet(null)?.let { throw IllegalStateException("录音写入失败", it) }
        check(rawFile.exists() && rawFile.length() >= MIN_PCM_BYTES) { "录音时间过短，请重新录制" }

        writeWavFile(rawFile, targetFile)
        rawFile.delete()
        check(targetFile.exists() && targetFile.length() > WAV_HEADER_SIZE) { "未生成有效录音文件" }
        pcmFile = null
        wavFile = null
        targetFile.absolutePath
    }.onFailure {
        cleanupFiles()
    }

    fun release() {
        isRecording = false
        audioRecord?.runCatching { stop() }
        recordingThread?.join(RECORDING_THREAD_TIMEOUT_MS)
        audioRecord?.release()
        audioRecord = null
        recordingThread = null
        recordingError.set(null)
        cleanupFiles()
    }

    private fun writePcmStream(recorder: AudioRecord, file: File, bufferSize: Int) {
        runCatching {
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(bufferSize)
                while (isRecording) {
                    val bytesRead = recorder.read(buffer, 0, buffer.size)
                    when {
                        bytesRead > 0 -> output.write(buffer, 0, bytesRead)
                        bytesRead == AudioRecord.ERROR_INVALID_OPERATION && !isRecording -> break
                        bytesRead < 0 -> error("AudioRecord 读取失败：$bytesRead")
                    }
                }
            }
        }.onFailure { recordingError.compareAndSet(null, it) }
    }

    private fun writeWavFile(rawFile: File, targetFile: File) {
        val pcmSize = rawFile.length()
        FileOutputStream(targetFile).use { output ->
            output.write(createWavHeader(pcmSize))
            FileInputStream(rawFile).use { input ->
                input.copyTo(output)
            }
        }
    }

    private fun createWavHeader(pcmSize: Long): ByteArray {
        val byteRate = SAMPLE_RATE * CHANNEL_COUNT * BITS_PER_SAMPLE / 8
        return ByteArray(WAV_HEADER_SIZE).apply {
            putAscii(0, "RIFF")
            putIntLittleEndian(4, (pcmSize + 36).toInt())
            putAscii(8, "WAVE")
            putAscii(12, "fmt ")
            putIntLittleEndian(16, 16)
            putShortLittleEndian(20, 1)
            putShortLittleEndian(22, CHANNEL_COUNT)
            putIntLittleEndian(24, SAMPLE_RATE)
            putIntLittleEndian(28, byteRate)
            putShortLittleEndian(32, CHANNEL_COUNT * BITS_PER_SAMPLE / 8)
            putShortLittleEndian(34, BITS_PER_SAMPLE)
            putAscii(36, "data")
            putIntLittleEndian(40, pcmSize.toInt())
        }
    }

    private fun cleanupFiles() {
        pcmFile?.delete()
        wavFile?.delete()
        pcmFile = null
        wavFile = null
    }

    private fun ByteArray.putAscii(offset: Int, value: String) {
        value.encodeToByteArray().copyInto(this, offset)
    }

    private fun ByteArray.putIntLittleEndian(offset: Int, value: Int) {
        this[offset] = value.toByte()
        this[offset + 1] = (value shr 8).toByte()
        this[offset + 2] = (value shr 16).toByte()
        this[offset + 3] = (value shr 24).toByte()
    }

    private fun ByteArray.putShortLittleEndian(offset: Int, value: Int) {
        this[offset] = value.toByte()
        this[offset + 1] = (value shr 8).toByte()
    }

    companion object {
        const val SAMPLE_RATE = 16_000
        const val CHANNEL_COUNT = 1
        const val BITS_PER_SAMPLE = 16
        const val WAV_HEADER_SIZE = 44

        private const val MIN_PCM_BYTES = SAMPLE_RATE * BITS_PER_SAMPLE / 8 / 2
        private const val RECORDING_THREAD_TIMEOUT_MS = 2_000L
    }
}
