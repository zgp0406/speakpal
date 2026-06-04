package com.zgp.speakpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

val AppPurple = Color(0xFF6C55F6)
val AppPurpleDeep = Color(0xFF5A45EE)
val AppPurpleSoft = Color(0xFFEAE7FF)
val AppBlue = Color(0xFF55A8FF)
val AppGreen = Color(0xFF28C58B)
val AppOrange = Color(0xFFFFA94D)
val AppPink = Color(0xFFFFE8EC)
val AppSurface = Color(0xFFFFFFFF)
val AppBackground = Color(0xFFF7F8FF)
val AppText = Color(0xFF141824)
val AppMuted = Color(0xFF6D7280)

@Composable
fun AppScreen(content: @Composable () -> Unit) {
    // 全局页面背景统一为浅色渐变，保持各页面视觉一致。
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF8FAFF), Color(0xFFF2F4FF)),
                ),
            ),
    ) {
        content()
    }
}

@Composable
fun ScreenTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: String? = null,
) {
    // 顶栏兼容首页时间展示和二级页面返回入口。
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = if (onBack == null) "9:41" else "‹",
            modifier = if (onBack == null) Modifier else Modifier.clickable { onBack() },
            color = AppText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(title, color = AppText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(trailing ?: "⌁", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    containerColor: Color = AppSurface,
    content: @Composable () -> Unit,
) {
    // 通用圆角卡片用于承载训练计划、报告和个人信息模块。
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        content()
    }
}

@Composable
fun PrimaryGradientButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    // 主操作按钮统一使用高对比紫色，突出当前页面的关键动作。
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppPurple,
            disabledContainerColor = AppPurpleSoft,
        ),
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MetricBar(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = AppText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text("$value", color = AppText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { value / 100f },
            color = color,
            trackColor = Color(0xFFE9ECF6),
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
    }
}

@Composable
fun WordChip(text: String, selected: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color(0xFFBFF7DD) else Color(0xFFF0F2FF))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = if (selected) Color(0xFF006F45) else AppText, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AvatarBadge(modifier: Modifier = Modifier, label: String = "AI") {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color.White, AppPurpleSoft, Color(0xFFD9ECFF))))
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = AppPurpleDeep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
    }
}

@Composable
fun BottomTabs(
    active: String,
    onHome: () -> Unit,
    onTraining: () -> Unit,
    onReports: () -> Unit,
    onProfile: () -> Unit,
) {
    // 底部导航由外部传入跳转回调，避免 UI 组件直接依赖 NavController。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(AppSurface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        listOf("首页", "训练", "报告", "我的").forEach { item ->
            val targetAction = when (item) {
                "首页" -> onHome
                "训练" -> onTraining
                "报告" -> onReports
                else -> onProfile
            }
            Column(
                modifier = Modifier.clickable { targetAction() },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = when (item) {
                        "首页" -> "⌂"
                        "训练" -> "🎙"
                        "报告" -> "▣"
                        else -> "●"
                    },
                    textAlign = TextAlign.Center,
                    color = if (item == active) AppPurple else AppMuted,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    item,
                    color = if (item == active) AppPurple else AppMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (item == active) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, trailing: String? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = AppText, fontWeight = FontWeight.Bold)
        if (trailing != null) {
            Text(trailing, color = AppMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SpacerWidth(width: Int) {
    Spacer(modifier = Modifier.width(width.dp))
}
