package com.habitarchitect.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitarchitect.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker that sends evening check-in notifications.
 */
@HiltWorker
class EveningCheckinWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        createNotificationChannel()
        showNotification()
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Evening Check-ins",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily evening habit check-ins"
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        // Create "All Good" action intent
        val allGoodIntent = Intent(applicationContext, AllGoodReceiver::class.java).apply {
            action = AllGoodReceiver.ACTION_ALL_GOOD
        }
        val allGoodPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            allGoodIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Evening Check-in")
            .setContentText("How did your habits go today?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "All Good!",
                allGoodPendingIntent
            )
            .build()

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "evening_checkin_channel"
        private const val NOTIFICATION_ID = 1002
    }
}
