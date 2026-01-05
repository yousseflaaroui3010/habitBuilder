package com.habitarchitect.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
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

        // Migration from version 1 to 2: Add location and goal fields for intentions-based habit creation
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE habits ADD COLUMN location TEXT")
                database.execSQL("ALTER TABLE habits ADD COLUMN goal TEXT")
            }
        }

        // Migration from version 2 to 3: Add paper clip jar fields for gamification
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE habits ADD COLUMN paperClipCount INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE habits ADD COLUMN paperClipGoal INTEGER NOT NULL DEFAULT 30")
            }
        }

        /**
         * Get singleton database instance for non-Hilt contexts (like widgets).
         */
        fun getInstance(context: Context): HabitArchitectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitArchitectDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
