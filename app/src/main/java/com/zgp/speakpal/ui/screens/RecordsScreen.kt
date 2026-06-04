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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.zgp.speakpal.data.PracticeRecord
import com.zgp.speakpal.data.SamplePracticeRecords

@Composable
fun RecordsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val records = SamplePracticeRecords.all

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenTopBar(title = "练习记录", onBack = onBack, trailing = "▣")

            SectionTitle("本周")
            WeekStrip()

            SoftCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("学习报告", color = AppText, fontWeight = FontWeight.Bold)
                        Text("5.20 - 5.26 ˅", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("78", color = AppText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text(" 分  →  ", color = AppMuted)
                        Text("${SamplePracticeRecords.averageScore}", color = AppText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text(" 分", color = AppMuted)
                        Box(modifier = Modifier.weight(1f))
                        Text("提升 7 分 ›", color = AppGreen, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    MetricBar("综合趋势", SamplePracticeRecords.averageScore, AppPurple)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(records) { record ->
                    RecordRow(record)
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
private fun WeekStrip() {
    // 周历条用于快速定位本周练习记录。
    val days = listOf("一" to "20", "二" to "21", "三" to "22", "四" to "23", "五" to "24", "六" to "25", "日" to "26")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { (week, day) ->
            val selected = week == "三"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(week, color = AppMuted, style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) AppPurpleSoft else Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(day, color = if (selected) AppPurpleDeep else AppText, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun RecordRow(record: PracticeRecord) {
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
            Text(record.practicedAt, color = AppMuted, style = MaterialTheme.typography.bodySmall)
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
                .background(AppPurpleSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text("▶", color = AppPurpleDeep)
        }
    }
}
