package com.nextroom.nextroom

import android.content.Context
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlavorExtraFunction @Inject constructor(
    private val context: Context,
) {
    fun initializeFlipper() {
        // do nothing
    }

    fun getFlipperInterceptor(): Interceptor? = null
}