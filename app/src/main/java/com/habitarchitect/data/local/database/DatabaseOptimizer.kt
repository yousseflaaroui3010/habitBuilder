package com.habitarchitect.data.local.database

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles database maintenance tasks like VACUUM and cleanup.
 * Runs periodically to optimize storage and performance.
 */
@Singleton
class DatabaseOptimizer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: HabitArchitectDatabase
) {
    companion object {
        private const val WORK_NAME = "database_optimization"
        private const val VACUUM_INTERVAL_DAYS = 7L
    }

    /**
     * Schedule periodic database optimization.
     */
    fun scheduleOptimization() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<DatabaseOptimizationWorker>(
            VACUUM_INTERVAL_DAYS, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Timber.d("Database optimization scheduled every $VACUUM_INTERVAL_DAYS days")
    }

    /**
     * Run VACUUM to optimize database size and performance.
     * Should only be called when app is idle.
     */
    suspend fun runVacuum() {
        try {
            database.openHelper.writableDatabase.execSQL("VACUUM")
            Timber.d("Database VACUUM completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to run VACUUM")
        }
    }

    /**
     * Analyze tables to update query optimizer statistics.
     */
    suspend fun runAnalyze() {
        try {
            database.openHelper.writableDatabase.execSQL("ANALYZE")
            Timber.d("Database ANALYZE completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to run ANALYZE")
        }
    }

    /**
     * Get current database size in bytes.
     */
    fun getDatabaseSize(): Long {
        return context.getDatabasePath(HabitArchitectDatabase.DATABASE_NAME).length()
    }
}

/**
 * WorkManager worker for periodic database optimization.
 */
class DatabaseOptimizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val optimizer: DatabaseOptimizer
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting database optimization")
            optimizer.runAnalyze()
            optimizer.runVacuum()
            Timber.d("Database optimization completed")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Database optimization failed")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
