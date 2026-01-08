package com.habitarchitect.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receiver that reschedules notification alarms after device boot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotificationAlarms(context)
        }
    }

    private fun scheduleNotificationAlarms(context: Context) {
        val alarmScheduler = AlarmScheduler(context)
        alarmScheduler.scheduleMorningReminder()
        alarmScheduler.scheduleEveningCheckin()
    }
}
