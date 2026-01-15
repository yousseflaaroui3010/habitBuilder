package com.habitarchitect.data.analytics

import com.habitarchitect.domain.analytics.AnalyticsEvent
import com.habitarchitect.domain.analytics.ConsentTier

/**
 * Interface for analytics tracking.
 * Provides a clean API for tracking events throughout the app.
 */
interface AnalyticsSDK {

    /**
     * Track an analytics event.
     * The event will be filtered based on consent level.
     */
    suspend fun trackEvent(event: AnalyticsEvent)

    /**
     * Track an event with a simple builder pattern.
     */
    suspend fun track(
        eventName: String,
        properties: Map<String, Any> = emptyMap(),
        consentTier: ConsentTier = ConsentTier.ESSENTIAL
    )

    /**
     * Set a user property that will be attached to future events.
     */
    suspend fun setUserProperty(key: String, value: Any)

    /**
     * Set the current user ID (will be anonymized).
     */
    suspend fun setUserId(userId: String)

    /**
     * Clear user data (e.g., on logout).
     */
    suspend fun clearUser()

    /**
     * Force flush pending events to backend.
     */
    suspend fun flush()

    /**
     * Get count of pending events.
     */
    suspend fun getPendingEventCount(): Int

    /**
     * Check if tracking is enabled for a consent tier.
     */
    suspend fun isTrackingEnabled(tier: ConsentTier): Boolean
}
