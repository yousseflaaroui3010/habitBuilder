package com.habitarchitect.data.analytics

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.anonymousIdDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "anonymous_id_preferences"
)

/**
 * Generates and manages anonymous user IDs for analytics.
 * The anonymous ID is a hash of the user ID + installation salt.
 * This ensures:
 * - Same user always gets the same anonymous ID on the same device
 * - Different anonymous ID on different devices/reinstalls
 * - Cannot be reversed to get the actual user ID
 */
@Singleton
class AnonymousIdGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_INSTALLATION_SALT = stringPreferencesKey("installation_salt")
        private val KEY_CACHED_ANONYMOUS_ID = stringPreferencesKey("cached_anonymous_id")
        private val KEY_CACHED_USER_ID = stringPreferencesKey("cached_user_id")
    }

    private var cachedAnonymousId: String? = null
    private var cachedUserId: String? = null
    private var installationSalt: String? = null

    /**
     * Get or generate the installation-specific salt.
     * The salt is unique per installation and never changes.
     */
    private suspend fun getInstallationSalt(): String {
        installationSalt?.let { return it }

        val prefs = context.anonymousIdDataStore.data.first()
        val stored = prefs[KEY_INSTALLATION_SALT]

        if (stored != null) {
            installationSalt = stored
            return stored
        }

        // Generate new salt for this installation
        val newSalt = UUID.randomUUID().toString()
        context.anonymousIdDataStore.edit { it[KEY_INSTALLATION_SALT] = newSalt }
        installationSalt = newSalt
        return newSalt
    }

    /**
     * Generate an anonymous ID for a given user ID.
     * The anonymous ID is consistent for the same user on the same installation.
     */
    suspend fun generateAnonymousId(userId: String): String {
        // Return cached if same user
        if (cachedUserId == userId && cachedAnonymousId != null) {
            return cachedAnonymousId!!
        }

        // Check if we have a cached value in DataStore
        val prefs = context.anonymousIdDataStore.data.first()
        val storedUserId = prefs[KEY_CACHED_USER_ID]
        val storedAnonymousId = prefs[KEY_CACHED_ANONYMOUS_ID]

        if (storedUserId == userId && storedAnonymousId != null) {
            cachedUserId = userId
            cachedAnonymousId = storedAnonymousId
            return storedAnonymousId
        }

        // Generate new anonymous ID
        val salt = getInstallationSalt()
        val combined = "$userId:$salt"
        val anonymousId = sha256(combined).substring(0, 16)

        // Cache it
        context.anonymousIdDataStore.edit { mutablePrefs ->
            mutablePrefs[KEY_CACHED_USER_ID] = userId
            mutablePrefs[KEY_CACHED_ANONYMOUS_ID] = anonymousId
        }

        cachedUserId = userId
        cachedAnonymousId = anonymousId

        return anonymousId
    }

    /**
     * Generate anonymous ID synchronously.
     */
    fun generateAnonymousIdSync(userId: String): String = runBlocking {
        generateAnonymousId(userId)
    }

    /**
     * Clear cached anonymous ID (e.g., on logout).
     */
    suspend fun clearCache() {
        context.anonymousIdDataStore.edit { prefs ->
            prefs.remove(KEY_CACHED_ANONYMOUS_ID)
            prefs.remove(KEY_CACHED_USER_ID)
        }
        cachedAnonymousId = null
        cachedUserId = null
    }

    /**
     * Get the currently cached anonymous ID (if any).
     */
    fun getCachedAnonymousId(): String? = cachedAnonymousId

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
