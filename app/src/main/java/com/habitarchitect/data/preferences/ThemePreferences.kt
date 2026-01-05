package com.habitarchitect.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val todaysFocusKey = stringPreferencesKey("todays_focus")
    private val focusDateKey = stringPreferencesKey("focus_date")

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val modeString = preferences[themeModeKey] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(modeString)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeModeKey] = mode.name
        }
    }

    val todaysFocus: Flow<String> = context.dataStore.data.map { preferences ->
        val storedDate = preferences[focusDateKey] ?: ""
        val today = java.time.LocalDate.now().toString()
        if (storedDate == today) {
            preferences[todaysFocusKey] ?: ""
        } else {
            ""
        }
    }

    suspend fun setTodaysFocus(focus: String) {
        context.dataStore.edit { preferences ->
            preferences[todaysFocusKey] = focus
            preferences[focusDateKey] = java.time.LocalDate.now().toString()
        }
    }
}
