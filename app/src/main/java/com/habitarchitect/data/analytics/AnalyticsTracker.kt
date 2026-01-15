package com.habitarchitect.data.analytics

import com.habitarchitect.domain.analytics.AnalyticsEvents
import com.habitarchitect.domain.analytics.ConsentTier
import com.habitarchitect.domain.analytics.EventProperties
import com.habitarchitect.domain.model.DailyStatus
import com.habitarchitect.domain.model.Habit
import com.habitarchitect.domain.model.HabitType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for tracking common analytics events.
 * Provides type-safe methods for each event type.
 */
@Singleton
class AnalyticsTracker @Inject constructor(
    private val analytics: HabitAnalytics
) {
    // ==================== Habit Events ====================

    suspend fun trackHabitCreated(
        habit: Habit,
        fromTemplate: Boolean = false,
        templateId: String? = null
    ) {
        analytics.track(
            eventName = AnalyticsEvents.Habit.CREATED,
            properties = mapOf(
                EventProperties.HABIT_TYPE to habit.type.name,
                EventProperties.HABIT_CATEGORY to (habit.category ?: "uncategorized"),
                EventProperties.FROM_TEMPLATE to fromTemplate,
                EventProperties.TEMPLATE_ID to (templateId ?: "")
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackHabitDeleted(
        habit: Habit,
        habitAgeDays: Int,
        successRate: Float
    ) {
        analytics.track(
            eventName = AnalyticsEvents.Habit.DELETED,
            properties = mapOf(
                EventProperties.HABIT_TYPE to habit.type.name,
                EventProperties.HABIT_CATEGORY to (habit.category ?: "uncategorized"),
                EventProperties.HABIT_AGE_DAYS to habitAgeDays,
                EventProperties.SUCCESS_RATE_7D to successRate
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackHabitArchived(
        habit: Habit,
        habitAgeDays: Int,
        successRate: Float
    ) {
        analytics.track(
            eventName = AnalyticsEvents.Habit.ARCHIVED,
            properties = mapOf(
                EventProperties.HABIT_TYPE to habit.type.name,
                EventProperties.HABIT_CATEGORY to (habit.category ?: "uncategorized"),
                EventProperties.HABIT_AGE_DAYS to habitAgeDays,
                EventProperties.SUCCESS_RATE_7D to successRate
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackHabitEdited(
        habit: Habit,
        fieldsChanged: List<String>,
        habitAgeDays: Int
    ) {
        analytics.track(
            eventName = AnalyticsEvents.Habit.EDITED,
            properties = mapOf(
                EventProperties.HABIT_TYPE to habit.type.name,
                EventProperties.HABIT_AGE_DAYS to habitAgeDays,
                "fields_changed" to fieldsChanged.joinToString(",")
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    suspend fun trackHabitShared(habit: Habit) {
        analytics.track(
            eventName = AnalyticsEvents.Habit.SHARED,
            properties = mapOf(
                EventProperties.HABIT_TYPE to habit.type.name,
                EventProperties.CURRENT_STREAK to habit.currentStreak
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    // ==================== Completion Events ====================

    suspend fun trackHabitMarked(
        habit: Habit,
        status: DailyStatus,
        currentStreak: Int,
        hoursAfterTrigger: Float? = null,
        successRate7d: Float = 0f,
        successRate30d: Float = 0f
    ) {
        val eventName = when (status) {
            DailyStatus.SUCCESS -> AnalyticsEvents.Completion.MARKED_SUCCESS
            DailyStatus.FAILURE -> AnalyticsEvents.Completion.MARKED_FAILURE
            DailyStatus.SKIPPED -> AnalyticsEvents.Completion.MARKED_SKIPPED
            else -> return // Don't track PENDING
        }

        val properties = mutableMapOf<String, Any>(
            EventProperties.HABIT_TYPE to habit.type.name,
            EventProperties.HABIT_CATEGORY to (habit.category ?: "uncategorized"),
            EventProperties.CURRENT_STREAK to currentStreak,
            EventProperties.LONGEST_STREAK to habit.longestStreak,
            EventProperties.SUCCESS_RATE_7D to successRate7d,
            EventProperties.SUCCESS_RATE_30D to successRate30d
        )

        hoursAfterTrigger?.let {
            properties[EventProperties.HOURS_AFTER_TRIGGER] = it
        }

        analytics.track(
            eventName = eventName,
            properties = properties,
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackUndoAction(originalAction: String, timeToUndoMs: Long) {
        analytics.track(
            eventName = AnalyticsEvents.Completion.UNDO_USED,
            properties = mapOf(
                "original_action" to originalAction,
                "time_to_undo_ms" to timeToUndoMs
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    // ==================== Navigation Events ====================

    suspend fun trackScreenViewed(screenName: String, fromScreen: String? = null) {
        val properties = mutableMapOf<String, Any>(
            EventProperties.SCREEN_NAME to screenName
        )
        fromScreen?.let { properties[EventProperties.FROM_SCREEN] = it }

        analytics.track(
            eventName = AnalyticsEvents.Navigation.SCREEN_VIEWED,
            properties = properties,
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackScreenExited(screenName: String, timeSpentMs: Long, exitAction: String) {
        analytics.track(
            eventName = AnalyticsEvents.Navigation.SCREEN_EXITED,
            properties = mapOf(
                EventProperties.SCREEN_NAME to screenName,
                EventProperties.TIME_SPENT_MS to timeSpentMs,
                EventProperties.EXIT_ACTION to exitAction
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    // ==================== Strategy Events ====================

    suspend fun trackCueAdded(habitType: HabitType, habitAgeDays: Int, cueCount: Int) {
        analytics.track(
            eventName = AnalyticsEvents.Strategy.CUE_ADDED,
            properties = mapOf(
                EventProperties.HABIT_TYPE to habitType.name,
                EventProperties.HABIT_AGE_DAYS to habitAgeDays,
                EventProperties.STRATEGY_COUNT to cueCount
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    suspend fun trackFrictionAdded(habitAgeDays: Int, frictionCount: Int) {
        analytics.track(
            eventName = AnalyticsEvents.Strategy.FRICTION_ADDED,
            properties = mapOf(
                EventProperties.HABIT_AGE_DAYS to habitAgeDays,
                EventProperties.STRATEGY_COUNT to frictionCount
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    suspend fun trackFrictionImplemented(habitAgeDays: Int, daysToImplement: Int) {
        analytics.track(
            eventName = AnalyticsEvents.Strategy.FRICTION_IMPLEMENTED,
            properties = mapOf(
                EventProperties.HABIT_AGE_DAYS to habitAgeDays,
                "days_to_implement" to daysToImplement
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    // ==================== Notification Events ====================

    suspend fun trackNotificationSent(type: String, habitCategory: String? = null) {
        val properties = mutableMapOf<String, Any>(
            EventProperties.NOTIFICATION_TYPE to type
        )
        habitCategory?.let { properties[EventProperties.HABIT_CATEGORY] = it }

        analytics.track(
            eventName = AnalyticsEvents.Notification.SENT,
            properties = properties,
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackNotificationOpened(type: String, delaySeconds: Long) {
        analytics.track(
            eventName = AnalyticsEvents.Notification.OPENED,
            properties = mapOf(
                EventProperties.NOTIFICATION_TYPE to type,
                EventProperties.DELAY_SECONDS to delaySeconds
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackNotificationDismissed(type: String, delaySeconds: Long) {
        analytics.track(
            eventName = AnalyticsEvents.Notification.DISMISSED,
            properties = mapOf(
                EventProperties.NOTIFICATION_TYPE to type,
                EventProperties.DELAY_SECONDS to delaySeconds
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackAllGoodClicked(pendingHabitsCount: Int) {
        analytics.track(
            eventName = AnalyticsEvents.Notification.ALL_GOOD_CLICKED,
            properties = mapOf(
                EventProperties.PENDING_COUNT to pendingHabitsCount
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    // ==================== Social Events ====================

    suspend fun trackPartnerInvited(daysSinceSignup: Int) {
        analytics.track(
            eventName = AnalyticsEvents.Social.PARTNER_INVITED,
            properties = mapOf(
                EventProperties.DAYS_SINCE_SIGNUP to daysSinceSignup
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    suspend fun trackPartnerAccepted(inviteAgeHours: Int) {
        analytics.track(
            eventName = AnalyticsEvents.Social.PARTNER_ACCEPTED,
            properties = mapOf(
                EventProperties.INVITE_AGE_HOURS to inviteAgeHours
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    // ==================== Reflection Events ====================

    suspend fun trackReflectionCompleted(
        wentWellWordCount: Int,
        didntGoWellWordCount: Int,
        learnedWordCount: Int,
        timeToCompleteMs: Long
    ) {
        analytics.track(
            eventName = AnalyticsEvents.Reflection.COMPLETED,
            properties = mapOf(
                "went_well_words" to wentWellWordCount,
                "didnt_go_well_words" to didntGoWellWordCount,
                "learned_words" to learnedWordCount,
                EventProperties.DURATION_MS to timeToCompleteMs
            ),
            consentTier = ConsentTier.ENHANCED
        )
    }

    // ==================== Session Events ====================

    suspend fun trackAppOpened(source: String = "direct") {
        analytics.track(
            eventName = AnalyticsEvents.Session.APP_OPENED,
            properties = mapOf("source" to source),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackAppBackgrounded(sessionDurationMs: Long) {
        analytics.track(
            eventName = AnalyticsEvents.Session.APP_BACKGROUNDED,
            properties = mapOf(EventProperties.DURATION_MS to sessionDurationMs),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    // ==================== Settings Events ====================

    suspend fun trackConsentChanged(oldTier: ConsentTier, newTier: ConsentTier) {
        analytics.track(
            eventName = AnalyticsEvents.Settings.CONSENT_CHANGED,
            properties = mapOf(
                "old_tier" to oldTier.name,
                "new_tier" to newTier.name
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackDataExported() {
        analytics.track(
            eventName = AnalyticsEvents.Settings.DATA_EXPORTED,
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    // ==================== Error Events ====================

    suspend fun trackError(errorType: String, errorMessage: String, stackTraceHash: String? = null) {
        val properties = mutableMapOf<String, Any>(
            EventProperties.ERROR_TYPE to errorType,
            EventProperties.ERROR_MESSAGE to errorMessage.take(500) // Limit message length
        )
        stackTraceHash?.let { properties[EventProperties.STACK_TRACE_HASH] = it }

        analytics.track(
            eventName = AnalyticsEvents.Error.ERROR_OCCURRED,
            properties = properties,
            consentTier = ConsentTier.ESSENTIAL
        )
    }

    suspend fun trackSlowOperation(operationName: String, durationMs: Long) {
        analytics.track(
            eventName = AnalyticsEvents.Error.SLOW_OPERATION,
            properties = mapOf(
                EventProperties.OPERATION_NAME to operationName,
                EventProperties.DURATION_MS to durationMs
            ),
            consentTier = ConsentTier.ESSENTIAL
        )
    }
}
