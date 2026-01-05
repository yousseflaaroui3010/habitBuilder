package com.habitarchitect.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.habitarchitect.R
import com.habitarchitect.data.local.database.HabitArchitectDatabase
import com.habitarchitect.domain.model.HabitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Home screen widget for habit tracking.
 * Shows habits with streak count, navigation arrows to switch habits.
 */
class HabitWidget : GlanceAppWidget() {

    companion object {
        val HABIT_ID_KEY = stringPreferencesKey("habit_id")
        val HABIT_NAME_KEY = stringPreferencesKey("habit_name")
        val HABIT_EMOJI_KEY = stringPreferencesKey("habit_emoji")
        val HABIT_STREAK_KEY = intPreferencesKey("habit_streak")
        val HABIT_TYPE_KEY = stringPreferencesKey("habit_type")
        val CURRENT_INDEX_KEY = intPreferencesKey("current_index")
        val TOTAL_HABITS_KEY = intPreferencesKey("total_habits")

        val directionParam = ActionParameters.Key<String>("direction")

        /**
         * Update all widget instances with fresh data.
         */
        suspend fun updateAllWidgets(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(HabitWidget::class.java)

            glanceIds.forEach { glanceId ->
                updateWidgetData(context, glanceId, null)
            }
        }

        /**
         * Update a single widget instance with fresh data from database.
         */
        suspend fun updateWidgetData(context: Context, glanceId: GlanceId, newIndex: Int?) {
            withContext(Dispatchers.IO) {
                try {
                    val database = HabitArchitectDatabase.getInstance(context)
                    val habitDao = database.habitDao()

                    // Get ALL active habits (both BUILD and BREAK)
                    val allHabits = habitDao.getHabitsByTypeOnce(HabitType.BUILD.name) +
                            habitDao.getHabitsByTypeOnce(HabitType.BREAK.name)

                    updateAppWidgetState(context, glanceId) { prefs ->
                        if (allHabits.isNotEmpty()) {
                            val currentIndex = newIndex ?: (prefs[CURRENT_INDEX_KEY] ?: 0)
                            val safeIndex = currentIndex.coerceIn(0, allHabits.lastIndex)
                            val habit = allHabits[safeIndex]

                            prefs[HABIT_ID_KEY] = habit.id
                            prefs[HABIT_NAME_KEY] = habit.name
                            prefs[HABIT_EMOJI_KEY] = habit.iconEmoji
                            prefs[HABIT_STREAK_KEY] = habit.currentStreak
                            prefs[HABIT_TYPE_KEY] = habit.type
                            prefs[CURRENT_INDEX_KEY] = safeIndex
                            prefs[TOTAL_HABITS_KEY] = allHabits.size
                        } else {
                            prefs[HABIT_ID_KEY] = ""
                            prefs[HABIT_NAME_KEY] = "Add a habit"
                            prefs[HABIT_EMOJI_KEY] = ""
                            prefs[HABIT_STREAK_KEY] = 0
                            prefs[HABIT_TYPE_KEY] = ""
                            prefs[CURRENT_INDEX_KEY] = 0
                            prefs[TOTAL_HABITS_KEY] = 0
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
        updateWidgetData(context, id, null)

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
        val habitType = prefs[HABIT_TYPE_KEY] ?: ""
        val currentIndex = prefs[CURRENT_INDEX_KEY] ?: 0
        val totalHabits = prefs[TOTAL_HABITS_KEY] ?: 0

        val isBreakHabit = habitType == HabitType.BREAK.name

        // Dynamic colors based on theme
        val textColor = GlanceTheme.colors.onSurface
        val secondaryTextColor = GlanceTheme.colors.onSurfaceVariant

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Navigation row with arrows
            if (totalHabits > 1) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left arrow
                    Box(
                        modifier = GlanceModifier
                            .size(32.dp)
                            .clickable(actionRunCallback<NavigateHabitAction>(
                                actionParametersOf(directionParam to "prev")
                            )),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "◀",
                            style = TextStyle(
                                color = textColor,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Text(
                        text = "${currentIndex + 1}/$totalHabits",
                        style = TextStyle(
                            color = secondaryTextColor,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    // Right arrow
                    Box(
                        modifier = GlanceModifier
                            .size(32.dp)
                            .clickable(actionRunCallback<NavigateHabitAction>(
                                actionParametersOf(directionParam to "next")
                            )),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▶",
                            style = TextStyle(
                                color = textColor,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(4.dp))
            }

            // Emoji
            if (habitEmoji.isNotBlank()) {
                Text(
                    text = habitEmoji,
                    style = TextStyle(fontSize = 28.sp)
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
            }

            // Habit name
            Text(
                text = habitName,
                style = TextStyle(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Type badge
            if (habitType.isNotBlank()) {
                Text(
                    text = if (isBreakHabit) "BREAK" else "BUILD",
                    style = TextStyle(
                        color = if (isBreakHabit)
                            ColorProvider(Color(0xFFE57373))
                        else
                            ColorProvider(Color(0xFF81C784)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Streak
            Text(
                text = if (streak > 0) "🔥 $streak days" else "Start today!",
                style = TextStyle(
                    color = secondaryTextColor,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Action button
            if (habitId.isNotBlank()) {
                val context = LocalContext.current
                if (isBreakHabit) {
                    val intent = Intent(context, TemptationActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("habitId", habitId)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    Button(
                        text = "I'm Tempted",
                        onClick = actionStartActivity(intent)
                    )
                } else {
                    val intent = Intent(context, TemptationActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("habitId", habitId)
                        putExtra("isBuildHabit", true)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    Button(
                        text = "Need Motivation",
                        onClick = actionStartActivity(intent)
                    )
                }
            }
        }
    }
}

/**
 * Action callback for navigating between habits.
 */
class NavigateHabitAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val direction = parameters[HabitWidget.directionParam] ?: return

        // Get current state
        val database = HabitArchitectDatabase.getInstance(context)
        val habitDao = database.habitDao()
        val allHabits = habitDao.getHabitsByTypeOnce(HabitType.BUILD.name) +
                habitDao.getHabitsByTypeOnce(HabitType.BREAK.name)

        if (allHabits.isEmpty()) return

        // Calculate new index
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentIndex = prefs[HabitWidget.CURRENT_INDEX_KEY] ?: 0
            val newIndex = when (direction) {
                "next" -> (currentIndex + 1) % allHabits.size
                "prev" -> if (currentIndex - 1 < 0) allHabits.lastIndex else currentIndex - 1
                else -> currentIndex
            }
            prefs[HabitWidget.CURRENT_INDEX_KEY] = newIndex
        }

        // Update widget with new index
        HabitWidget.updateWidgetData(context, glanceId, null)
    }
}
