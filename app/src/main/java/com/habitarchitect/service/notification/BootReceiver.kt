package com.habitarchitect.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Receiver that reschedules notification workers after device boot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotificationWorkers(context)
        }
    }

    private fun scheduleNotificationWorkers(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Morning reminder worker
        val morningWork = PeriodicWorkRequestBuilder<MorningReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "morning_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            morningWork
        )

        // Evening check-in worker
        val eveningWork = PeriodicWorkRequestBuilder<EveningCheckinWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "evening_checkin",
            ExistingPeriodicWorkPolicy.KEEP,
            eveningWork
        )
    }
}
