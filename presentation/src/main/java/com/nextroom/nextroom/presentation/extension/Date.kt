package com.nextroom.nextroom.presentation.extension

import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.calculateDday(locale: Locale = Locale.KOREA): Int {
    val currentDate = Calendar.getInstance(locale).apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val targetDate = Calendar.getInstance(locale).apply {
        time = this@calculateDday
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val timeDiff = targetDate.timeInMillis - currentDate.timeInMillis
    return (timeDiff / (1000 * 60 * 60 * 24)).toInt()
}
