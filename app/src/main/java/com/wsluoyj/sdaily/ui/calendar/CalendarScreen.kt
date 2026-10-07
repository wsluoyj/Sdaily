package com.wsluoyj.sdaily.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wsluoyj.sdaily.SdailyApp
import com.wsluoyj.sdaily.data.local.entity.EjacRecord
import com.wsluoyj.sdaily.data.local.entity.RecordMethod
import com.wsluoyj.sdaily.util.formatTime
import com.wsluoyj.sdaily.util.toLocalDate
import java.time.LocalDate
import java.time.YearMonth

private val weekdayHeaders = listOf("一", "二", "三", "四", "五", "六", "日")

@Composable
fun CalendarScreen(modifier: Modifier = Modifier, onEditRecord: (EjacRecord) -> Unit = {}) {
    val context = LocalContext.current
    val app = context.applicationContext as SdailyApp
    val records by app.recordRepository.allRecords.collectAsState(initial = emptyList())
    val partners by app.partnerRepository.activePartners.collectAsState(initial = emptyList())

    val today = LocalDate.now()
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var selected by remember { mutableStateOf(today) }

    // 当月每天的次数
    val dayCounts = remember(records, month) {
        records.asSequence()
            .map { it.timeMs.toLocalDate() }
            .filter { YearMonth.from(it) == month }
            .groupingBy { it.dayOfMonth }
            .eachCount()
    }
    val monthTotal = dayCounts.values.sum()
    val selectedRecords = remember(records, selected) {
        records.filter { it.timeMs.toLocalDate() == selected }
    }
    val partnerName = { id: Long? -> partners.firstOrNull { it.id == id }?.name }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        // 月份切换
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { month = month.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "上个月")
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${month.year}年${month.monthValue}月",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "本月 $monthTotal 次",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { month = month.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "下个月")
            }
        }
        if (month != YearMonth.from(today)) {
            TextButton(onClick = {
                month = YearMonth.from(today)
                selected = today
            }) { Text("回到今天") }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(12.dp)) {
                Row {
                    weekdayHeaders.forEach { w ->
                        Text(
                            w,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                val firstDay = month.atDay(1)
                val leadingBlanks = firstDay.dayOfWeek.value - 1 // 周一为一周开始
                val daysInMonth = month.lengthOfMonth()
                val cells = leadingBlanks + daysInMonth
                val rows = (0 until ((cells + 6) / 7))
                rows.forEach { rowIndex ->
                    Row(Modifier.fillMaxWidth()) {
                        (0 until 7).forEach { col ->
                            val cellIndex = rowIndex * 7 + col
                            val day = cellIndex - leadingBlanks + 1
                            if (day in 1..daysInMonth) {
                                val date = month.atDay(day)
                                DayCell(
                                    day = day,
                                    count = dayCounts[day] ?: 0,
                                    isToday = date == today,
                                    isSelected = date == selected,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selected = date }
                                )
                            } else {
                                Spacer(Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "${selected.monthValue}月${selected.dayOfMonth}日 · ${selectedRecords.size} 次",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        if (selectedRecords.isEmpty()) {
            Text(
                "当天没有记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(selectedRecords, key = { it.id }) { record ->
                    DayEventRow(record, partnerName(record.partnerId), onClick = { onEditRecord(record) })
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    count: Int,
    isToday: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isSelected)
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                else Modifier
            )
            .then(
                if (isToday)
                    Modifier.border(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(10.dp)
                    )
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier.fillMaxSize().padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
            if (count > 0) {
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    val dots = if (count >= 2) 2 else 1
                    repeat(dots) {
                        Box(
                            Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayEventRow(record: EjacRecord, partnerName: String?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(formatTime(record.timeMs), fontWeight = FontWeight.Medium)
            Spacer(Modifier.size(12.dp))
            Column {
                Text(
                    buildString {
                        append(record.method.label)
                        if (record.method == RecordMethod.MASTURBATION && record.usedToy == true) {
                            append(" · 器具")
                        }
                        partnerName?.let { append(" · ").append(it) }
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                val sub = buildList {
                    record.durationMin?.let { add("${it}分钟") }
                    record.rating?.let { add("★".repeat(it)) }
                    record.internalEjac?.let { if (it) add("内射") }
                    record.protectionUsed?.let { if (it) add("已防护") }
                    record.note?.takeIf { it.isNotBlank() }?.let { add(it) }
                }.joinToString(" · ")
                if (sub.isNotEmpty()) {
                    Text(
                        sub,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
