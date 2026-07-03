package com.zgp.speakpal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zgp.speakpal.audio.AudioPlaybackController
import com.zgp.speakpal.audio.AudioRecorder
import com.zgp.speakpal.data.LocalDemoPronunciationEvaluationService
import com.zgp.speakpal.data.LocalDemoSpeechRecognitionService
import com.zgp.speakpal.data.PracticeAnalysisPipeline
import com.zgp.speakpal.data.PracticeRecordStore
import com.zgp.speakpal.data.SampleWords
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PracticeScreen(
    wordId: String,
    onBack: () -> Unit,
    onSubmit: (Int) -> Unit,
) {
    val context = LocalContext.current
    val word = SampleWords.findById(wordId)
    val recorder = remember { AudioRecorder(context) }
    val playback = remember { AudioPlaybackController() }
    val recordStore = remember { PracticeRecordStore(context) }
    val analysisPipeline = remember {
        PracticeAnalysisPipeline(
            recognitionService = LocalDemoSpeechRecognitionService,
            evaluationService = LocalDemoPronunciationEvaluationService,
        )
    }
    val coroutineScope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(false) }
    var isPlayingRecording by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recordedSeconds by remember { mutableIntStateOf(0) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf("先听标准发音，再录下你的跟读") }
    var ttsReady by remember { mutableStateOf(false) }

    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    fun startRecording() {
        playback.release()
        isPlayingRecording = false
        recorder.start().fold(
            onSuccess = {
                audioPath = null
                recordedSeconds = 0
                isRecording = true
                statusMessage = "正在录音，请清晰朗读 ${word.text}"
            },
            onFailure = {
                statusMessage = it.message ?: "无法开始录音，请检查麦克风"
            },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            startRecording()
        } else {
            statusMessage = "需要麦克风权限才能录音，请在系统设置中允许"
        }
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1_000)
            recordedSeconds += 1
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.release()
            playback.release()
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScreenTopBar(title = "单词训练", onBack = onBack, trailing = "${SampleWords.all.indexOf(word) + 1}/${SampleWords.all.size}")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF0F3FF))
                    .clickable(enabled = ttsReady && !isRecording) {
                        textToSpeech.language = Locale.US
                        textToSpeech.speak(word.text, TextToSpeech.QUEUE_FLUSH, null, "standard_word")
                        statusMessage = "正在播放标准发音"
                    }
                    .padding(vertical = 30.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    word.text.replaceFirstChar { it.uppercase() },
                    color = AppText,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(word.phonetic, color = AppMuted, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (ttsReady) "点击单词播放标准发音  ♪" else "正在准备标准发音…",
                    color = AppPurpleDeep,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = statusMessage,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = if (isRecording) Color(0xFFE83F54) else AppText,
                fontWeight = FontWeight.Bold,
            )

            Waveform(active = isRecording)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isRecording) 104.dp else 92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    if (isRecording) {
                                        listOf(Color(0xFFFF6D7E), Color(0xFFE83F54))
                                    } else {
                                        listOf(AppPurple, AppPurpleDeep)
                                    },
                                ),
                            )
                            .clickable {
                                if (isRecording) {
                                    recorder.stop().fold(
                                        onSuccess = {
                                            audioPath = it
                                            isRecording = false
                                            statusMessage = "录音完成，可回放或提交评测"
                                        },
                                        onFailure = {
                                            isRecording = false
                                            statusMessage = it.message ?: "录音失败，请重新录制"
                                        },
                                    )
                                } else if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    startRecording()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(if (isRecording) "■" else "🎙", color = Color.White, style = MaterialTheme.typography.displaySmall)
                    }
                    Text(
                        if (isRecording) "点击停止  ${formatDuration(recordedSeconds)}" else "点击开始录音",
                        color = AppMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (audioPath != null) {
                PrimaryGradientButton(
                    text = if (isPlayingRecording) "正在播放我的录音…" else "回放我的录音",
                    enabled = !isPlayingRecording,
                    onClick = {
                        isPlayingRecording = true
                        playback.play(audioPath.orEmpty()) {
                            isPlayingRecording = false
                            statusMessage = "录音回放完成"
                        }.onFailure {
                            isPlayingRecording = false
                            statusMessage = it.message ?: "录音播放失败"
                        }
                    },
                )
            }

            PrimaryGradientButton(
                text = if (isAnalyzing) "正在识别并评测…" else "提交评测",
                enabled = audioPath != null && !isRecording && !isPlayingRecording && !isAnalyzing,
                onClick = {
                    val path = audioPath ?: return@PrimaryGradientButton
                    if (isAnalyzing) return@PrimaryGradientButton
                    isAnalyzing = true
                    statusMessage = "正在识别朗读内容…"
                    coroutineScope.launch {
                        analysisPipeline.analyze(path, word).fold(
                            onSuccess = { analysis ->
                                statusMessage = "识别完成：${analysis.recognition.recognizedText}"
                                recordStore.add(word, analysis, path)
                                isAnalyzing = false
                                onSubmit(analysis.evaluation.totalScore)
                            },
                            onFailure = {
                                isAnalyzing = false
                                statusMessage = it.message ?: "识别或评测失败，请重新录制"
                            },
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun Waveform(active: Boolean) {
    // 录音时使用更高对比度的波形，帮助用户确认麦克风状态。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val bars = listOf(16, 24, 30, 36, 28, 42, 22, 34, 48, 30, 38, 26, 20, 32, 24, 18)
        bars.forEachIndexed { index, height ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(
                        width = 4.dp,
                        height = if (active && index % 3 == 0) (height + 8).dp else height.dp,
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (active) Color(0xFFE83F54) else AppPurpleSoft),
            )
        }
    }
}

private fun formatDuration(seconds: Int): String {
    return "%02d:%02d".format(seconds / 60, seconds % 60)
}
