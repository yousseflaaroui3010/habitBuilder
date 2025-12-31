package com.habitarchitect.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.habitarchitect.data.local.database.HabitArchitectDatabase
import com.habitarchitect.domain.model.HabitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Home screen widget for habit tracking with temptation support.
 * Shows the user's primary BREAK habit with streak count and "I'm Tempted" button.
 */
class HabitWidget : GlanceAppWidget() {

    companion object {
        val HABIT_ID_KEY = stringPreferencesKey("habit_id")
        val HABIT_NAME_KEY = stringPreferencesKey("habit_name")
        val HABIT_EMOJI_KEY = stringPreferencesKey("habit_emoji")
        val HABIT_STREAK_KEY = intPreferencesKey("habit_streak")

        /**
         * Update all widget instances with fresh data.
         */
        suspend fun updateAllWidgets(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(HabitWidget::class.java)

            glanceIds.forEach { glanceId ->
                updateWidgetData(context, glanceId)
            }
        }

        /**
         * Update a single widget instance with fresh data from database.
         */
        private suspend fun updateWidgetData(context: Context, glanceId: GlanceId) {
            withContext(Dispatchers.IO) {
                try {
                    val database = HabitArchitectDatabase.getInstance(context)
                    val habitDao = database.habitDao()

                    // Get the first active BREAK habit for the widget
                    val breakHabits = habitDao.getHabitsByTypeOnce(HabitType.BREAK.name)
                    val habit = breakHabits.firstOrNull()

                    updateAppWidgetState(context, glanceId) { prefs ->
                        if (habit != null) {
                            prefs[HABIT_ID_KEY] = habit.id
                            prefs[HABIT_NAME_KEY] = habit.name
                            prefs[HABIT_EMOJI_KEY] = habit.iconEmoji
                            prefs[HABIT_STREAK_KEY] = habit.currentStreak
                        } else {
                            prefs[HABIT_ID_KEY] = ""
                            prefs[HABIT_NAME_KEY] = "Add a BREAK habit"
                            prefs[HABIT_EMOJI_KEY] = ""
                            prefs[HABIT_STREAK_KEY] = 0
                        }
                    }

                    HabitWidget().update(context, glanceId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load initial data
        updateWidgetData(context, id)

        provideContent {
            GlanceTheme {
                HabitWidgetContent()
            }
        }
    }

    @Composable
    private fun HabitWidgetContent() {
        val prefs = currentState<Preferences>()
        val habitId = prefs[HABIT_ID_KEY] ?: ""
        val habitName = prefs[HABIT_NAME_KEY] ?: "No habit"
        val habitEmoji = prefs[HABIT_EMOJI_KEY] ?: ""
        val streak = prefs[HABIT_STREAK_KEY] ?: 0

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (habitEmoji.isNotBlank()) {
                Text(
                    text = habitEmoji,
                    style = TextStyle(fontSize = 24.sp)
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
            }

            Text(
                text = habitName,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = if (streak > 0) "$streak Day Streak" else "Start your streak!",
                style = TextStyle(
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            if (habitId.isNotBlank()) {
                val context = LocalContext.current
                val intent = Intent(context, TemptationActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("habitId", habitId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                Button(
                    text = "I'm Tempted",
                    onClick = actionStartActivity(intent)
                )
            }
        }
    }
}
