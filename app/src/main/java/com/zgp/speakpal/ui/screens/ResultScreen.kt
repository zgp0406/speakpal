package com.zgp.speakpal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    val feedback = when {
        score >= 90 -> "发音很棒，继续保持！"
        score >= 80 -> "整体不错，再关注一下细节音素。"
        else -> "再试一次，重点看口型和舌位。"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("评测结果", style = MaterialTheme.typography.headlineSmall)
        Text("单词：${word.text}")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("总分", style = MaterialTheme.typography.titleMedium)
                Text("$score", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                Text("错误音素：/æ/")
                Text("纠错建议：${word.hint}")
                Text("反馈：$feedback")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onBackToPractice, modifier = Modifier.fillMaxWidth()) {
            Text("再练一次")
        }
        Button(onClick = onViewRecords, modifier = Modifier.fillMaxWidth()) {
            Text("查看学习记录")
        }
    }
}
