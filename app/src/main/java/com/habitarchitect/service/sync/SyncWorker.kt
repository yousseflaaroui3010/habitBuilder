package com.habitarchitect.service.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.habitarchitect.data.sync.NetworkMonitor
import com.habitarchitect.data.sync.OfflineQueue
import com.habitarchitect.data.sync.SyncManager
import com.habitarchitect.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for syncing data with remote server.
 * Runs periodically and when network becomes available.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
    private val offlineQueue: OfflineQueue,
    private val networkMonitor: NetworkMonitor,
    private val userRepository: UserRepository
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "habit_sync_worker"
        private const val SYNC_INTERVAL_HOURS = 1L

        /**
         * Schedule periodic sync work.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                SYNC_INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )

            Timber.d("Scheduled periodic sync every $SYNC_INTERVAL_HOURS hours")
        }

        /**
         * Cancel scheduled sync.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Timber.d("Cancelled periodic sync")
        }
    }

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker started")

        // Check network
        if (!networkMonitor.isOnline.value) {
            Timber.d("No network, skipping sync")
            return Result.retry()
        }

        // Get current user
        val user = userRepository.getCurrentUser().firstOrNull()
        if (user == null) {
            Timber.d("No user logged in, skipping sync")
            return Result.success()
        }

        return try {
            // Run sync
            val result = syncManager.sync(user.id)

            if (result.isSuccess) {
                val syncedCount = result.getOrDefault(0)
                Timber.d("Sync completed successfully: $syncedCount items synced")

                // Cleanup old operations
                syncManager.cleanup()

                Result.success()
            } else {
                Timber.w("Sync failed: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Sync worker error")
            Result.retry()
        }
    }
}
