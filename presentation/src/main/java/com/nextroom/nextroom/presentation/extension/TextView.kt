package com.nextroom.nextroom.presentation.extension

import android.graphics.Paint
import android.widget.TextView

fun TextView.underline(apply: Boolean = true) {
    if (apply) paintFlags = Paint.UNDERLINE_TEXT_FLAG else Paint.UNDERLINE_TEXT_FLAG.inv()
}
