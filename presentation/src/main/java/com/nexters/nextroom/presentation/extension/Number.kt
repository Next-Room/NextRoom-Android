package com.nexters.nextroom.presentation.extension

import android.content.res.Resources

private val density
    get() = Resources.getSystem().displayMetrics.density
private val scaledDensity
    get() = Resources.getSystem().displayMetrics.scaledDensity

/**
 * dp to px
 * */
val Number.dpToPx: Int
    get() = (this.toFloat() * density).toInt()
val Number.dp: Int
    get() = dpToPx

/**
 * sp to px
 * */
val Number.spToPx: Int
    get() = (this.toFloat() * scaledDensity).toInt()
val Number.sp: Int
    get() = spToPx

/*
* px to dp
* */
val Number.pxToDp: Int
    get() = (this.toFloat() / density).toInt()

val Number.px: Int
    get() = pxToDp

val Number.pxToSp: Int
    get() = (this.toFloat() / scaledDensity).toInt()

fun Double.isInteger(): Boolean = this == toLong().toDouble()

fun Float.isInteger(): Boolean = this == toInt().toFloat()

fun Int.toTimerFormat(): String {
    return String.format("%d:%02d:%02d", *toTimeUnit().toList().toTypedArray())
}

fun Int.toTimeUnit(): Triple<Int, Int, Int> {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return Triple(hours, minutes, seconds)
}
