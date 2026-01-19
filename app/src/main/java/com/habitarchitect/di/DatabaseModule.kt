package com.habitarchitect.di

import android.content.Context
import com.habitarchitect.data.local.database.EncryptedDatabaseFactory
import com.habitarchitect.data.local.database.HabitArchitectDatabase
import com.habitarchitect.data.local.database.dao.AnalyticsDao
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.dao.PartnershipDao
import com.habitarchitect.data.local.database.dao.UserDao
import com.habitarchitect.data.local.database.dao.WeeklyReflectionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependency injection.
 * Provides encrypted Room database with SQLCipher.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitArchitectDatabase {
        // Use SQLCipher encrypted database for security
        return EncryptedDatabaseFactory.create(context)
    }

    @Provides
    fun provideUserDao(database: HabitArchitectDatabase): UserDao = database.userDao()

    @Provides
    fun provideHabitDao(database: HabitArchitectDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideDailyLogDao(database: HabitArchitectDatabase): DailyLogDao = database.dailyLogDao()

    @Provides
    fun provideListItemDao(database: HabitArchitectDatabase): ListItemDao = database.listItemDao()

    @Provides
    fun providePartnershipDao(database: HabitArchitectDatabase): PartnershipDao = database.partnershipDao()

    @Provides
    fun provideWeeklyReflectionDao(database: HabitArchitectDatabase): WeeklyReflectionDao = database.weeklyReflectionDao()

    @Provides
    fun provideAnalyticsDao(database: HabitArchitectDatabase): AnalyticsDao = database.analyticsDao()
}
