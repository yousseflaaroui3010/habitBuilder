package com.habitarchitect.di

import android.content.Context
import com.habitarchitect.data.local.database.HabitArchitectDatabase
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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitArchitectDatabase {
        // Use the singleton instance to ensure widgets and DI use the same database
        return HabitArchitectDatabase.getInstance(context)
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
}
