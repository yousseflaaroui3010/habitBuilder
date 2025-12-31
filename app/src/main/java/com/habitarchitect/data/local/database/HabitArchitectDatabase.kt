package com.habitarchitect.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.dao.PartnershipDao
import com.habitarchitect.data.local.database.dao.UserDao
import com.habitarchitect.data.local.database.entity.DailyLogEntity
import com.habitarchitect.data.local.database.entity.HabitEntity
import com.habitarchitect.data.local.database.entity.ListItemEntity
import com.habitarchitect.data.local.database.entity.PartnershipEntity
import com.habitarchitect.data.local.database.entity.UserEntity

/**
 * Room database for Habit Architect app.
 * Contains all local data storage for habits, users, logs, and partnerships.
 */
@Database(
    entities = [
        UserEntity::class,
        HabitEntity::class,
        DailyLogEntity::class,
        ListItemEntity::class,
        PartnershipEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HabitArchitectDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun listItemDao(): ListItemDao
    abstract fun partnershipDao(): PartnershipDao

    companion object {
        const val DATABASE_NAME = "habit_architect_db"

        @Volatile
        private var INSTANCE: HabitArchitectDatabase? = null

        /**
         * Get singleton database instance for non-Hilt contexts (like widgets).
         */
        fun getInstance(context: Context): HabitArchitectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitArchitectDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
