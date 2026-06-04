package com.zgp.speakpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
fun HomeScreen(
    onStartPractice: () -> Unit,
    onViewRecords: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("早上好，淑芬 👋", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("🔥 连续学习 7 天", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                }
                AvatarBadge(label = "AI")
            }

            DailyPlanCard(onStartPractice = onStartPractice)

            SoftCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("AI 教练提醒", color = AppPurpleDeep, fontWeight = FontWeight.Bold)
                        Text("你的 /θ/ 发音错误率较高", color = AppText, style = MaterialTheme.typography.bodyMedium)
                        Text("建议多练习 /θ/ 和 /ð/ 的区分", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE7F1FF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🤖", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }

            PrimaryGradientButton(text = "查看学习报告", onClick = onViewRecords)
            Spacer(modifier = Modifier.weight(1f))
            BottomTabs(
                active = "首页",
                onHome = {},
                onTraining = onStartPractice,
                onReports = onViewRecords,
                onProfile = onOpenProfile,
            )
        }
    }
}

@Composable
private fun DailyPlanCard(onStartPractice: () -> Unit) {
    // 今日训练卡片承载首页最主要的练习入口。
    SoftCard(
        containerColor = AppPurple,
        modifier = Modifier.clip(RoundedCornerShape(24.dp)),
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(AppPurple, AppBlue)))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("今日训练计划", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("预计用时 8 分钟", color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodySmall)
                }
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0f },
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier.size(72.dp),
                    )
                    Text("0%\n完成度", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }

            Text("重点强化音素：", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WordChip("/θ/", selected = true)
                WordChip("/r/")
            }

            listOf("think", "three", "right", "red").forEachIndexed { index, word ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .clickable { if (index == 0) onStartPractice() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(AppPurpleSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${index + 1}", color = AppPurpleDeep, style = MaterialTheme.typography.labelSmall)
                    }
                    SpacerWidth(10)
                    Text(word, modifier = Modifier.weight(1f), color = AppText, fontWeight = FontWeight.Bold)
                    Text("☆  ☆", color = AppMuted)
                }
            }

            PrimaryGradientButton(text = "开始今日训练", onClick = onStartPractice)
        }
    }
}
