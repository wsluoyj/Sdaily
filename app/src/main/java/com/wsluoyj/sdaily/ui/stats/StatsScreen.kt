package com.wsluoyj.sdaily.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.SdailyApp
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.RecordMethod
import com.wsluoyj.sdaily.util.StatsPeriod
import com.wsluoyj.sdaily.util.periodRange
import com.wsluoyj.sdaily.util.periodShift
import com.wsluoyj.sdaily.util.periodTitle
import com.wsluoyj.sdaily.util.toEpochMsAtStart
import com.wsluoyj.sdaily.util.toLocalDate
import java.time.LocalDate

@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as SdailyApp
    val records by app.recordRepository.allRecords.collectAsState(initial = emptyList())

    val today = LocalDate.now()
    var type by remember { mutableStateOf(StatsPeriod.MONTH) }
    var cursor by remember { mutableStateOf(today) }

    val (startDate, endDate) = periodRange(type, cursor)
    val startMs = startDate.toEpochMsAtStart()
    val endMs = endDate.plusDays(1).toEpochMsAtStart() - 1
    val inPeriod = records.filter { it.timeMs in startMs..endMs }

    // 指标
    val total = inPeriod.size
    val avgGap = if (total >= 2) {
        val days = (inPeriod.maxOf { it.timeMs } - inPeriod.minOf { it.timeMs }) / 86_400_000.0
        String.format("%.1f", days / (total - 1))
    } else "—"
    val peakDay = inPeriod.groupingBy { it.timeMs.toLocalDate() }.eachCount().values.maxOrNull() ?: 0
    val durations = inPeriod.mapNotNull { it.durationMin }
    val avgDuration = if (durations.isEmpty()) "—" else String.format("%.0f", durations.average())
    val ratings = inPeriod.mapNotNull { it.rating }
    val avgRating = if (ratings.isEmpty()) "—" else String.format("%.1f", ratings.average())

    // 方式占比
    val methodCounts = RecordMethod.entries.associateWith { m -> inPeriod.count { it.method == m } }
    // 近 8 周（以周期末日为终点）
    val weeks = remember(endDate, records) {
        (7 downTo 0).map { w ->
            val wEnd = endDate.minusDays(w * 7L)
            val wStart = wEnd.minusDays(6)
            val cnt = records.count {
                val d = it.timeMs.toLocalDate()
                !d.isBefore(wStart) && !d.isAfter(wEnd)
            }
            wStart to cnt
        }
    }
    val weekMax = (weeks.maxOf { it.second }).coerceAtLeast(1)
    val isCurrentPeriod = !today.isBefore(startDate) && !today.isAfter(endDate)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatsPeriod.entries.forEach { p ->
                    FilterChip(
                        selected = type == p,
                        onClick = { type = p },
                        label = { Text(p.label) }
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { cursor = periodShift(type, cursor, -1) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "上一周期")
                    }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            periodTitle(type, cursor) + if (isCurrentPeriod) " · 本期" else "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = { cursor = periodShift(type, cursor, 1) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "下一周期")
                    }
                }
                if (!isCurrentPeriod) {
                    TextButton(onClick = { cursor = today }) { Text("回到本期") }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("总次数", "$total", Modifier.weight(1f))
                MetricCard("平均间隔", "$avgGap 天", Modifier.weight(1f))
                MetricCard("单日峰值", "$peakDay 次", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    "平均时长",
                    if (durations.isEmpty()) "—" else "$avgDuration 分",
                    Modifier.weight(1f),
                    sub = if (durations.isEmpty()) null else "${durations.size} 条"
                )
                MetricCard("平均评分", avgRating, Modifier.weight(1f))
            }
        }

        item {
            ChartCard("近 8 周频次") {
                Row(
                    Modifier.fillMaxWidth().height(120.dp).padding(top = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    weeks.forEach { (_, count) ->
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            if (count > 0) {
                                Text(
                                    "$count",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        if (count == 0) 3.dp
                                        else (20 + (count.toFloat() / weekMax) * 76).dp
                                    )
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .then(
                                        if (count > 0)
                                            Modifier.background(MaterialTheme.colorScheme.primary)
                                        else
                                            Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                    )
                            )
                        }
                    }
                }
            }
        }

        item {
            ChartCard("方式占比") {
                Spacer(Modifier.height(10.dp))
                val totalMethods = methodCounts.values.sum().coerceAtLeast(1)
                // 最大余数法分配整数百分比，保证各项之和恰为 100%
                val percents = remember(methodCounts) {
                    val total = methodCounts.values.sum()
                    if (total == 0) return@remember emptyMap<RecordMethod, Int>()
                    val exact = methodCounts.mapValues { it.value * 100.0 / total }
                    val base = exact.mapValues { it.value.toInt() }
                    val remainder = 100 - base.values.sum()
                    val order = exact.entries
                        .sortedByDescending { it.value - base.getValue(it.key) }
                        .map { it.key }
                    val result = base.toMutableMap()
                    order.take(remainder).forEach { result[it] = result.getValue(it) + 1 }
                    result
                }
                val colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.outline
                )
                Row(Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(7.dp))) {
                    methodCounts.values.forEachIndexed { i, c ->
                        if (c > 0) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(c.toFloat())
                                    .background(colors[i % colors.size])
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                methodCounts.entries.forEachIndexed { i, (m, c) ->
                    if (c > 0) {
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .height(10.dp)
                                        .padding(end = 0.dp)
                                )
                                Text(
                                    "■ ${m.label}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors[i % colors.size]
                                )
                            }
                            Text(
                                "$c 次 · ${percents[m]}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (methodCounts.values.sum() == 0) {
                    Text(
                        "本周期暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier, sub: String? = null) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (sub != null) {
                Text(
                    sub,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}


