package com.zgp.speakpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zgp.speakpal.audio.AudioPlaybackController
import com.zgp.speakpal.data.PracticeRecord
import com.zgp.speakpal.data.PracticeRecordStore

@Composable
fun RecordsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val context = LocalContext.current
    val records = remember { PracticeRecordStore(context).getAll() }
    val averageScore = records.map { it.score }.average().toInt()
    val playback = remember { AudioPlaybackController() }
    var playingRecordId by remember { mutableStateOf<Long?>(null) }
    var playbackMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose { playback.release() }
    }

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenTopBar(title = "练习记录", onBack = onBack, trailing = "▣")

            SectionTitle("最近练习", trailing = "按时间倒序")

            SoftCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("练习概览", color = AppText, fontWeight = FontWeight.Bold)
                        Text("本机数据", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("78", color = AppText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text(" 分  →  ", color = AppMuted)
                        Text("$averageScore", color = AppText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text(" 分", color = AppMuted)
                        Box(modifier = Modifier.weight(1f))
                        Text("共 ${records.size} 次 ›", color = AppGreen, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    MetricBar("平均得分", averageScore, AppPurple)
                }
            }

            playbackMessage?.let {
                Text(it, color = AppMuted, style = MaterialTheme.typography.bodySmall)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(records, key = { it.id }) { record ->
                    RecordRow(
                        record = record,
                        isPlaying = playingRecordId == record.id,
                        onPlay = {
                            val path = record.audioPath
                            if (path == null) {
                                playbackMessage = "示例记录没有可回放的录音"
                            } else {
                                playingRecordId = record.id
                                playbackMessage = "正在回放 ${record.wordText}"
                                playback.play(path) {
                                    playingRecordId = null
                                    playbackMessage = "录音回放完成"
                                }.onFailure {
                                    playingRecordId = null
                                    playbackMessage = it.message ?: "录音播放失败"
                                }
                            }
                        },
                    )
                }
            }

            BottomTabs(
                active = "报告",
                onHome = onOpenHome,
                onTraining = onOpenTraining,
                onReports = {},
                onProfile = onOpenProfile,
            )
        }
    }
}

@Composable
private fun RecordRow(
    record: PracticeRecord,
    isPlaying: Boolean,
    onPlay: () -> Unit,
) {
    // 单条记录展示分数、评级和回放入口。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(record.wordText.replaceFirstChar { it.uppercase() }, color = AppText, fontWeight = FontWeight.Bold)
            Text(
                "${record.practicedAt}  ·  ${record.issuePhoneme}",
                color = AppMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text("得分 ${record.score}", color = AppText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        SpacerWidth(8)
        Text(
            text = if (record.score >= 85) "优秀" else "良好",
            color = if (record.score >= 85) AppGreen else AppOrange,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
        SpacerWidth(10)
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (record.audioPath == null) Color(0xFFF0F1F5) else AppPurpleSoft)
                .clickable { onPlay() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (isPlaying) "■" else "▶",
                color = if (record.audioPath == null) AppMuted else AppPurpleDeep,
            )
        }
    }
}
