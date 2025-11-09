package com.nextroom.nextroom.di

import android.content.Context
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.nextroom.nextroom.DebugConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDebugConfig(
        @ApplicationContext context: Context,
    ): DebugConfig {
        return DebugConfig(context)
    }

    @Singleton
    @Provides
    fun provideNetworkFlipperPlugin(
        debugConfig: DebugConfig,
    ): NetworkFlipperPlugin? {
        return debugConfig.getNetworkFlipperPlugin()
    }
}