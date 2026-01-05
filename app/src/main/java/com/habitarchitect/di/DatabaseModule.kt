package com.habitarchitect.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.habitarchitect.data.local.database.HabitArchitectDatabase
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.dao.PartnershipDao
import com.habitarchitect.data.local.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        HabitArchitectDatabase.MIGRATION_1_2,
        HabitArchitectDatabase.MIGRATION_2_3
    )

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitArchitectDatabase {
        return Room.databaseBuilder(
            context,
            HabitArchitectDatabase::class.java,
            HabitArchitectDatabase.DATABASE_NAME
        )
            .addMigrations(*ALL_MIGRATIONS)
            .build()
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
}
