package com.nexters.nextroom.data.di

import android.content.Context
import androidx.room.Room
import com.nexters.nextroom.data.datasource.HintLocalDataSource
import com.nexters.nextroom.data.db.GameStateDao
import com.nexters.nextroom.data.db.HintDao
import com.nexters.nextroom.data.db.NextRoomDatabase
import com.nexters.nextroom.data.db.NextRoomDatabase.Companion.DB_NAME
import com.nexters.nextroom.data.db.ThemeDao
import com.nexters.nextroom.data.db.ThemeTimeDao
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
}
