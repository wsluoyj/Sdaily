package com.wsluoyj.sdaily.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.SdailyApp
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.RecordMethod
import com.wsluoyj.sdaily.util.formatTime
import com.wsluoyj.sdaily.util.relativeDayText
import com.wsluoyj.sdaily.util.toLocalDate
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onRecordClick: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as SdailyApp
    val records by app.recordRepository.allRecords.collectAsState(initial = emptyList())
    val partners by app.partnerRepository.activePartners.collectAsState(initial = emptyList())

    val today = LocalDate.now()
    val monthRecords = remember(records, today) {
        records.filter {
            val d = it.timeMs.toLocalDate()
            d.year == today.year && d.monthValue == today.monthValue
        }
    }
    val todayRecords = records.filter { it.timeMs.toLocalDate() == today }
    val latest = records.maxByOrNull { it.timeMs }

    // 本月平均间隔（天）
    val avgGapText = remember(monthRecords) {
        if (monthRecords.size < 2) "—"
        else {
            val sorted = monthRecords.sortedBy { it.timeMs }
            val days = (sorted.last().timeMs - sorted.first().timeMs) / 86_400_000.0
            String.format("%.1f", days / (sorted.size - 1))
        }
    }

    // 按天分组（记录已按时间倒序）
    val grouped = remember(records) {
        records.groupBy { it.timeMs.toLocalDate() }.toList()
    }
    val partnerName = { id: Long? -> partners.firstOrNull { it.id == id }?.name }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "${today.monthValue}月概览",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${monthRecords.size}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            " 次 · 今日 ${todayRecords.size} 次",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "上次记录：" + (latest?.let { "${relativeDayText(it.timeMs.toLocalDate())} ${formatTime(it.timeMs)}" } ?: "暂无"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "平均间隔：$avgGapText 天",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Button(
                onClick = onRecordClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("  记录一次", style = MaterialTheme.typography.titleMedium)
            }
        }

        if (grouped.isEmpty()) {
            item {
                Text(
                    "还没有记录，点击上方按钮记录第一次吧",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }

        grouped.forEach { (date, dayRecords) ->
            item(key = "h-${date.toEpochDay()}") {
                Text(
                    relativeDayText(date) + " · ${dayRecords.size} 次",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            items(dayRecords, key = { it.id }) { record ->
                RecordRow(record, partnerName(record.partnerId))
            }
        }
    }
}

@Composable
private fun RecordRow(record: EjacRecord, partnerName: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                val title = buildString {
                    append(record.method.label)
                    if (record.method == RecordMethod.MASTURBATION && record.usedToy == true) {
                        append(" · 器具")
                    }
                    partnerName?.let { append(" · ").append(it) }
                }
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                val sub = buildList {
                    add(formatTime(record.timeMs))
                    record.durationMin?.let { add("${it}分钟") }
                    record.location?.takeIf { it.isNotBlank() }?.let { add(it) }
                    record.rating?.let { add("★".repeat(it)) }
                }.joinToString("  ·  ")
                Text(
                    sub,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
