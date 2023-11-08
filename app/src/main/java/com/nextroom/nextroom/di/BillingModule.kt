package com.nextroom.nextroom.di

import android.content.Context
import com.nextroom.nextroom.NextRoomApplication
import com.nextroom.nextroom.presentation.util.BillingClientLifecycle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {
    @Singleton
    @Provides
    fun provideBillingClientLifecycle(
        @ApplicationContext context: Context,
    ): BillingClientLifecycle {
        return BillingClientLifecycle.getInstance(context as NextRoomApplication)
    }
}
