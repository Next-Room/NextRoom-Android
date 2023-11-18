package com.mangbaam.commonutil

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateTimeUtil(private val locale: Locale = Locale.getDefault()) {
    private val dateFormatString = "yyyy-MM-dd HH:mm:ss"
    private fun dateFormat(pattern: String = dateFormatString) = SimpleDateFormat(pattern, locale)

    fun dateToString(date: Date, pattern: String = dateFormatString) =
        dateFormat(pattern).safeFormat(date)

    fun stringToDate(string: String, pattern: String = dateFormatString): Date? {
        return dateFormat(pattern).safeParse(string)
    }

    fun dateToLong(date: Date) = date.time

    fun longToDate(milliseconds: Long, pattern: String = dateFormatString) =
        Calendar.getInstance(locale).apply {
            timeInMillis = milliseconds
            SimpleDateFormat(pattern, locale).format(time)
        }.time

    fun longToDateString(
        milliseconds: Long,
        pattern: String = dateFormatString,
    ) = dateToString(longToDate(milliseconds, pattern), pattern)

    fun calculateDday(targetDate: Date): Int {
        val currentDateCal = Calendar.getInstance(locale).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetDateCal = Calendar.getInstance(locale).apply {
            time = targetDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val timeDiff = targetDateCal.timeInMillis - currentDateCal.timeInMillis
        return (timeDiff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun currentTime(
        pattern: String = dateFormatString,
    ) = longToDateString(System.currentTimeMillis(), pattern)

    private fun SimpleDateFormat.safeParse(pattern: String): Date? {
        return try {
            parse(pattern)
        } catch (_: Exception) {
            null
        }
    }

    private fun SimpleDateFormat.safeFormat(pattern: Date): String? {
        return try {
            format(pattern)
        } catch (_: Exception) {
            null
        }
    }
}
