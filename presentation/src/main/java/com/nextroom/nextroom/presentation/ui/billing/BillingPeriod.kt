package com.nextroom.nextroom.presentation.ui.billing

import java.util.Calendar

/**
 * Play Billing이 내려주는 ISO-8601 기간 표기(P1W, P1M, P3M, P1Y ...)를 담는다.
 *
 * minSdk 24라 java.time.Period(API 26+)를 쓸 수 없어 직접 파싱한다.
 */
data class BillingPeriod(
    val years: Int = 0,
    val months: Int = 0,
    val weeks: Int = 0,
    val days: Int = 0,
) {

    /** [calendar]에 이 기간을 더한다. */
    fun addTo(calendar: Calendar) {
        if (years != 0) calendar.add(Calendar.YEAR, years)
        if (months != 0) calendar.add(Calendar.MONTH, months)
        if (weeks != 0) calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        if (days != 0) calendar.add(Calendar.DAY_OF_MONTH, days)
    }

    companion object {
        private val PATTERN = Regex("""^P(?:(\d+)Y)?(?:(\d+)M)?(?:(\d+)W)?(?:(\d+)D)?$""")

        /** 파싱할 수 없거나 기간이 0이면 null을 반환한다. */
        fun parse(iso8601: String?): BillingPeriod? {
            val match = iso8601?.let { PATTERN.matchEntire(it) } ?: return null
            val (years, months, weeks, days) = match.destructured

            return BillingPeriod(
                years = years.toIntOrNull() ?: 0,
                months = months.toIntOrNull() ?: 0,
                weeks = weeks.toIntOrNull() ?: 0,
                days = days.toIntOrNull() ?: 0,
            ).takeIf { it != BillingPeriod() }
        }
    }
}
