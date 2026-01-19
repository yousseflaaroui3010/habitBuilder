package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.PendingOperationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing pending sync operations.
 * Supports offline-first architecture with operation queue.
 */
@Dao
interface PendingOperationDao {

    /** Get all pending operations ordered by creation time */
    @Query("SELECT * FROM pending_operations WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingOperations(): Flow<List<PendingOperationEntity>>

    /** Get pending operations for sync (excludes completed) */
    @Query("SELECT * FROM pending_operations WHERE status IN ('PENDING', 'FAILED') ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getOperationsToSync(limit: Int = 50): List<PendingOperationEntity>

    /** Get count of pending operations */
    @Query("SELECT COUNT(*) FROM pending_operations WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    /** Get count of pending operations (suspend version) */
    @Query("SELECT COUNT(*) FROM pending_operations WHERE status = 'PENDING'")
    suspend fun getPendingCountOnce(): Int

    /** Get operations for a specific entity */
    @Query("SELECT * FROM pending_operations WHERE entityType = :entityType AND entityId = :entityId ORDER BY createdAt DESC")
    suspend fun getOperationsForEntity(entityType: String, entityId: String): List<PendingOperationEntity>

    /** Get the latest operation for an entity */
    @Query("SELECT * FROM pending_operations WHERE entityType = :entityType AND entityId = :entityId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestOperationForEntity(entityType: String, entityId: String): PendingOperationEntity?

    /** Insert a new pending operation */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: PendingOperationEntity)

    /** Update an existing operation */
    @Update
    suspend fun update(operation: PendingOperationEntity)

    /** Mark operation as in progress */
    @Query("UPDATE pending_operations SET status = 'IN_PROGRESS', lastAttemptAt = :timestamp WHERE id = :operationId")
    suspend fun markInProgress(operationId: String, timestamp: Long = System.currentTimeMillis())

    /** Mark operation as completed */
    @Query("UPDATE pending_operations SET status = 'COMPLETED' WHERE id = :operationId")
    suspend fun markCompleted(operationId: String)

    /** Mark operation as failed with error */
    @Query("UPDATE pending_operations SET status = 'FAILED', attempts = attempts + 1, lastError = :error, lastAttemptAt = :timestamp WHERE id = :operationId")
    suspend fun markFailed(operationId: String, error: String, timestamp: Long = System.currentTimeMillis())

    /** Reset failed operations to pending (for retry) */
    @Query("UPDATE pending_operations SET status = 'PENDING' WHERE status = 'FAILED' AND attempts < :maxAttempts")
    suspend fun resetFailedOperations(maxAttempts: Int = 5)

    /** Delete completed operations older than threshold */
    @Query("DELETE FROM pending_operations WHERE status = 'COMPLETED' AND createdAt < :threshold")
    suspend fun cleanupCompletedOperations(threshold: Long)

    /** Delete all operations for an entity (used when entity is deleted) */
    @Query("DELETE FROM pending_operations WHERE entityType = :entityType AND entityId = :entityId")
    suspend fun deleteOperationsForEntity(entityType: String, entityId: String)

    /** Delete a specific operation */
    @Query("DELETE FROM pending_operations WHERE id = :operationId")
    suspend fun delete(operationId: String)

    /** Consolidate operations: remove redundant creates/updates if delete exists */
    @Query("""
        DELETE FROM pending_operations
        WHERE entityType = :entityType
        AND entityId = :entityId
        AND operation IN ('CREATE', 'UPDATE')
        AND EXISTS (
            SELECT 1 FROM pending_operations p2
            WHERE p2.entityType = :entityType
            AND p2.entityId = :entityId
            AND p2.operation = 'DELETE'
        )
    """)
    suspend fun consolidateOperations(entityType: String, entityId: String)

    /** Get failed operations for retry */
    @Query("SELECT * FROM pending_operations WHERE status = 'FAILED' AND attempts < :maxAttempts ORDER BY lastAttemptAt ASC LIMIT :limit")
    suspend fun getFailedOperationsForRetry(maxAttempts: Int = 5, limit: Int = 10): List<PendingOperationEntity>
}
