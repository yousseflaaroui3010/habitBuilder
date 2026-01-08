package com.habitarchitect

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.habitarchitect.service.notification.AlarmScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for Habit Architect.
 * Initializes Hilt dependency injection, WorkManager, and notification alarms.
 */
@HiltAndroidApp
class HabitArchitectApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate() {
        super.onCreate()
        // Schedule daily notification alarms
        scheduleNotificationAlarms()
    }

    private fun scheduleNotificationAlarms() {
        alarmScheduler.scheduleMorningReminder()
        alarmScheduler.scheduleEveningCheckin()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
