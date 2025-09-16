package com.example.subcriptionmanagementapp.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.formatDate(): String {
    return DateUtils.formatDate(this)
}

fun Long.formatTime(): String {
    return DateUtils.formatTime(this)
}

fun Long.formatDateTime(): String {
    return DateUtils.formatDateTime(this)
}

fun Long.getDaysUntil(): Long {
    return DateUtils.getDaysUntil(this)
}

fun getFirstDayOfMonth(month: Int, year: Int): Long {
    return DateUtils.getFirstDayOfMonth(month, year)
}

fun getLastDayOfMonth(month: Int, year: Int): Long {
    return DateUtils.getLastDayOfMonth(month, year)
}

fun getFirstDayOfWeek(): Long {
    return DateUtils.getFirstDayOfWeek()
}

fun getLastDayOfWeek(): Long {
    return DateUtils.getLastDayOfWeek()
}

fun getFirstDayOfYear(year: Int): Long {
    return DateUtils.getFirstDayOfYear(year)
}

fun getLastDayOfYear(year: Int): Long {
    return DateUtils.getLastDayOfYear(year)
}

fun getCurrentMonth(): Int {
    return Calendar.getInstance().get(Calendar.MONTH)
}

fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}

fun getCurrentDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun getCurrentDateTime(): Long {
    return System.currentTimeMillis()
}

fun String.toDate(format: String = "MM/dd/yyyy"): Long? {
    return try {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.parse(this)?.time
    } catch (e: Exception) {
        null
    }
}