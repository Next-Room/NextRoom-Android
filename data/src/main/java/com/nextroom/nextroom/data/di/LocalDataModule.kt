package com.nextroom.nextroom.data.di

import android.content.Context
import androidx.room.Room
import com.nextroom.nextroom.data.datasource.HintLocalDataSource
import com.nextroom.nextroom.data.datasource.StatisticsDataSource
import com.nextroom.nextroom.data.db.GameStateDao
import com.nextroom.nextroom.data.db.HintDao
import com.nextroom.nextroom.data.db.NextRoomDatabase
import com.nextroom.nextroom.data.db.NextRoomDatabase.Companion.DB_NAME
import com.nextroom.nextroom.data.db.StatisticsDao
import com.nextroom.nextroom.data.db.ThemeDao
import com.nextroom.nextroom.data.db.ThemeTimeDao
import com.nextroom.nextroom.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Singleton
    @Provides
    fun provideHintDatabase(@ApplicationContext context: Context): NextRoomDatabase {
        return Room.databaseBuilder(
            context,
            NextRoomDatabase::class.java,
            DB_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideHintDao(nextRoomDatabase: NextRoomDatabase): HintDao {
        return nextRoomDatabase.hintDao()
    }

    @Singleton
    @Provides
    fun provideThemeDao(nextRoomDatabase: NextRoomDatabase): ThemeDao {
        return nextRoomDatabase.themeDao()
    }

    @Singleton
    @Provides
    fun provideThemeTimeDao(nextRoomDatabase: NextRoomDatabase): ThemeTimeDao {
        return nextRoomDatabase.themeTimeDao()
    }

    @Singleton
    @Provides
    fun provideGameStateDao(nextRoomDatabase: NextRoomDatabase): GameStateDao {
        return nextRoomDatabase.gameStateDao()
    }

    @Singleton
    @Provides
    fun provideHintLocalDataSource(hintDao: HintDao): HintLocalDataSource {
        return HintLocalDataSource(hintDao)
    }

    @Singleton
    @Provides
    fun provideStatisticsDao(nextRoomDatabase: NextRoomDatabase): StatisticsDao {
        return nextRoomDatabase.statisticsDao()
    }

    @Provides
    fun provideStatisticsDataSource(
        statsDao: StatisticsDao,
        apiService: ApiService,
    ): StatisticsDataSource = StatisticsDataSource(statsDao, apiService)
}
