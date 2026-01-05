package com.habitarchitect.service.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.repository.DailyLogRepository
import com.habitarchitect.domain.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * BroadcastReceiver that handles the "All Good" notification action.
 * Marks all habits as successful for the current day.
 */
@AndroidEntryPoint
class AllGoodReceiver : BroadcastReceiver() {

    @Inject
    lateinit var habitRepository: HabitRepository

    @Inject
    lateinit var dailyLogRepository: DailyLogRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_ALL_GOOD) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = firebaseAuth.currentUser?.uid ?: return@launch
                val today = LocalDate.now()

                // Get all active habits
                val habits = habitRepository.getActiveHabits(userId).first()

                // Mark each habit as successful if not already logged today
                habits.forEach { habit ->
                    val existingLog = dailyLogRepository.getLogForDate(habit.id, today)
                    if (existingLog == null) {
                        dailyLogRepository.markStatus(
                            habitId = habit.id,
                            date = today,
                            status = DailyStatus.SUCCESS,
                            note = "Marked via All Good action"
                        )
                        habitRepository.incrementStreak(habit.id)
                    }
                }

                // Dismiss the notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(EVENING_NOTIFICATION_ID)

            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_ALL_GOOD = "com.habitarchitect.ACTION_ALL_GOOD"
        private const val EVENING_NOTIFICATION_ID = 1002
    }
}
