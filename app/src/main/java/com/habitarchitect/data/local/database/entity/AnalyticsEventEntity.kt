package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing analytics events locally before syncing to backend.
 */
@Entity(
    tableName = "analytics_events",
    indices = [
        Index(value = ["synced"]),
        Index(value = ["timestamp"]),
        Index(value = ["consentTier"])
    ]
)
data class AnalyticsEventEntity(
    @PrimaryKey
    val eventId: String,
    val eventName: String,
    val timestamp: Long,
    val anonymousUserId: String,
    val propertiesJson: String,
    val contextJson: String,
    val consentTier: Int,
    val synced: Boolean = false,
    val syncAttempts: Int = 0,
    val lastSyncAttempt: Long? = null
)
