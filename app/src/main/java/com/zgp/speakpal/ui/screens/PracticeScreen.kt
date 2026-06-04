package com.zgp.speakpal.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zgp.speakpal.data.SampleWords
import kotlinx.coroutines.delay

@Composable
fun PracticeScreen(
    wordId: String,
    onBack: () -> Unit,
    onSubmit: (Int) -> Unit,
) {
    val word = SampleWords.findById(wordId)
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 80) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackMessage by remember { mutableStateOf("请跟读上方单词") }

    // 页面销毁时释放占位音频播放器资源。
    DisposableEffect(Unit) {
        onDispose { toneGenerator.release() }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            delay(800)
            toneGenerator.stopTone()
            isPlaying = false
            playbackMessage = "录音中... 松开发结束"
        }
    }

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ScreenTopBar(title = "单词训练", onBack = onBack, trailing = "1/4")

            SoftCard(containerColor = Color(0xFFF4F6FC)) {
                Column(
                    modifier = Modifier.padding(vertical = 34.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(word.text.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(word.phonetic, color = AppText)
                        SpacerWidth(8)
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(AppPurpleSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("♪", color = AppPurpleDeep)
                        }
                    }
                }
            }

            Text(
                text = playbackMessage,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AppText,
                fontWeight = FontWeight.Bold,
            )

            Waveform()

            Text(
                text = "当前使用占位提示音模拟标准发音，后续替换为真实单词音频。",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AppMuted,
                style = MaterialTheme.typography.bodySmall,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(AppPurple, AppPurpleDeep))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🎙", color = Color.White, style = MaterialTheme.typography.displaySmall)
                }
            }

            PrimaryGradientButton(
                text = if (isPlaying) "播放中" else "播放标准音并模拟录音",
                enabled = !isPlaying,
                onClick = {
                    val started = toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 700)
                    isPlaying = started
                    playbackMessage = if (started) "正在播放占位标准音..." else "占位标准音播放失败，请检查设备音量或音频输出。"
                },
            )
            PrimaryGradientButton(text = "提交评测", onClick = { onSubmit(82) })
        }
    }
}

@Composable
private fun Waveform() {
    // 静态波形用于模拟录音过程的视觉反馈。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val bars = listOf(16, 24, 30, 36, 28, 42, 22, 34, 48, 30, 38, 26, 20, 32, 24, 18)
        bars.forEach { height ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(width = 4.dp, height = height.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppPurple),
            )
        }
    }
}
