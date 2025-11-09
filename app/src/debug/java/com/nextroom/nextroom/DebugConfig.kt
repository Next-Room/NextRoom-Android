import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.nextroom.nextroom.data.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugConfig @Inject constructor(
    private val context: Context,
) {
    private lateinit var networkFlipperPlugin: NetworkFlipperPlugin

    init {
        initializeFlipper()
    }

    private fun initializeFlipper() {
        SoLoader.init(context, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            client.addPlugin(
                InspectorFlipperPlugin(
                    context,
                    DescriptorMapping.withDefaults(),
                ),
            )
            client.addPlugin(SharedPreferencesFlipperPlugin(context, "app-settings.json"))
            client.addPlugin(getNetworkFlipperPlugin())
            client.start()
        }
    }

    fun getNetworkFlipperPlugin(): NetworkFlipperPlugin? {
        return if (::networkFlipperPlugin.isInitialized) {
            networkFlipperPlugin
        } else {
            networkFlipperPlugin = NetworkFlipperPlugin()
            networkFlipperPlugin
        }
    }
}