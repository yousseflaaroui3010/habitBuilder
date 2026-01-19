package com.habitarchitect.data.sync

import com.google.gson.Gson
import com.habitarchitect.data.local.database.dao.HabitDao
import com.habitarchitect.data.local.database.dao.DailyLogDao
import com.habitarchitect.data.local.database.entity.EntityType
import com.habitarchitect.data.local.database.entity.HabitEntity
import com.habitarchitect.data.local.database.entity.DailyLogEntity
import com.habitarchitect.data.local.database.entity.PendingOperationEntity
import com.habitarchitect.data.remote.api.HabitArchitectApi
import com.habitarchitect.data.remote.dto.DailyLogDto
import com.habitarchitect.data.remote.dto.HabitDto
import com.habitarchitect.data.remote.dto.HabitSyncRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates sync operations between local storage and remote API.
 * Implements local-first architecture with background sync.
 */
@Singleton
class SyncManager @Inject constructor(
    private val api: HabitArchitectApi,
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
    private val offlineQueue: OfflineQueue,
    private val networkMonitor: NetworkMonitor,
    private val gson: Gson
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    init {
        // Monitor network changes and trigger sync when online
        scope.launch {
            networkMonitor.connectivityFlow.collect { isOnline ->
                if (isOnline) {
                    val pendingCount = offlineQueue.getPendingCountOnce()
                    if (pendingCount > 0) {
                        Timber.d("Network restored, syncing $pendingCount pending operations")
                        syncPendingOperations()
                    }
                } else {
                    val pendingCount = offlineQueue.getPendingCountOnce()
                    _syncState.value = SyncState.Offline(pendingCount)
                }
            }
        }
    }

    /**
     * Trigger a full sync (push local changes, pull remote changes).
     */
    suspend fun sync(userId: String): Result<Int> {
        if (!networkMonitor.isOnline.value) {
            val pendingCount = offlineQueue.getPendingCountOnce()
            _syncState.value = SyncState.Offline(pendingCount)
            return Result.failure(Exception("No network connection"))
        }

        return try {
            _syncState.value = SyncState.Syncing(0f)

            // Step 1: Push pending operations
            val pushedCount = syncPendingOperations()
            _syncState.value = SyncState.Syncing(0.5f)

            // Step 2: Pull remote changes
            val pulledCount = pullRemoteChanges(userId)
            _syncState.value = SyncState.Syncing(1f)

            val totalSynced = pushedCount + pulledCount
            _lastSyncTime.value = System.currentTimeMillis()
            _syncState.value = SyncState.Success(totalSynced)

            Timber.d("Sync completed: pushed=$pushedCount, pulled=$pulledCount")
            Result.success(totalSynced)
        } catch (e: Exception) {
            Timber.e(e, "Sync failed")
            _syncState.value = SyncState.Error(e.message ?: "Sync failed", retryable = true)
            Result.failure(e)
        }
    }

    /**
     * Process pending operations from the offline queue.
     */
    suspend fun syncPendingOperations(): Int {
        if (!networkMonitor.isOnline.value) {
            return 0
        }

        val operations = offlineQueue.getOperationsToSync()
        if (operations.isEmpty()) {
            return 0
        }

        Timber.d("Processing ${operations.size} pending operations")
        var syncedCount = 0

        for (operation in operations) {
            try {
                offlineQueue.markInProgress(operation.id)
                processOperation(operation)
                offlineQueue.markCompleted(operation.id)
                syncedCount++
            } catch (e: Exception) {
                Timber.w(e, "Failed to process operation ${operation.id}")
                offlineQueue.markFailed(operation.id, e.message ?: "Unknown error")
            }
        }

        return syncedCount
    }

    /**
     * Process a single pending operation.
     */
    private suspend fun processOperation(operation: PendingOperationEntity) {
        when (operation.entityType) {
            EntityType.HABIT -> processHabitOperation(operation)
            EntityType.DAILY_LOG -> processDailyLogOperation(operation)
            else -> Timber.w("Unknown entity type: ${operation.entityType}")
        }
    }

    /**
     * Process habit sync operation.
     */
    private suspend fun processHabitOperation(operation: PendingOperationEntity) {
        when (operation.operation) {
            SyncOperation.CREATE.name, SyncOperation.UPDATE.name -> {
                val habit = gson.fromJson(operation.payload, HabitEntity::class.java)
                val habitDto = habit.toDto()
                val request = HabitSyncRequest(
                    habits = listOf(habitDto),
                    dailyLogs = emptyList(),
                    lastSyncTimestamp = _lastSyncTime.value
                )
                api.syncHabits(request)
            }
            SyncOperation.DELETE.name -> {
                // API would need a delete endpoint
                Timber.d("Delete operation for habit ${operation.entityId}")
            }
        }
    }

    /**
     * Process daily log sync operation.
     */
    private suspend fun processDailyLogOperation(operation: PendingOperationEntity) {
        when (operation.operation) {
            SyncOperation.CREATE.name, SyncOperation.UPDATE.name -> {
                val log = gson.fromJson(operation.payload, DailyLogEntity::class.java)
                val logDto = log.toDto()
                val request = HabitSyncRequest(
                    habits = emptyList(),
                    dailyLogs = listOf(logDto),
                    lastSyncTimestamp = _lastSyncTime.value
                )
                api.syncHabits(request)
            }
            SyncOperation.DELETE.name -> {
                Timber.d("Delete operation for daily log ${operation.entityId}")
            }
        }
    }

    /**
     * Pull changes from remote server.
     */
    private suspend fun pullRemoteChanges(userId: String): Int {
        return try {
            val response = api.getHabits(userId)
            if (response.isSuccessful) {
                val habits = response.body()?.habits ?: emptyList()
                // TODO: Merge remote habits with local, handling conflicts
                Timber.d("Pulled ${habits.size} habits from server")
                habits.size
            } else {
                Timber.w("Failed to pull habits: ${response.code()}")
                0
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to pull remote changes")
            0
        }
    }

    /**
     * Reset sync state to idle.
     */
    fun resetState() {
        _syncState.value = SyncState.Idle
    }

    /**
     * Check if sync is in progress.
     */
    fun isSyncing(): Boolean = _syncState.value is SyncState.Syncing

    /**
     * Cleanup old completed operations.
     */
    suspend fun cleanup() {
        offlineQueue.cleanup()
    }
}

/**
 * Extension to convert HabitEntity to HabitDto.
 */
private fun HabitEntity.toDto(): HabitDto {
    return HabitDto(
        id = id,
        userId = userId,
        name = name,
        type = type,
        category = category,
        templateId = templateId,
        iconEmoji = iconEmoji,
        triggerTime = triggerTime,
        triggerContext = triggerContext,
        frequency = frequency,
        activeDays = activeDays?.split("|") ?: emptyList(),
        location = location,
        goal = goal,
        minimumVersion = minimumVersion,
        stackAnchor = stackAnchor,
        reward = reward,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalSuccessDays = totalSuccessDays,
        totalFailureDays = totalFailureDays,
        paperClipCount = paperClipCount,
        paperClipGoal = paperClipGoal,
        isSharedWithPartner = isSharedWithPartner,
        orderIndex = orderIndex,
        priority = priority,
        isArchived = isArchived,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Extension to convert DailyLogEntity to DailyLogDto.
 */
private fun DailyLogEntity.toDto(): DailyLogDto {
    return DailyLogDto(
        habitId = habitId,
        date = date,
        status = status,
        markedAt = markedAt,
        note = note
    )
}
