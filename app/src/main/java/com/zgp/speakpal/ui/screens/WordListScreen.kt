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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zgp.speakpal.data.SampleWords

@Composable
fun WordListScreen(
    onBack: () -> Unit,
    onWordSelected: (String) -> Unit,
    onOpenHome: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenTopBar(title = "AI 训练计划", onBack = onBack, trailing = "更多计划 ›")

            SoftCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("本周训练目标", fontWeight = FontWeight.Bold, color = AppText)
                        Text("提升薄弱音素发音准确率", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                        Text("目标 > 85%", color = AppPurpleDeep, fontWeight = FontWeight.Bold)
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { 0.6f },
                            color = AppPurple,
                            trackColor = AppPurpleSoft,
                            modifier = Modifier.size(72.dp),
                        )
                        Text("60%", color = AppText, fontWeight = FontWeight.Bold)
                    }
                }
            }

            SectionTitle("本周重点强化音素")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PhonemeFocusCard("/θ/", "错误率 32%", AppPink, Modifier.weight(1f))
                PhonemeFocusCard("/r/", "错误率 21%", Color(0xFFE6FAF1), Modifier.weight(1f))
            }

            SectionTitle("今日推荐练习")
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(SampleWords.all) { index, word ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(AppSurface)
                            .clickable { onWordSelected(word.id) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(AppPurpleSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("${index + 1}", color = AppPurpleDeep, style = MaterialTheme.typography.labelSmall)
                        }
                        SpacerWidth(12)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(word.text, color = AppText, fontWeight = FontWeight.Bold)
                            Text(word.phonetic, color = AppMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(AppPurple)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text("去练习", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            BottomTabs(
                active = "训练",
                onHome = onOpenHome,
                onTraining = {},
                onReports = onOpenRecords,
                onProfile = onOpenProfile,
            )
        }
    }
}

@Composable
private fun PhonemeFocusCard(
    phoneme: String,
    detail: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    // 音素卡片突出本周需要强化的发音问题。
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(phoneme, color = Color(0xFFE83F54), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Text(detail, color = AppText, style = MaterialTheme.typography.bodySmall)
        Text("需加强", color = AppGreen, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
