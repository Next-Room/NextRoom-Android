package com.nextroom.nextroom.presentation.extension

import android.os.Bundle

fun Bundle.hasResultData() = this.containsKey(BUNDLE_KEY_RESULT_DATA)

fun Bundle.getResultData() = this.getString(BUNDLE_KEY_RESULT_DATA)

const val BUNDLE_KEY_RESULT_DATA = "BUNDLE_KEY_RESULT_DATA"