package com.habitarchitect.domain.analytics

/**
 * Constants for all analytics event names used in the app.
 * Organized by feature area for easy discovery.
 */
object AnalyticsEvents {

    /**
     * Habit lifecycle events - creation, modification, deletion
     */
    object Habit {
        const val CREATED = "habit_created"
        const val EDITED = "habit_edited"
        const val DELETED = "habit_deleted"
        const val ARCHIVED = "habit_archived"
        const val UNARCHIVED = "habit_unarchived"
        const val REORDERED = "habit_reordered"
        const val SHARED = "habit_shared"
        const val UNSHARED = "habit_unshared"
        const val REMINDER_ENABLED = "habit_reminder_enabled"
        const val REMINDER_DISABLED = "habit_reminder_disabled"
    }

    /**
     * Daily habit completion events
     */
    object Completion {
        const val MARKED_SUCCESS = "habit_marked_success"
        const val MARKED_FAILURE = "habit_marked_failure"
        const val MARKED_SKIPPED = "habit_marked_skipped"
        const val UNDO_USED = "undo_action_used"
    }

    /**
     * Strategy engagement events (cues, friction, etc.)
     */
    object Strategy {
        const val CUE_ADDED = "cue_added"
        const val CUE_DELETED = "cue_deleted"
        const val FRICTION_ADDED = "friction_added"
        const val FRICTION_IMPLEMENTED = "friction_implemented"
        const val FRICTION_DELETED = "friction_deleted"
        const val COST_JOURNALED = "cost_journaled"
        const val COST_DELETED = "cost_deleted"
        const val TEMPTATION_BUNDLE_SET = "temptation_bundle_set"
        const val TEMPTATION_BUNDLE_CLEARED = "temptation_bundle_cleared"
        const val RESISTANCE_ADDED = "resistance_item_added"
        const val RESISTANCE_DELETED = "resistance_item_deleted"
        const val ATTRACTION_ADDED = "attraction_item_added"
        const val ATTRACTION_DELETED = "attraction_item_deleted"
    }

    /**
     * Navigation and screen events
     */
    object Navigation {
        const val SCREEN_VIEWED = "screen_viewed"
        const val SCREEN_EXITED = "screen_exited"
        const val BOTTOM_NAV_CLICKED = "bottom_nav_clicked"
        const val HABIT_CARD_CLICKED = "habit_card_clicked"
        const val CALENDAR_DATE_CLICKED = "calendar_date_clicked"
        const val BACK_PRESSED = "back_pressed"
    }

    /**
     * Notification and reminder events
     */
    object Notification {
        const val SENT = "notification_sent"
        const val OPENED = "notification_opened"
        const val DISMISSED = "notification_dismissed"
        const val ALL_GOOD_CLICKED = "all_good_clicked"
        const val SETTINGS_CHANGED = "notification_settings_changed"
    }

    /**
     * Social and partnership events
     */
    object Social {
        const val PARTNER_INVITED = "partner_invited"
        const val PARTNER_ACCEPTED = "partner_accepted"
        const val PARTNER_REMOVED = "partner_removed"
        const val PARTNER_VIEWED = "partner_habits_viewed"
    }

    /**
     * Reflection events
     */
    object Reflection {
        const val STARTED = "reflection_started"
        const val COMPLETED = "reflection_completed"
        const val EDITED = "reflection_edited"
    }

    /**
     * Session and app lifecycle events
     */
    object Session {
        const val APP_OPENED = "app_opened"
        const val APP_BACKGROUNDED = "app_backgrounded"
        const val APP_FOREGROUNDED = "app_foregrounded"
        const val SESSION_STARTED = "session_started"
        const val SESSION_ENDED = "session_ended"
    }

    /**
     * Settings and preference events
     */
    object Settings {
        const val THEME_CHANGED = "theme_changed"
        const val CONSENT_CHANGED = "consent_changed"
        const val DATA_EXPORTED = "data_exported"
        const val ACCOUNT_DELETED = "account_deleted"
        const val SIGNED_OUT = "signed_out"
    }

    /**
     * Onboarding events
     */
    object Onboarding {
        const val STARTED = "onboarding_started"
        const val COMPLETED = "onboarding_completed"
        const val SKIPPED = "onboarding_skipped"
        const val STEP_VIEWED = "onboarding_step_viewed"
    }

    /**
     * ML/Recommendation events
     */
    object ML {
        const val RECOMMENDATION_SHOWN = "recommendation_shown"
        const val RECOMMENDATION_ACCEPTED = "recommendation_accepted"
        const val RECOMMENDATION_REJECTED = "recommendation_rejected"
        const val PREDICTION_SHOWN = "prediction_shown"
        const val FEEDBACK_PROVIDED = "ml_feedback_provided"
    }

    /**
     * Error and crash events
     */
    object Error {
        const val ERROR_OCCURRED = "error_occurred"
        const val CRASH_DETECTED = "crash_detected"
        const val SLOW_OPERATION = "slow_operation"
    }
}

/**
 * Common property keys used across events.
 */
object EventProperties {
    // Habit properties
    const val HABIT_ID = "habit_id"
    const val HABIT_TYPE = "habit_type"
    const val HABIT_CATEGORY = "habit_category"
    const val HABIT_AGE_DAYS = "habit_age_days"
    const val FROM_TEMPLATE = "from_template"
    const val TEMPLATE_ID = "template_id"

    // Completion properties
    const val STATUS = "status"
    const val CURRENT_STREAK = "current_streak"
    const val LONGEST_STREAK = "longest_streak"
    const val SUCCESS_RATE_7D = "success_rate_7d"
    const val SUCCESS_RATE_30D = "success_rate_30d"
    const val HOURS_AFTER_TRIGGER = "hours_after_trigger"

    // Position properties
    const val OLD_POSITION = "old_position"
    const val NEW_POSITION = "new_position"
    const val SCROLL_POSITION = "scroll_position"

    // Screen properties
    const val SCREEN_NAME = "screen_name"
    const val FROM_SCREEN = "from_screen"
    const val TIME_SPENT_MS = "time_spent_ms"
    const val EXIT_ACTION = "exit_action"

    // Notification properties
    const val NOTIFICATION_TYPE = "notification_type"
    const val DELAY_SECONDS = "delay_seconds"
    const val PENDING_COUNT = "pending_count"

    // Strategy properties
    const val STRATEGY_TYPE = "strategy_type"
    const val STRATEGY_COUNT = "strategy_count"
    const val WORD_COUNT = "word_count"

    // Timing properties
    const val DAYS_SINCE_SIGNUP = "days_since_signup"
    const val DAYS_SINCE_CREATED = "days_since_created"
    const val DURATION_MS = "duration_ms"

    // Partner properties
    const val PARTNERSHIP_DURATION_DAYS = "partnership_duration_days"
    const val INVITE_AGE_HOURS = "invite_age_hours"

    // ML properties
    const val RECOMMENDATION_TYPE = "recommendation_type"
    const val RECOMMENDATION_ID = "recommendation_id"
    const val CONFIDENCE_SCORE = "confidence_score"
    const val FEEDBACK_TYPE = "feedback_type"

    // Error properties
    const val ERROR_TYPE = "error_type"
    const val ERROR_MESSAGE = "error_message"
    const val STACK_TRACE_HASH = "stack_trace_hash"
    const val OPERATION_NAME = "operation_name"
}
