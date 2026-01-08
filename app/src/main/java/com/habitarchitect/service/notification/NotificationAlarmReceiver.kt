package com.habitarchitect.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.habitarchitect.MainActivity
import com.habitarchitect.R

/**
 * Receives alarm broadcasts and shows notifications.
 */
class NotificationAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID_REMINDERS = "habit_reminders"
        private const val CHANNEL_ID_CHECKINS = "habit_checkins"
        private const val CHANNEL_ID_RECOVERY = "habit_recovery"

        private const val NOTIFICATION_MORNING = 1
        private const val NOTIFICATION_EVENING = 2
        private const val NOTIFICATION_POST_FAILURE_BASE = 100
    }

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannels(context)

        when (intent.action) {
            AlarmScheduler.ACTION_MORNING_REMINDER -> showMorningReminder(context)
            AlarmScheduler.ACTION_EVENING_CHECKIN -> showEveningCheckin(context)
            AlarmScheduler.ACTION_POST_FAILURE -> {
                val habitId = intent.getStringExtra(AlarmScheduler.EXTRA_HABIT_ID) ?: return
                val habitName = intent.getStringExtra(AlarmScheduler.EXTRA_HABIT_NAME) ?: "your habit"
                showPostFailureReminder(context, habitId, habitName)
            }
        }

        // Reschedule daily alarms for the next day
        if (intent.action == AlarmScheduler.ACTION_MORNING_REMINDER ||
            intent.action == AlarmScheduler.ACTION_EVENING_CHECKIN) {
            rescheduleAlarm(context, intent.action!!)
        }
    }

    private fun showMorningReminder(context: Context) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Start your day strong!")
            .setContentText("Check in with your habits and set yourself up for success.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_MORNING, notification)
    }

    private fun showEveningCheckin(context: Context) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "All Good" action button
        val allGoodIntent = Intent(context, AllGoodReceiver::class.java)
        val allGoodPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            allGoodIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CHECKINS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("How did today go?")
            .setContentText("Take a moment to reflect on your habits.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(0, "All Good!", allGoodPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_EVENING, notification)
    }

    private fun showPostFailureReminder(context: Context, habitId: String, habitName: String) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_RECOVERY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Ready to try again?")
            .setContentText("One slip doesn't define you. What strategy can you use next time for \"$habitName\"?")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("One slip doesn't define you. What strategy can you use next time for \"$habitName\"? Consider making the habit more invisible or difficult."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = NOTIFICATION_POST_FAILURE_BASE + habitId.hashCode()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Morning and evening habit reminders"
            }

            val checkinsChannel = NotificationChannel(
                CHANNEL_ID_CHECKINS,
                "Check-ins",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily check-in prompts"
            }

            val recoveryChannel = NotificationChannel(
                CHANNEL_ID_RECOVERY,
                "Recovery Support",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Supportive messages after setbacks"
            }

            notificationManager.createNotificationChannels(
                listOf(remindersChannel, checkinsChannel, recoveryChannel)
            )
        }
    }

    private fun rescheduleAlarm(context: Context, action: String) {
        // We need to reschedule for the next day after the alarm fires
        // This is done by getting AlarmScheduler via a simple approach
        // Since we can't inject here, we create a new instance
        val alarmScheduler = AlarmScheduler(context)
        when (action) {
            AlarmScheduler.ACTION_MORNING_REMINDER -> alarmScheduler.scheduleMorningReminder()
            AlarmScheduler.ACTION_EVENING_CHECKIN -> alarmScheduler.scheduleEveningCheckin()
        }
    }
}
