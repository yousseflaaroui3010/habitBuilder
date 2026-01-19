package com.habitarchitect.data.sync

import com.google.gson.Gson
import com.habitarchitect.data.local.database.dao.PendingOperationDao
import com.habitarchitect.data.local.database.entity.EntityType
import com.habitarchitect.data.local.database.entity.OperationStatus
import com.habitarchitect.data.local.database.entity.PendingOperationEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the offline operation queue.
 * Queues operations when offline and processes them when online.
 */
@Singleton
class OfflineQueue @Inject constructor(
    private val pendingOperationDao: PendingOperationDao,
    private val gson: Gson
) {
    companion object {
        private const val MAX_RETRY_ATTEMPTS = 5
        private const val CLEANUP_THRESHOLD_MS = 7 * 24 * 60 * 60 * 1000L // 7 days
    }

    /** Flow of pending operation count */
    val pendingCount: Flow<Int> = pendingOperationDao.getPendingCount()

    /** Flow of all pending operations */
    val pendingOperations: Flow<List<PendingOperationEntity>> = pendingOperationDao.getPendingOperations()

    /**
     * Queue a create operation.
     */
    suspend fun <T> queueCreate(entityType: String, entityId: String, entity: T) {
        val payload = gson.toJson(entity)
        queueOperation(entityType, entityId, SyncOperation.CREATE, payload)
    }

    /**
     * Queue an update operation.
     */
    suspend fun <T> queueUpdate(entityType: String, entityId: String, entity: T) {
        val payload = gson.toJson(entity)

        // Check if there's a pending CREATE - if so, just update the payload
        val existingCreate = pendingOperationDao.getLatestOperationForEntity(entityType, entityId)
        if (existingCreate != null && existingCreate.operation == SyncOperation.CREATE.name) {
            val updated = existingCreate.copy(payload = payload)
            pendingOperationDao.update(updated)
            Timber.d("Updated pending CREATE operation for $entityType:$entityId")
            return
        }

        queueOperation(entityType, entityId, SyncOperation.UPDATE, payload)
    }

    /**
     * Queue a delete operation.
     */
    suspend fun queueDelete(entityType: String, entityId: String) {
        // Consolidate: remove any pending creates/updates for this entity
        pendingOperationDao.consolidateOperations(entityType, entityId)

        // Check if entity was never synced (only had CREATE)
        val operations = pendingOperationDao.getOperationsForEntity(entityType, entityId)
        if (operations.isEmpty()) {
            // Entity was never synced, no need to queue delete
            Timber.d("Entity $entityType:$entityId was never synced, skipping delete queue")
            return
        }

        queueOperation(entityType, entityId, SyncOperation.DELETE, "")
    }

    /**
     * Queue a sync operation.
     */
    private suspend fun queueOperation(
        entityType: String,
        entityId: String,
        operation: SyncOperation,
        payload: String
    ) {
        // Check if there's already a pending operation of the same type
        val existing = pendingOperationDao.getLatestOperationForEntity(entityType, entityId)
        if (existing != null && existing.operation == operation.name && existing.status == OperationStatus.PENDING) {
            // Update existing operation with new payload
            val updated = existing.copy(payload = payload)
            pendingOperationDao.update(updated)
            Timber.d("Updated existing $operation for $entityType:$entityId")
            return
        }

        val operationEntity = PendingOperationEntity(
            id = UUID.randomUUID().toString(),
            entityType = entityType,
            entityId = entityId,
            operation = operation.name,
            payload = payload,
            status = OperationStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )

        pendingOperationDao.insert(operationEntity)
        Timber.d("Queued $operation for $entityType:$entityId")
    }

    /**
     * Get operations ready for sync.
     */
    suspend fun getOperationsToSync(limit: Int = 50): List<PendingOperationEntity> {
        return pendingOperationDao.getOperationsToSync(limit)
    }

    /**
     * Mark operation as in progress.
     */
    suspend fun markInProgress(operationId: String) {
        pendingOperationDao.markInProgress(operationId)
    }

    /**
     * Mark operation as completed (synced successfully).
     */
    suspend fun markCompleted(operationId: String) {
        pendingOperationDao.markCompleted(operationId)
        Timber.d("Operation $operationId completed")
    }

    /**
     * Mark operation as failed.
     */
    suspend fun markFailed(operationId: String, error: String) {
        pendingOperationDao.markFailed(operationId, error)
        Timber.w("Operation $operationId failed: $error")
    }

    /**
     * Reset failed operations for retry.
     */
    suspend fun resetFailedOperations() {
        pendingOperationDao.resetFailedOperations(MAX_RETRY_ATTEMPTS)
    }

    /**
     * Get pending count synchronously.
     */
    suspend fun getPendingCountOnce(): Int {
        return pendingOperationDao.getPendingCountOnce()
    }

    /**
     * Cleanup old completed operations.
     */
    suspend fun cleanup() {
        val threshold = System.currentTimeMillis() - CLEANUP_THRESHOLD_MS
        pendingOperationDao.cleanupCompletedOperations(threshold)
        Timber.d("Cleaned up completed operations older than 7 days")
    }

    /**
     * Check if entity has pending operations.
     */
    suspend fun hasPendingOperations(entityType: String, entityId: String): Boolean {
        return pendingOperationDao.getOperationsForEntity(entityType, entityId)
            .any { it.status == OperationStatus.PENDING || it.status == OperationStatus.IN_PROGRESS }
    }

    /**
     * Cancel all pending operations for an entity.
     */
    suspend fun cancelOperationsForEntity(entityType: String, entityId: String) {
        pendingOperationDao.deleteOperationsForEntity(entityType, entityId)
        Timber.d("Cancelled all operations for $entityType:$entityId")
    }
}
