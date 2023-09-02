package com.nexters.nextroom.di

import android.content.Context
import com.nexters.nextroom.data.datasource.AuthDataSource
import com.nexters.nextroom.data.datasource.HintLocalDataSource
import com.nexters.nextroom.data.datasource.HintRemoteDataSource
import com.nexters.nextroom.data.datasource.SettingDataSource
import com.nexters.nextroom.data.datasource.ThemeLocalDataSource
import com.nexters.nextroom.data.datasource.ThemeRemoteDataSource
import com.nexters.nextroom.data.db.GameStateDao
import com.nexters.nextroom.data.db.HintDao
import com.nexters.nextroom.data.db.ThemeDao
import com.nexters.nextroom.data.db.ThemeTimeDao
import com.nexters.nextroom.data.network.ApiService
import com.nexters.nextroom.data.repository.AdminRepositoryImpl
import com.nexters.nextroom.data.repository.GameStateRepositoryImpl
import com.nexters.nextroom.data.repository.HintRepositoryImpl
import com.nexters.nextroom.data.repository.ThemeRepositoryImpl
import com.nexters.nextroom.data.repository.TimerRepositoryImpl
import com.nexters.nextroom.domain.repository.AdminRepository
import com.nexters.nextroom.domain.repository.GameStateRepository
import com.nexters.nextroom.domain.repository.HintRepository
import com.nexters.nextroom.domain.repository.ThemeRepository
import com.nexters.nextroom.domain.repository.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
        settingDataSource: SettingDataSource,
    ): AdminRepository = AdminRepositoryImpl(authDataSource, settingDataSource)

    @Singleton
    @Provides
    fun provideAuthDataSource(
        @ApplicationContext context: Context,
        apiSource: ApiService,
    ): AuthDataSource = AuthDataSource(context, apiSource)

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
}
