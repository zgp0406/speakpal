package com.zgp.speakpal.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zgp.speakpal.audio.AudioPlaybackController
import com.zgp.speakpal.data.MockPronunciationEvaluator
import com.zgp.speakpal.data.PhonemeGuidance
import com.zgp.speakpal.data.PracticeRecordStore
import com.zgp.speakpal.data.SampleWords

@Composable
fun ResultScreen(
    wordId: String,
    score: Int,
    onBackToPractice: () -> Unit,
    onViewRecords: () -> Unit,
) {
    val context = LocalContext.current
    val word = SampleWords.findById(wordId)
    val fallback = MockPronunciationEvaluator.evaluate(word)
    val record = remember(wordId) { PracticeRecordStore(context).latestForWord(wordId) }
    val totalScore = record?.score ?: score
    val accuracy = record?.accuracyScore ?: fallback.accuracyScore
    val fluency = record?.fluencyScore ?: fallback.fluencyScore
    val integrity = record?.integrityScore ?: fallback.integrityScore
    val guidance = record?.let {
        PhonemeGuidance(
            target = it.issuePhoneme,
            mistakenAs = it.mistakenAs,
            title = it.feedbackTitle,
            instruction = it.feedback,
            airflow = it.airflow,
            tonguePosition = it.tonguePosition,
        )
    } ?: fallback.guidance

    val playback = remember { AudioPlaybackController() }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackMessage by remember { mutableStateOf<String?>(null) }
    val level = when {
        totalScore >= 90 -> "优秀"
        totalScore >= 80 -> "良好"
        else -> "待加强"
    }

    DisposableEffect(Unit) {
        onDispose { playback.release() }
    }

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScreenTopBar(title = "评测结果", onBack = onBackToPractice)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ScoreSummary(totalScore, level, accuracy, fluency, integrity)

                record?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("识别文本", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                            Text(it.recognizedText, color = AppText, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            if (it.recognitionProvider == "local_demo") "离线演示" else "真实识别",
                            color = if (it.recognitionProvider == "local_demo") AppOrange else AppGreen,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                SectionTitle("需要改进的音素")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFF1F3))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFCBD2)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(guidance.target, color = Color(0xFFE83F54), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("听起来接近 ${guidance.mistakenAs}", color = AppText, fontWeight = FontWeight.Bold)
                        Text(guidance.title, color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }

                SectionTitle("口型与舌位")
                ArticulationGuide(guidance)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEFF3FF))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    Text("纠错提示", color = AppPurpleDeep, fontWeight = FontWeight.Bold)
                    Text(guidance.instruction, color = AppText)
                    Text(guidance.airflow, color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    Text("当前单词：${word.text}  ${word.phonetic}", color = AppPurpleDeep, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                if (record?.audioPath != null) {
                    PrimaryGradientButton(
                        text = if (isPlaying) "正在播放我的录音…" else "回放我的录音",
                        enabled = !isPlaying,
                        onClick = {
                            isPlaying = true
                            playbackMessage = null
                            playback.play(record.audioPath) {
                                isPlaying = false
                                playbackMessage = "播放完成，可返回练习再次尝试"
                            }.onFailure {
                                isPlaying = false
                                playbackMessage = it.message ?: "录音播放失败"
                            }
                        },
                    )
                }

                playbackMessage?.let {
                    Text(
                        text = it,
                        modifier = Modifier.fillMaxWidth(),
                        color = AppMuted,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            PrimaryGradientButton(text = "查看练习记录", onClick = onViewRecords)
        }
    }
}

@Composable
private fun ScoreSummary(
    score: Int,
    level: String,
    accuracy: Int,
    fluency: Int,
    integrity: Int,
) {
    SoftCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("综合得分", color = AppText, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$score", color = AppPurple, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)
                        SpacerWidth(10)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppPurple)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(level, color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                Text("🏅", style = MaterialTheme.typography.displaySmall)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                MetricBar("准确度", accuracy, AppGreen, Modifier.weight(1f))
                MetricBar("流利度", fluency, AppOrange, Modifier.weight(1f))
                MetricBar("完整度", integrity, AppBlue, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ArticulationGuide(guidance: PhonemeGuidance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(132.dp),
        ) {
            val outline = Color(0xFF33384A)
            val mouthColor = Color(0xFFFFA5B2)
            val tongueColor = Color(0xFFE85D75)

            // 侧视口腔轮廓与舌位点，位置由音素映射数据驱动。
            drawOval(
                color = mouthColor,
                topLeft = Offset(size.width * 0.08f, size.height * 0.12f),
                size = Size(size.width * 0.84f, size.height * 0.76f),
                style = Stroke(width = 8f),
            )
            drawLine(
                color = outline,
                start = Offset(size.width * 0.24f, size.height * 0.58f),
                end = Offset(size.width * 0.80f, size.height * 0.58f),
                strokeWidth = 5f,
            )
            drawOval(
                color = tongueColor,
                topLeft = Offset(size.width * guidance.tonguePosition * 0.58f, size.height * 0.48f),
                size = Size(size.width * 0.30f, size.height * 0.20f),
            )
            drawCircle(
                color = AppPurple,
                radius = 9f,
                center = Offset(size.width * guidance.tonguePosition, size.height * 0.52f),
            )
        }

        Column(
            modifier = Modifier.weight(1.15f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("${guidance.target} 发音位置", color = AppText, fontWeight = FontWeight.Bold)
            Text(guidance.instruction, color = AppMuted, style = MaterialTheme.typography.bodySmall)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppPurpleSoft)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            ) {
                Text("紫点表示舌尖位置", color = AppPurpleDeep, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
