package com.habitarchitect

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.habitarchitect.service.notification.AlarmScheduler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for Habit Architect.
 * Initializes Hilt DI, WorkManager, Timber logging, and Crashlytics.
 */
@HiltAndroidApp
class HabitArchitectApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging
        initializeTimber()
        // Initialize Crashlytics
        initializeCrashlytics()
        // Schedule daily notification alarms
        scheduleNotificationAlarms()

        Timber.d("HabitArchitectApp initialized")
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            // Debug builds: log to Logcat
            Timber.plant(Timber.DebugTree())
        } else {
            // Release builds: log to Crashlytics
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun initializeCrashlytics() {
        // Enable Crashlytics in release, disable in debug for cleaner logs
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
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

/**
 * Timber tree that logs to Firebase Crashlytics for release builds.
 * Only logs warnings, errors, and WTF messages to reduce noise.
 */
private class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < android.util.Log.WARN) return

        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log("${tag ?: "App"}: $message")

        if (t != null) {
            crashlytics.recordException(t)
        }
    }
}
