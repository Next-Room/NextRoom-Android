package com.nextroom.nextroom

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class NextRoomApplication : Application() {
    @Inject
    lateinit var flavorExtraFunction: FlavorExtraFunction

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            flavorExtraFunction.initializeFlipper()
        }
    }
}
