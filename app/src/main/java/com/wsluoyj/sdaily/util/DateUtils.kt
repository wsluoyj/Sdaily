package com.wsluoyj.sdaily.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val zone: ZoneId = ZoneId.systemDefault()

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(zone).toLocalDate()

fun LocalDate.toEpochMsAtStart(): Long = this.atStartOfDay(zone).toInstant().toEpochMilli()

private val cnWeek = listOf("一", "二", "三", "四", "五", "六", "日")

fun LocalDate.weekdayCn(): String = "周${cnWeek[this.dayOfWeek.value - 1]}"

private val mdFmt = DateTimeFormatter.ofPattern("M月d日", Locale.CHINA)

/** "10月6日 周二"；非今年补年份 */
fun formatDayLabel(date: LocalDate, today: LocalDate = LocalDate.now()): String {
    val prefix = if (date.year != today.year) "${date.year}年" else ""
    return "$prefix${mdFmt.format(date)} ${date.weekdayCn()}"
}

fun formatTime(ms: Long): String =
    DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA)
        .format(Instant.ofEpochMilli(ms).atZone(zone))

/** 相对今天的中文描述 */
fun relativeDayText(date: LocalDate, today: LocalDate = LocalDate.now()): String {
    val days = ChronoUnit.DAYS.between(date, today)
    return when {
        days == 0L -> "今天"
        days == 1L -> "昨天"
        days == 2L -> "前天"
        days in 3..6 -> "${days}天前"
        else -> formatDayLabel(date, today)
    }
}

/** 统计周期类型 */
enum class StatsPeriod(val label: String) {
    DAY("日"), MONTH("月"), QUARTER("季"), YEAR("年")
}

/** 返回某周期的起止日期（含首尾） */
fun periodRange(type: StatsPeriod, cursor: LocalDate): Pair<LocalDate, LocalDate> = when (type) {
    StatsPeriod.DAY -> cursor to cursor
    StatsPeriod.MONTH -> cursor.withDayOfMonth(1) to
        cursor.withDayOfMonth(1).plusMonths(1).minusDays(1)
    StatsPeriod.QUARTER -> {
        val qStartMonth = (cursor.monthValue - 1) / 3 * 3 + 1
        val start = LocalDate.of(cursor.year, qStartMonth, 1)
        start to start.plusMonths(3).minusDays(1)
    }
    StatsPeriod.YEAR -> LocalDate.of(cursor.year, 1, 1) to LocalDate.of(cursor.year, 12, 31)
}

fun periodShift(type: StatsPeriod, cursor: LocalDate, delta: Long): LocalDate = when (type) {
    StatsPeriod.DAY -> cursor.plusDays(delta)
    StatsPeriod.MONTH -> cursor.plusMonths(delta)
    StatsPeriod.QUARTER -> cursor.plusMonths(delta * 3)
    StatsPeriod.YEAR -> cursor.plusYears(delta)
}

/** 周期标题，如 "2026年10月"、"2026年Q4"、"2026年10月6日" */
fun periodTitle(type: StatsPeriod, cursor: LocalDate): String = when (type) {
    StatsPeriod.DAY -> "${cursor.year}年${cursor.monthValue}月${cursor.dayOfMonth}日"
    StatsPeriod.MONTH -> "${cursor.year}年${cursor.monthValue}月"
    StatsPeriod.QUARTER -> "${cursor.year}年Q${(cursor.monthValue - 1) / 3 + 1}"
    StatsPeriod.YEAR -> "${cursor.year}年"
}
