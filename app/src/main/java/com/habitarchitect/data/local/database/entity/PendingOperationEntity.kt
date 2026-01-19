package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing pending sync operations.
 * Used for offline-first architecture to queue changes when offline.
 */
@Entity(
    tableName = "pending_operations",
    indices = [
        Index(value = ["entityType", "entityId"]),
        Index(value = ["createdAt"]),
        Index(value = ["status"])
    ]
)
data class PendingOperationEntity(
    @PrimaryKey
    val id: String,

    /** Type of entity: HABIT, DAILY_LOG, USER, etc. */
    val entityType: String,

    /** ID of the entity being modified */
    val entityId: String,

    /** Operation type: CREATE, UPDATE, DELETE */
    val operation: String,

    /** JSON payload of the entity data */
    val payload: String,

    /** Status: PENDING, IN_PROGRESS, FAILED, COMPLETED */
    val status: String = "PENDING",

    /** Number of sync attempts */
    val attempts: Int = 0,

    /** Last error message if failed */
    val lastError: String? = null,

    /** Timestamp when operation was created */
    val createdAt: Long,

    /** Timestamp of last sync attempt */
    val lastAttemptAt: Long? = null
)

/**
 * Entity types for sync operations.
 */
object EntityType {
    const val HABIT = "HABIT"
    const val DAILY_LOG = "DAILY_LOG"
    const val USER = "USER"
    const val LIST_ITEM = "LIST_ITEM"
    const val WEEKLY_REFLECTION = "WEEKLY_REFLECTION"
    const val PARTNERSHIP = "PARTNERSHIP"
}

/**
 * Operation status for pending operations.
 */
object OperationStatus {
    const val PENDING = "PENDING"
    const val IN_PROGRESS = "IN_PROGRESS"
    const val FAILED = "FAILED"
    const val COMPLETED = "COMPLETED"
}
