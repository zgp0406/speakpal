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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenRecords: () -> Unit,
) {
    AppScreen {
        // 个人中心先展示演示成就和功能入口，后续接入真实用户数据。
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenTopBar(title = "我的", trailing = "⚙")

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AvatarBadge(label = "AI")
                Column {
                    Text("淑芬", color = AppText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Lv.5 学习达人", color = AppPurpleDeep, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            SoftCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("我的成就", color = AppText, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AchievementItem("🔥", "7", "连续学习(天)")
                        AchievementItem("✅", "56", "累计练习(次)")
                        AchievementItem("🏅", "12", "获得徽章(个)")
                    }
                }
            }

            SoftCard {
                Column(modifier = Modifier.padding(6.dp)) {
                    ProfileMenuItem("◴", "学习目标", "修改")
                    ProfileMenuItem("☆", "我的收藏", "›")
                    ProfileMenuItem("▣", "错题本", "›")
                    ProfileMenuItem("⚙", "学习设置", "›")
                    ProfileMenuItem("ⓘ", "关于我们", "›")
                }
            }

            Box(modifier = Modifier.weight(1f))
            BottomTabs(
                active = "我的",
                onHome = onOpenHome,
                onTraining = onOpenTraining,
                onReports = onOpenRecords,
                onProfile = {},
            )
        }
    }
}

@Composable
private fun AchievementItem(icon: String, value: String, label: String) {
    // 成就项复用同一结构，保证三项统计对齐。
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(AppPurpleSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon)
        }
        Text(value, color = AppText, fontWeight = FontWeight.Black)
        Text(label, color = AppMuted, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ProfileMenuItem(icon: String, title: String, trailing: String) {
    // 菜单项保留跳转样式，功能页后续逐步补齐。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, color = AppText)
        SpacerWidth(12)
        Text(title, modifier = Modifier.weight(1f), color = AppText, fontWeight = FontWeight.Bold)
        Text(trailing, color = AppMuted, style = MaterialTheme.typography.bodySmall)
    }
}
