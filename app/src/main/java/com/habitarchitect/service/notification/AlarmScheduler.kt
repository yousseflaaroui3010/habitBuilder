package com.habitarchitect.service.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules exact-time notifications using AlarmManager.
 * WorkManager is not suitable for time-critical notifications as it batches work.
 */
@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_MORNING_REMINDER = "com.habitarchitect.MORNING_REMINDER"
        const val ACTION_EVENING_CHECKIN = "com.habitarchitect.EVENING_CHECKIN"
        const val ACTION_POST_FAILURE = "com.habitarchitect.POST_FAILURE"

        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_NAME = "habit_name"

        private const val REQUEST_MORNING = 1001
        private const val REQUEST_EVENING = 1002
        private const val REQUEST_POST_FAILURE_BASE = 2000
    }

    /**
     * Schedule daily morning reminder at 8:00 AM.
     */
    fun scheduleMorningReminder() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            action = ACTION_MORNING_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_MORNING,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactAlarm(calendar.timeInMillis, pendingIntent)
    }

    /**
     * Schedule daily evening check-in at 8:00 PM.
     */
    fun scheduleEveningCheckin() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            action = ACTION_EVENING_CHECKIN
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_EVENING,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactAlarm(calendar.timeInMillis, pendingIntent)
    }

    /**
     * Schedule a notification 1 hour after a habit failure.
     */
    fun schedulePostFailureReminder(habitId: String, habitName: String) {
        val triggerTime = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour from now

        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            action = ACTION_POST_FAILURE
            putExtra(EXTRA_HABIT_ID, habitId)
            putExtra(EXTRA_HABIT_NAME, habitName)
        }

        // Use habit ID hash for unique request code
        val requestCode = REQUEST_POST_FAILURE_BASE + habitId.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactAlarm(triggerTime, pendingIntent)
    }

    /**
     * Cancel all scheduled alarms.
     */
    fun cancelAllAlarms() {
        cancelAlarm(REQUEST_MORNING, ACTION_MORNING_REMINDER)
        cancelAlarm(REQUEST_EVENING, ACTION_EVENING_CHECKIN)
    }

    private fun cancelAlarm(requestCode: Int, action: String) {
        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleExactAlarm(triggerTime: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+: Check if we can schedule exact alarms
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                // Fallback to inexact alarm if permission not granted
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            // Pre-Android 12: Always use exact alarms
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
}
