package com.habitarchitect.data.sync

/**
 * Represents the sync status of an entity.
 * Used for local-first storage with remote sync.
 */
enum class SyncStatus {
    /** Entity is synced with server */
    SYNCED,
    /** Entity has local changes pending upload */
    PENDING_UPLOAD,
    /** Entity has server changes pending download */
    PENDING_DOWNLOAD,
    /** Entity has conflicts that need resolution */
    CONFLICT,
    /** Entity is being synced */
    SYNCING
}

/**
 * Represents a pending sync operation.
 */
enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}

/**
 * Represents the overall sync state of the app.
 */
sealed class SyncState {
    /** App is idle, all data synced */
    data object Idle : SyncState()

    /** App is currently syncing */
    data class Syncing(val progress: Float = 0f) : SyncState()

    /** Sync completed successfully */
    data class Success(val syncedCount: Int) : SyncState()

    /** Sync failed with error */
    data class Error(val message: String, val retryable: Boolean = true) : SyncState()

    /** App is offline, changes queued */
    data class Offline(val pendingCount: Int) : SyncState()
}
