package com.habitarchitect.service.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.dao.UserDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for exporting user data to JSON format.
 */
@Singleton
class DataExportService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao,
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
    private val listItemDao: ListItemDao
) {

    /**
     * Export all user data to a JSON file.
     * @param userId The user's ID
     * @return Uri of the exported file, or null if export failed
     */
    suspend fun exportUserData(userId: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val exportData = JSONObject()

            // Export metadata
            exportData.put("exportDate", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date()))
            exportData.put("appVersion", "1.0.0")
            exportData.put("userId", userId)

            // Export user info
            val user = userDao.getUserById(userId).first()
            user?.let {
                val userJson = JSONObject().apply {
                    put("email", it.email)
                    put("displayName", it.displayName ?: "")
                    put("createdAt", it.createdAt)
                    put("notificationsEnabled", it.notificationsEnabled)
                    put("morningReminderTime", it.morningReminderTime ?: "")
                    put("eveningReminderTime", it.eveningReminderTime ?: "")
                }
                exportData.put("user", userJson)
            }

            // Export habits
            val habits = habitDao.getActiveHabits(userId).first()
            val habitsArray = JSONArray()
            habits.forEach { habit ->
                val habitJson = JSONObject().apply {
                    put("id", habit.id)
                    put("name", habit.name)
                    put("type", habit.type)
                    put("category", habit.category ?: "")
                    put("iconEmoji", habit.iconEmoji)
                    put("triggerTime", habit.triggerTime ?: "")
                    put("triggerContext", habit.triggerContext ?: "")
                    put("frequency", habit.frequency)
                    put("minimumVersion", habit.minimumVersion ?: "")
                    put("stackAnchor", habit.stackAnchor ?: "")
                    put("reward", habit.reward ?: "")
                    put("currentStreak", habit.currentStreak)
                    put("longestStreak", habit.longestStreak)
                    put("totalSuccessDays", habit.totalSuccessDays)
                    put("totalFailureDays", habit.totalFailureDays)
                    put("createdAt", habit.createdAt)
                    put("isArchived", habit.isArchived)
                }

                // Export list items for this habit
                val listItems = listItemDao.getListItemsForHabit(habit.id).first()
                val listItemsArray = JSONArray()
                listItems.forEach { item ->
                    val itemJson = JSONObject().apply {
                        put("type", item.type)
                        put("content", item.content)
                        put("orderIndex", item.orderIndex)
                    }
                    listItemsArray.put(itemJson)
                }
                habitJson.put("listItems", listItemsArray)

                // Export daily logs for this habit (last 365 days)
                val logs = dailyLogDao.getLogsForHabit(habit.id).first()
                val logsArray = JSONArray()
                logs.forEach { log ->
                    val logJson = JSONObject().apply {
                        put("date", log.date)
                        put("status", log.status)
                        put("markedAt", log.markedAt)
                        put("note", log.note ?: "")
                    }
                    logsArray.put(logJson)
                }
                habitJson.put("dailyLogs", logsArray)

                habitsArray.put(habitJson)
            }
            exportData.put("habits", habitsArray)

            // Write to file
            val fileName = "habit_architect_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.json"
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            val exportFile = File(exportDir, fileName)
            exportFile.writeText(exportData.toString(2))

            // Return file URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                exportFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Create a share intent for the exported file.
     */
    fun createShareIntent(fileUri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, "Habit Architect Data Export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
