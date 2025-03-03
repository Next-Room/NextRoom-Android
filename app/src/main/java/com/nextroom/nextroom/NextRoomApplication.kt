package com.nextroom.nextroom

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class NextRoomApplication : Application() {
    @Inject
    lateinit var flipperNetworkPlugin: NetworkFlipperPlugin

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setDebugTool()
    }

    private fun setDebugTool() {
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(
                InspectorFlipperPlugin(
                    applicationContext,
                    DescriptorMapping.withDefaults(),
                ),
            )
            client.addPlugin(SharedPreferencesFlipperPlugin(this, "app-settings.json"))
            client.addPlugin(flipperNetworkPlugin)
            client.start()
        }
    }
}
