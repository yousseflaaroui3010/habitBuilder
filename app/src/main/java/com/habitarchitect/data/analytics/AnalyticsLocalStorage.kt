package com.habitarchitect.data.analytics

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.habitarchitect.data.local.database.dao.AnalyticsDao
import com.habitarchitect.data.local.database.entity.AnalyticsEventEntity
import com.habitarchitect.domain.analytics.AnalyticsEvent
import com.habitarchitect.domain.analytics.ConsentTier
import com.habitarchitect.domain.analytics.EventContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles local storage of analytics events using Room database.
 * Converts between domain models and database entities.
 */
@Singleton
class AnalyticsLocalStorage @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val gson: Gson
) {
    companion object {
        const val DEFAULT_BATCH_SIZE = 100
        const val MAX_SYNC_ATTEMPTS = 5
        private const val SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000L
    }

    /**
     * Store an analytics event locally.
     */
    suspend fun store(event: AnalyticsEvent) {
        analyticsDao.insertEvent(event.toEntity())
    }

    /**
     * Store multiple analytics events in a batch.
     */
    suspend fun storeBatch(events: List<AnalyticsEvent>) {
        analyticsDao.insertEvents(events.map { it.toEntity() })
    }

    /**
     * Get unsynced events ready for upload.
     */
    suspend fun getUnsyncedEvents(limit: Int = DEFAULT_BATCH_SIZE): List<AnalyticsEvent> {
        return analyticsDao.getUnsyncedEvents(limit, MAX_SYNC_ATTEMPTS)
            .map { it.toDomain() }
    }

    /**
     * Get count of unsynced events.
     */
    suspend fun getPendingCount(): Int = analyticsDao.getUnsyncedCount()

    /**
     * Observe count of pending events.
     */
    fun observePendingCount(): Flow<Int> = analyticsDao.observeUnsyncedCount()

    /**
     * Mark events as successfully synced.
     */
    suspend fun markAsSynced(eventIds: List<String>) {
        analyticsDao.markAsSynced(eventIds)
    }

    /**
     * Record a failed sync attempt.
     */
    suspend fun recordSyncFailure(eventIds: List<String>) {
        analyticsDao.incrementSyncAttempts(eventIds)
    }

    /**
     * Clean up old synced events (keep for 7 days for debugging).
     */
    suspend fun cleanupSyncedEvents() {
        val cutoff = System.currentTimeMillis() - SEVEN_DAYS_MS
        analyticsDao.deleteSyncedEventsBefore(cutoff)
    }

    /**
     * Clean up events that have permanently failed syncing.
     */
    suspend fun cleanupFailedEvents() {
        analyticsDao.deleteFailedEvents(MAX_SYNC_ATTEMPTS)
    }

    /**
     * Delete all analytics data (for account deletion).
     */
    suspend fun deleteAllData() {
        analyticsDao.deleteAllEvents()
    }

    /**
     * Handle consent downgrade - remove unsynced events above new consent level.
     */
    suspend fun handleConsentDowngrade(newTier: ConsentTier) {
        analyticsDao.deleteEventsAboveConsentTier(newTier.ordinal)
    }

    /**
     * Get oldest unsynced event timestamp for staleness monitoring.
     */
    suspend fun getOldestPendingTimestamp(): Long? {
        return analyticsDao.getOldestUnsyncedTimestamp()
    }

    /**
     * Get total stored event count.
     */
    suspend fun getTotalCount(): Int = analyticsDao.getTotalCount()

    // Extension functions for conversion
    private fun AnalyticsEvent.toEntity(): AnalyticsEventEntity {
        return AnalyticsEventEntity(
            eventId = eventId,
            eventName = eventName,
            timestamp = timestamp,
            anonymousUserId = anonymousUserId,
            propertiesJson = gson.toJson(properties),
            contextJson = gson.toJson(context),
            consentTier = requiredConsentTier.ordinal,
            synced = false,
            syncAttempts = 0,
            lastSyncAttempt = null
        )
    }

    private fun AnalyticsEventEntity.toDomain(): AnalyticsEvent {
        val propertiesType = object : TypeToken<Map<String, Any>>() {}.type
        return AnalyticsEvent(
            eventId = eventId,
            eventName = eventName,
            timestamp = timestamp,
            anonymousUserId = anonymousUserId,
            properties = gson.fromJson(propertiesJson, propertiesType) ?: emptyMap(),
            context = gson.fromJson(contextJson, EventContext::class.java),
            requiredConsentTier = ConsentTier.entries[consentTier]
        )
    }
}
