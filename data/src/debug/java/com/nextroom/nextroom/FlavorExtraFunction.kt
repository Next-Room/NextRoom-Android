package com.nextroom.nextroom

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.nextroom.nextroom.data.BuildConfig
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlavorExtraFunction @Inject constructor(
    private val context: Context,
) {
    private lateinit var flipperNetworkPlugin: NetworkFlipperPlugin

    fun initializeFlipper() {
        SoLoader.init(context, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            flipperNetworkPlugin = NetworkFlipperPlugin()
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            client.addPlugin(
                InspectorFlipperPlugin(
                    context,
                    DescriptorMapping.withDefaults(),
                ),
            )
            client.addPlugin(SharedPreferencesFlipperPlugin(context, "app-settings.json"))
            client.addPlugin(flipperNetworkPlugin)
            client.start()
        }
    }

    fun getFlipperInterceptor(): Interceptor {
        return FlipperOkhttpInterceptor(flipperNetworkPlugin, true)
    }
}