package com.habitarchitect.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs for API requests and responses.
 */

// ==================== Analytics ====================

data class AnalyticsBatchRequest(
    @SerializedName("events") val events: List<AnalyticsEventDto>,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("app_version") val appVersion: String
)

data class AnalyticsEventDto(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("anonymous_user_id") val anonymousUserId: String,
    @SerializedName("properties") val properties: Map<String, Any>,
    @SerializedName("context") val context: Map<String, Any>
)

data class AnalyticsBatchResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("processed_count") val processedCount: Int,
    @SerializedName("failed_event_ids") val failedEventIds: List<String>?
)

// ==================== Habits Sync ====================

data class HabitSyncRequest(
    @SerializedName("habits") val habits: List<HabitDto>,
    @SerializedName("daily_logs") val dailyLogs: List<DailyLogDto>,
    @SerializedName("last_sync_timestamp") val lastSyncTimestamp: Long?
)

data class HabitSyncResponse(
    @SerializedName("habits") val habits: List<HabitDto>,
    @SerializedName("daily_logs") val dailyLogs: List<DailyLogDto>,
    @SerializedName("sync_timestamp") val syncTimestamp: Long,
    @SerializedName("conflicts") val conflicts: List<SyncConflict>?
)

data class HabitDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("category") val category: String?,
    @SerializedName("icon_emoji") val iconEmoji: String,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_shared") val isShared: Boolean,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("updated_at") val updatedAt: Long
)

data class DailyLogDto(
    @SerializedName("id") val id: String,
    @SerializedName("habit_id") val habitId: String,
    @SerializedName("date") val date: String,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: Long
)

data class SyncConflict(
    @SerializedName("entity_type") val entityType: String,
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("local_version") val localVersion: Long,
    @SerializedName("server_version") val serverVersion: Long,
    @SerializedName("resolution") val resolution: String
)

// ==================== Partnerships ====================

data class PartnerInviteRequest(
    @SerializedName("habit_ids") val habitIds: List<String>
)

data class PartnerInviteResponse(
    @SerializedName("partnership_id") val partnershipId: String,
    @SerializedName("invite_code") val inviteCode: String?,
    @SerializedName("partner_id") val partnerId: String?,
    @SerializedName("partner_name") val partnerName: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: Long
)
