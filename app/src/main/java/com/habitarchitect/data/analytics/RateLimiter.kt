package com.habitarchitect.data.analytics

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rate limiter for analytics events to prevent spam and excessive data collection.
 * Uses sliding window algorithm with per-event-type limits.
 */
@Singleton
class RateLimiter @Inject constructor() {

    companion object {
        // Global limits
        private const val GLOBAL_MAX_EVENTS_PER_MINUTE = 60
        private const val GLOBAL_MAX_EVENTS_PER_HOUR = 500
        private const val WINDOW_SIZE_MS = 60_000L // 1 minute

        // Per-event-type limits (events per minute)
        private val EVENT_LIMITS = mapOf(
            "screen_viewed" to 20,
            "screen_exited" to 20,
            "habit_marked_success" to 30,
            "habit_marked_failure" to 30,
            "error_occurred" to 10,
            "slow_operation" to 5
        )

        // Default limit for unlisted events
        private const val DEFAULT_EVENT_LIMIT = 30
    }

    // Track event timestamps: eventName -> list of timestamps
    private val eventTimestamps = mutableMapOf<String, MutableList<Long>>()
    private val globalTimestamps = mutableListOf<Long>()

    /**
     * Check if an event should be allowed based on rate limits.
     * @return true if event is allowed, false if rate limited
     */
    @Synchronized
    fun shouldAllowEvent(eventName: String): Boolean {
        val now = System.currentTimeMillis()

        // Clean old timestamps
        cleanOldTimestamps(now)

        // Check global rate limit
        if (globalTimestamps.size >= GLOBAL_MAX_EVENTS_PER_MINUTE) {
            Timber.w("Analytics rate limited: global limit exceeded ($GLOBAL_MAX_EVENTS_PER_MINUTE/min)")
            return false
        }

        // Check per-event rate limit
        val eventLimit = EVENT_LIMITS[eventName] ?: DEFAULT_EVENT_LIMIT
        val eventTimes = eventTimestamps.getOrPut(eventName) { mutableListOf() }

        if (eventTimes.size >= eventLimit) {
            Timber.w("Analytics rate limited: $eventName limit exceeded ($eventLimit/min)")
            return false
        }

        // Record this event
        globalTimestamps.add(now)
        eventTimes.add(now)

        return true
    }

    /**
     * Remove timestamps older than the window size.
     */
    private fun cleanOldTimestamps(now: Long) {
        val cutoff = now - WINDOW_SIZE_MS

        globalTimestamps.removeAll { it < cutoff }

        eventTimestamps.forEach { (_, timestamps) ->
            timestamps.removeAll { it < cutoff }
        }

        // Remove empty entries
        eventTimestamps.entries.removeAll { it.value.isEmpty() }
    }

    /**
     * Get current event count for monitoring.
     */
    fun getCurrentEventCount(): Int = globalTimestamps.size

    /**
     * Get rate limit status for diagnostics.
     */
    fun getRateLimitStatus(): Map<String, Int> {
        return eventTimestamps.mapValues { it.value.size } + ("global" to globalTimestamps.size)
    }

    /**
     * Reset all rate limits (for testing).
     */
    @Synchronized
    fun reset() {
        eventTimestamps.clear()
        globalTimestamps.clear()
    }
}
