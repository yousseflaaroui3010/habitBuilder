package com.habitarchitect.data.analytics

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.habitarchitect.domain.analytics.ConsentTier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.consentDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "consent_preferences"
)

/**
 * Manages user consent for data collection.
 * Defaults to ESSENTIAL tier (anonymous analytics only).
 */
@Singleton
class ConsentManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_CONSENT_TIER = stringPreferencesKey("consent_tier")
    }

    private var cachedConsentTier: ConsentTier? = null

    /**
     * Get the current consent tier.
     */
    suspend fun getConsentTier(): ConsentTier {
        cachedConsentTier?.let { return it }

        val stored = context.consentDataStore.data.first()[KEY_CONSENT_TIER]
        val tier = stored?.let {
            try {
                ConsentTier.valueOf(it)
            } catch (e: IllegalArgumentException) {
                ConsentTier.ESSENTIAL
            }
        } ?: ConsentTier.ESSENTIAL

        cachedConsentTier = tier
        return tier
    }

    /**
     * Get consent tier synchronously.
     */
    fun getConsentTierSync(): ConsentTier = runBlocking { getConsentTier() }

    /**
     * Update the consent tier.
     */
    suspend fun setConsentTier(tier: ConsentTier) {
        context.consentDataStore.edit { prefs ->
            prefs[KEY_CONSENT_TIER] = tier.name
        }
        cachedConsentTier = tier
    }

    /**
     * Observe consent tier changes.
     */
    fun observeConsentTier(): Flow<ConsentTier> = context.consentDataStore.data.map { prefs ->
        val stored = prefs[KEY_CONSENT_TIER]
        stored?.let {
            try {
                ConsentTier.valueOf(it)
            } catch (e: IllegalArgumentException) {
                ConsentTier.ESSENTIAL
            }
        } ?: ConsentTier.ESSENTIAL
    }

    /**
     * Check if tracking is allowed for a given consent tier.
     */
    suspend fun canTrack(requiredTier: ConsentTier): Boolean {
        val currentTier = getConsentTier()
        return currentTier.ordinal >= requiredTier.ordinal
    }

    /**
     * Check if tracking is allowed synchronously.
     */
    fun canTrackSync(requiredTier: ConsentTier): Boolean {
        val currentTier = cachedConsentTier ?: getConsentTierSync()
        return currentTier.ordinal >= requiredTier.ordinal
    }

    /**
     * Reset consent to default (ESSENTIAL).
     */
    suspend fun resetConsent() {
        setConsentTier(ConsentTier.ESSENTIAL)
    }
}
