package com.nextroom.nextroom.data.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.BillingDataSource
import com.nextroom.nextroom.data.datasource.FirebaseRemoteConfigDataSource
import com.nextroom.nextroom.data.datasource.HintLocalDataSource
import com.nextroom.nextroom.data.datasource.HintRemoteDataSource
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.StatisticsDataSource
import com.nextroom.nextroom.data.datasource.SubscriptionDataSource
import com.nextroom.nextroom.data.datasource.ThemeLocalDataSource
import com.nextroom.nextroom.data.datasource.ThemeRemoteDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.data.datasource.UserDataSource
import com.nextroom.nextroom.data.db.GameStateDao
import com.nextroom.nextroom.data.db.HintDao
import com.nextroom.nextroom.data.db.ThemeDao
import com.nextroom.nextroom.data.db.ThemeTimeDao
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.repository.AdminRepositoryImpl
import com.nextroom.nextroom.data.repository.BannerRepositoryImpl
import com.nextroom.nextroom.data.repository.BillingRepositoryImpl
import com.nextroom.nextroom.data.repository.DataStoreRepositoryImpl
import com.nextroom.nextroom.data.repository.FirebaseRemoteConfigRepositoryImpl
import com.nextroom.nextroom.data.repository.GameStateRepositoryImpl
import com.nextroom.nextroom.data.repository.HintRepositoryImpl
import com.nextroom.nextroom.data.repository.StatisticsRepositoryImpl
import com.nextroom.nextroom.data.repository.ThemeRepositoryImpl
import com.nextroom.nextroom.data.repository.TimerRepositoryImpl
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.BannerRepository
import com.nextroom.nextroom.domain.repository.BillingRepository
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.FirebaseRemoteConfigRepository
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideTimerRepository(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    ): TimerRepository {
        return TimerRepositoryImpl(defaultDispatcher)
    }

    @Singleton
    @Provides
    fun provideHintRepository(
        hintLocalDataSource: HintLocalDataSource,
        hintRemoteDataSource: HintRemoteDataSource,
        themeLocalDataSource: ThemeLocalDataSource,
        settingDataSource: SettingDataSource,
    ): HintRepository {
        return HintRepositoryImpl(
            hintLocalDataSource,
            hintRemoteDataSource,
            themeLocalDataSource,
            settingDataSource,
        )
    }

    @Provides
    fun provideSettingDataSource(
        @ApplicationContext context: Context,
    ): SettingDataSource {
        return SettingDataSource(context)
    }

    @Provides
    fun provideSubscriptionDataSource(
        apiService: ApiService,
    ): SubscriptionDataSource {
        return SubscriptionDataSource(apiService)
    }

    @Provides
    fun provideThemeLocalDataSource(
        themeDao: ThemeDao,
        themeTimeDao: ThemeTimeDao,
        hintDao: HintDao,
    ): ThemeLocalDataSource {
        return ThemeLocalDataSource(
            themeDao,
            themeTimeDao,
            hintDao,
        )
    }

    @Provides
    fun provideThemeRemoteDataSource(
        apiSource: ApiService,
    ): ThemeRemoteDataSource {
        return ThemeRemoteDataSource(apiSource)
    }

    @Singleton
    @Provides
    fun provideAdminRepository(
        authDataSource: AuthDataSource,
        userDataSource: UserDataSource,
        settingDataSource: SettingDataSource,
        tokenDataSource: TokenDataSource,
        subscriptionDataSource: SubscriptionDataSource,
        getCredentialRequest: GetCredentialRequest,
        credentialManager: CredentialManager,
        @ApplicationContext context: Context,
        apiService: ApiService,
    ): AdminRepository = AdminRepositoryImpl(
        authDataSource = authDataSource,
        userDataSource = userDataSource,
        settingDataSource = settingDataSource,
        tokenDataSource = tokenDataSource,
        subscriptionDataSource = subscriptionDataSource,
        getCredentialRequest = getCredentialRequest,
        credentialManager = credentialManager,
        context = context,
        apiService = apiService,
    )

    @Singleton
    @Provides
    fun provideAuthDataSource(
        @ApplicationContext context: Context,
        @Named("defaultApiService") apiSource: ApiService,
    ): AuthDataSource = AuthDataSource(context, apiSource)

    @Provides
    fun provideUserDataSource(
        apiService: ApiService,
    ): UserDataSource = UserDataSource(apiService)

    @Singleton
    @Provides
    fun provideTokenDataSource(
        @ApplicationContext context: Context,
    ): TokenDataSource = TokenDataSource(context)

    @Singleton
    @Provides
    fun provideThemeRepository(
        settingDataSource: SettingDataSource,
        themeLocalDataSource: ThemeLocalDataSource,
        themeRemoteDataSource: ThemeRemoteDataSource,
    ): ThemeRepository = ThemeRepositoryImpl(
        settingDataSource,
        themeLocalDataSource,
        themeRemoteDataSource,
    )

    @Singleton
    @Provides
    fun provideGameRepository(
        settingDataSource: SettingDataSource,
        timerRepository: TimerRepository,
        gameStateDao: GameStateDao,
    ): GameStateRepository {
        return GameStateRepositoryImpl(
            settingDataSource,
            timerRepository,
            gameStateDao,
        )
    }

//    @Singleton
//    @Provides
//    fun provideBillingDataSource(): BillingDataSource = BillingDataSource()

    @Singleton
    @Provides
    fun provideBillingRepository(
        billingDataSource: BillingDataSource,
        subscriptionDataSource: SubscriptionDataSource,
    ): BillingRepository {
        return BillingRepositoryImpl(billingDataSource, subscriptionDataSource)
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        settingDataSource: SettingDataSource,
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(
            settingDataSource,
        )
    }

    @Provides
    fun provideStatisticsRepository(
        dataSource: StatisticsDataSource,
    ): StatisticsRepository {
        return StatisticsRepositoryImpl(dataSource)
    }

    @Singleton
    @Provides
    fun provideFirebaseRemoteConfigDataSource(
        firebaseRemoteConfig: FirebaseRemoteConfig,
    ): FirebaseRemoteConfigDataSource {
        return FirebaseRemoteConfigDataSource(firebaseRemoteConfig)
    }

    @Singleton
    @Provides
    fun provideFirebaseRemoteConfigRepository(
        dataSource: FirebaseRemoteConfigDataSource,
    ): FirebaseRemoteConfigRepository {
        return FirebaseRemoteConfigRepositoryImpl(dataSource)
    }

    @Singleton
    @Provides
    fun provideBannerRepository(
        apiService: ApiService
    ) : BannerRepository {
        return BannerRepositoryImpl(apiService)
    }
}
