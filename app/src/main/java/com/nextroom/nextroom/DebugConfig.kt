package com.nextroom.nextroom

import android.content.Context
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugConfig @Inject constructor(
    private val context: Context,
) {
    private fun initializeFlipper() {
        // do nothing
    }

    fun getNetworkFlipperPlugin(): NetworkFlipperPlugin? {
        return null
    }
}