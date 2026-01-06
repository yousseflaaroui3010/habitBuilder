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
import com.habitarchitect.data.local.database.dao.WeeklyReflectionDao
import com.habitarchitect.data.local.database.entity.DailyLogEntity
import com.habitarchitect.data.local.database.entity.HabitEntity
import com.habitarchitect.data.local.database.entity.ListItemEntity
import com.habitarchitect.data.local.database.entity.PartnershipEntity
import com.habitarchitect.data.local.database.entity.UserEntity
import com.habitarchitect.data.local.database.entity.WeeklyReflectionEntity

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
        PartnershipEntity::class,
        WeeklyReflectionEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class HabitArchitectDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun listItemDao(): ListItemDao
    abstract fun partnershipDao(): PartnershipDao
    abstract fun weeklyReflectionDao(): WeeklyReflectionDao

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

        // Migration from version 3 to 4: Add weekly reflections table
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS weekly_reflections (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL,
                        weekStartDate TEXT NOT NULL,
                        wentWell TEXT NOT NULL,
                        didntGoWell TEXT NOT NULL,
                        learned TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_weekly_reflections_userId_weekStartDate ON weekly_reflections(userId, weekStartDate)")
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
