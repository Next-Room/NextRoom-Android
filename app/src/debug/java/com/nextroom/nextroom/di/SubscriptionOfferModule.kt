package com.nextroom.nextroom.di

import com.nextroom.nextroom.presentation.ui.billing.SubscriptionOfferLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionOfferModule {
    @Singleton
    @Provides
    fun provideSubscriptionOfferLoader(): SubscriptionOfferLoader = FakeSubscriptionOfferLoader()
}
