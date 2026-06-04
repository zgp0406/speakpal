package com.zgp.speakpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zgp.speakpal.data.SampleWords

@Composable
fun ResultScreen(
    wordId: String,
    score: Int,
    onBackToPractice: () -> Unit,
    onViewRecords: () -> Unit,
) {
    val word = SampleWords.findById(wordId)
    val level = if (score >= 90) "优秀" else if (score >= 80) "良好" else "待加强"

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenTopBar(title = "评测结果", onBack = onBackToPractice)

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
                        MetricBar("准确度", 85, AppGreen, Modifier.weight(1f))
                        MetricBar("流利度", 78, AppOrange, Modifier.weight(1f))
                        MetricBar("完整度", 83, AppBlue, Modifier.weight(1f))
                    }
                }
            }

            SectionTitle("发现错误音素")
            SoftCard(containerColor = Color(0xFFFFF4F6)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFCBD2)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("/θ/", color = Color(0xFFE83F54), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("你读成了 /s/", color = AppText, fontWeight = FontWeight.Bold)
                        Text("舌尖位置不正确，气流从舌尖两侧通过。", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            SectionTitle("AI 数字人教练")
            SoftCard(
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFEFF3FF),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Color(0xFFF4F7FF), Color(0xFFE8EDFF))))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarBadge(label = "AI")
                        SpacerWidth(14)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White)
                                .padding(14.dp),
                        ) {
                            Text("你把 /θ/ 发成了 /s/", color = AppText, fontWeight = FontWeight.Bold)
                            Text("正确发音时，舌尖要轻触上下门牙之间。", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GuidancePill("查看口型")
                        GuidancePill("查看舌位")
                        GuidancePill("跟我练")
                    }

                    Text("当前单词：${word.text} ${word.phonetic}", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                }
            }

            PrimaryGradientButton(text = "下一题", onClick = onViewRecords)
        }
    }
}

@Composable
private fun GuidancePill(text: String) {
    // 教练操作胶囊用于承载口型、舌位和跟练入口。
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 9.dp),
    ) {
        Text(text, color = AppPurpleDeep, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
