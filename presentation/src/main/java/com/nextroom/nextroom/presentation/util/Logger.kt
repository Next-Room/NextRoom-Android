package com.nextroom.nextroom.presentation.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextroom.nextroom.presentation.BuildConfig

object Logger {
    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

    fun e(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("juhwan", "$message")
        } else {
            firebaseCrashlytics.log(message)
        }
    }

    fun e(exception: Exception) {
        if (BuildConfig.DEBUG) {
            Log.d("juhwan", "$exception")
        } else {
            firebaseCrashlytics.recordException(exception)
        }
    }
}