package com.nextroom.nextroom.data.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.nextroom.nextroom.data.util.UserEventLoggerImpl
import com.nextroom.nextroom.domain.util.UserEventLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Singleton
    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }

        return FirebaseRemoteConfig
            .getInstance()
            .apply {
                setConfigSettingsAsync(configSettings)
            }
    }

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideUserEventLogger(firebaseAnalytics: FirebaseAnalytics): UserEventLogger {
        return UserEventLoggerImpl(firebaseAnalytics)
    }
}
