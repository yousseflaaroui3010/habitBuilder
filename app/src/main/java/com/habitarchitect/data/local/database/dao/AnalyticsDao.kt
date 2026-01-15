package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.AnalyticsEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for analytics event operations.
 */
@Dao
interface AnalyticsDao {

    /**
     * Insert a new analytics event.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AnalyticsEventEntity)

    /**
     * Insert multiple analytics events in a batch.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<AnalyticsEventEntity>)

    /**
     * Get unsynced events, oldest first, limited to batch size.
     * Only returns events that haven't exceeded max sync attempts.
     */
    @Query("""
        SELECT * FROM analytics_events
        WHERE synced = 0 AND syncAttempts < :maxAttempts
        ORDER BY timestamp ASC
        LIMIT :limit
    """)
    suspend fun getUnsyncedEvents(limit: Int = 100, maxAttempts: Int = 5): List<AnalyticsEventEntity>

    /**
     * Count of unsynced events.
     */
    @Query("SELECT COUNT(*) FROM analytics_events WHERE synced = 0")
    suspend fun getUnsyncedCount(): Int

    /**
     * Observe count of unsynced events.
     */
    @Query("SELECT COUNT(*) FROM analytics_events WHERE synced = 0")
    fun observeUnsyncedCount(): Flow<Int>

    /**
     * Mark events as synced by their IDs.
     */
    @Query("UPDATE analytics_events SET synced = 1 WHERE eventId IN (:eventIds)")
    suspend fun markAsSynced(eventIds: List<String>)

    /**
     * Increment sync attempt count and update last attempt time for failed syncs.
     */
    @Query("""
        UPDATE analytics_events
        SET syncAttempts = syncAttempts + 1, lastSyncAttempt = :timestamp
        WHERE eventId IN (:eventIds)
    """)
    suspend fun incrementSyncAttempts(eventIds: List<String>, timestamp: Long = System.currentTimeMillis())

    /**
     * Delete synced events older than the specified timestamp.
     * Used for cleanup after successful sync.
     */
    @Query("DELETE FROM analytics_events WHERE synced = 1 AND timestamp < :beforeTimestamp")
    suspend fun deleteSyncedEventsBefore(beforeTimestamp: Long)

    /**
     * Delete all synced events.
     */
    @Query("DELETE FROM analytics_events WHERE synced = 1")
    suspend fun deleteAllSyncedEvents()

    /**
     * Delete events that have exceeded max sync attempts.
     * These are considered permanently failed.
     */
    @Query("DELETE FROM analytics_events WHERE syncAttempts >= :maxAttempts")
    suspend fun deleteFailedEvents(maxAttempts: Int = 5)

    /**
     * Delete all analytics events (for account deletion or testing).
     */
    @Query("DELETE FROM analytics_events")
    suspend fun deleteAllEvents()

    /**
     * Get total count of stored events.
     */
    @Query("SELECT COUNT(*) FROM analytics_events")
    suspend fun getTotalCount(): Int

    /**
     * Get events by consent tier (for consent level changes).
     */
    @Query("SELECT * FROM analytics_events WHERE consentTier > :tierOrdinal AND synced = 0")
    suspend fun getEventsAboveConsentTier(tierOrdinal: Int): List<AnalyticsEventEntity>

    /**
     * Delete unsynced events above a consent tier (when user downgrades consent).
     */
    @Query("DELETE FROM analytics_events WHERE consentTier > :tierOrdinal AND synced = 0")
    suspend fun deleteEventsAboveConsentTier(tierOrdinal: Int)

    /**
     * Get event by ID (for testing/debugging).
     */
    @Query("SELECT * FROM analytics_events WHERE eventId = :eventId")
    suspend fun getEventById(eventId: String): AnalyticsEventEntity?

    /**
     * Get oldest unsynced event timestamp (for monitoring staleness).
     */
    @Query("SELECT MIN(timestamp) FROM analytics_events WHERE synced = 0")
    suspend fun getOldestUnsyncedTimestamp(): Long?

    /**
     * Get events in a time range (for debugging/export).
     */
    @Query("""
        SELECT * FROM analytics_events
        WHERE timestamp BETWEEN :startTime AND :endTime
        ORDER BY timestamp ASC
    """)
    suspend fun getEventsInRange(startTime: Long, endTime: Long): List<AnalyticsEventEntity>
}
