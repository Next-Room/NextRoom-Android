package com.nextroom.nextroom.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}
