package com.nextroom.nextroom.di

import android.content.Context
import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.HintLocalDataSource
import com.nextroom.nextroom.data.datasource.HintRemoteDataSource
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.ThemeLocalDataSource
import com.nextroom.nextroom.data.datasource.ThemeRemoteDataSource
import com.nextroom.nextroom.data.db.GameStateDao
import com.nextroom.nextroom.data.db.HintDao
import com.nextroom.nextroom.data.db.ThemeDao
import com.nextroom.nextroom.data.db.ThemeTimeDao
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.repository.AdminRepositoryImpl
import com.nextroom.nextroom.data.repository.GameStateRepositoryImpl
import com.nextroom.nextroom.data.repository.HintRepositoryImpl
import com.nextroom.nextroom.data.repository.ThemeRepositoryImpl
import com.nextroom.nextroom.data.repository.TimerRepositoryImpl
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
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