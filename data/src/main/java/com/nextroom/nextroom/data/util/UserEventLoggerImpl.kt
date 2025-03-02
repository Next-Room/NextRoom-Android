package com.nextroom.nextroom.data.util

import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.data.BuildConfig
import com.nextroom.nextroom.domain.util.UserEventLogger

class UserEventLoggerImpl(
    private val firebaseAnalytics: FirebaseAnalytics,
) : UserEventLogger {
    override fun logScreenEvent(screenName: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG_NR, screenName)
        } else {
            firebaseAnalytics.logEvent("screen_view", bundleOf("screen_name" to screenName))
        }
    }

    override fun logClickEvent(buttonName: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG_NR, buttonName)
        } else {
            firebaseAnalytics.logEvent("button_click", bundleOf("button_name" to buttonName))
        }
    }

    companion object {
        const val TAG_NR = "TAG_NR"
    }
}