package com.nextroom.nextroom.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateTimeUtil(private val locale: Locale = Locale.getDefault()) {
    private val dateFormatString = "yyyy-MM-dd HH:mm:ss"
    private fun dateFormat(pattern: String = dateFormatString) = SimpleDateFormat(pattern, locale)

    fun dateToString(date: Date, pattern: String = dateFormatString) =
        dateFormat(pattern).format(date)

    fun stringToDate(string: String, pattern: String = dateFormatString): Date? {
        return dateFormat(pattern).parse(string)
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
        retPattern: String,
    ) = dateToString(longToDate(milliseconds, pattern), retPattern)
}
