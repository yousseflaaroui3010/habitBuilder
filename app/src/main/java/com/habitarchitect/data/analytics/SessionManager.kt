package com.habitarchitect.data.analytics

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session_preferences"
)

/**
 * Manages user sessions for analytics tracking.
 * A session starts when the app opens and ends after 30 minutes of inactivity.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_SESSION_ID = stringPreferencesKey("session_id")
        private val KEY_SESSION_START = longPreferencesKey("session_start")
        private val KEY_LAST_ACTIVITY = longPreferencesKey("last_activity")

        const val SESSION_TIMEOUT_MS = 30 * 60 * 1000L // 30 minutes
    }

    private var cachedSessionId: String? = null
    private var cachedSessionStart: Long? = null
    private var lastActivityTime: Long = System.currentTimeMillis()

    /**
     * Get or create the current session ID.
     * Creates a new session if:
     * - No existing session
     * - Session has timed out (30 min inactivity)
     */
    suspend fun getSessionId(): String {
        val now = System.currentTimeMillis()

        // Check if session is still valid
        if (cachedSessionId != null && !isSessionExpired(now)) {
            return cachedSessionId!!
        }

        // Try to restore from DataStore
        val stored = context.sessionDataStore.data.first()
        val storedSessionId = stored[KEY_SESSION_ID]
        val storedLastActivity = stored[KEY_LAST_ACTIVITY] ?: 0L

        // Check if stored session is still valid
        if (storedSessionId != null && (now - storedLastActivity) < SESSION_TIMEOUT_MS) {
            cachedSessionId = storedSessionId
            cachedSessionStart = stored[KEY_SESSION_START] ?: now
            lastActivityTime = storedLastActivity
            return storedSessionId
        }

        // Create new session
        return startNewSession()
    }

    /**
     * Get session ID synchronously (for contexts where suspend isn't available).
     * Uses runBlocking - should be used sparingly.
     */
    fun getSessionIdSync(): String = runBlocking { getSessionId() }

    /**
     * Start a new session explicitly.
     */
    suspend fun startNewSession(): String {
        val now = System.currentTimeMillis()
        val newSessionId = UUID.randomUUID().toString()

        context.sessionDataStore.edit { prefs ->
            prefs[KEY_SESSION_ID] = newSessionId
            prefs[KEY_SESSION_START] = now
            prefs[KEY_LAST_ACTIVITY] = now
        }

        cachedSessionId = newSessionId
        cachedSessionStart = now
        lastActivityTime = now

        return newSessionId
    }

    /**
     * Record user activity to extend session.
     * Should be called on meaningful user interactions.
     */
    suspend fun recordActivity() {
        val now = System.currentTimeMillis()

        // Check if session expired
        if (isSessionExpired(now)) {
            startNewSession()
            return
        }

        lastActivityTime = now
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_LAST_ACTIVITY] = now
        }
    }

    /**
     * Get session duration in milliseconds.
     */
    suspend fun getSessionDuration(): Long {
        val sessionStart = cachedSessionStart ?: context.sessionDataStore.data.first()[KEY_SESSION_START] ?: System.currentTimeMillis()
        return System.currentTimeMillis() - sessionStart
    }

    /**
     * Get session duration synchronously.
     */
    fun getSessionDurationSync(): Long = runBlocking { getSessionDuration() }

    /**
     * Get the session start timestamp.
     */
    suspend fun getSessionStart(): Long {
        return cachedSessionStart ?: context.sessionDataStore.data.first()[KEY_SESSION_START] ?: System.currentTimeMillis()
    }

    /**
     * Check if the current session has timed out.
     */
    fun isSessionExpired(currentTime: Long = System.currentTimeMillis()): Boolean {
        return (currentTime - lastActivityTime) >= SESSION_TIMEOUT_MS
    }

    /**
     * End the current session (e.g., on logout).
     */
    suspend fun endSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.remove(KEY_SESSION_ID)
            prefs.remove(KEY_SESSION_START)
            prefs.remove(KEY_LAST_ACTIVITY)
        }
        cachedSessionId = null
        cachedSessionStart = null
    }

    /**
     * Observe session changes.
     */
    fun observeSessionId() = context.sessionDataStore.data.map { it[KEY_SESSION_ID] }
}
