package com.habitarchitect.data.analytics

import com.habitarchitect.domain.analytics.AnalyticsEvent
import com.habitarchitect.domain.analytics.ConsentTier
import com.habitarchitect.domain.analytics.EventContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main implementation of the analytics SDK.
 * Handles event tracking, consent filtering, batching, and context enrichment.
 */
@Singleton
class HabitAnalytics @Inject constructor(
    private val localStorage: AnalyticsLocalStorage,
    private val sessionManager: SessionManager,
    private val consentManager: ConsentManager,
    private val anonymousIdGenerator: AnonymousIdGenerator,
    private val deviceInfoProvider: DeviceInfoProvider
) : AnalyticsSDK {

    companion object {
        const val BATCH_SIZE_THRESHOLD = 100
        const val BATCH_TIME_THRESHOLD_MS = 60_000L // 60 seconds
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var currentUserId: String? = null
    private var currentAnonymousId: String? = null
    private val userProperties = mutableMapOf<String, Any>()

    private var pendingEventCount = 0
    private var lastFlushTime = System.currentTimeMillis()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    /**
     * Track an analytics event.
     */
    override suspend fun trackEvent(event: AnalyticsEvent) {
        // Check consent
        if (!consentManager.canTrack(event.requiredConsentTier)) {
            return
        }

        // Store locally
        localStorage.store(event)
        pendingEventCount++

        // Check if we should flush
        checkFlushConditions()
    }

    /**
     * Track an event with simple parameters.
     */
    override suspend fun track(
        eventName: String,
        properties: Map<String, Any>,
        consentTier: ConsentTier
    ) {
        // Check consent early to avoid unnecessary work
        if (!consentManager.canTrack(consentTier)) {
            return
        }

        val anonymousId = currentAnonymousId ?: run {
            currentUserId?.let { anonymousIdGenerator.generateAnonymousId(it) } ?: "anonymous"
        }

        val context = createEventContext()

        // Merge user properties with event properties
        val mergedProperties = userProperties.toMutableMap().apply {
            putAll(properties)
        }

        val event = AnalyticsEvent(
            eventName = eventName,
            anonymousUserId = anonymousId,
            properties = mergedProperties,
            context = context,
            requiredConsentTier = consentTier
        )

        trackEvent(event)
    }

    /**
     * Set a user property.
     */
    override suspend fun setUserProperty(key: String, value: Any) {
        userProperties[key] = value
    }

    /**
     * Set the current user ID.
     */
    override suspend fun setUserId(userId: String) {
        currentUserId = userId
        currentAnonymousId = anonymousIdGenerator.generateAnonymousId(userId)
        _isInitialized.value = true
    }

    /**
     * Clear user data.
     */
    override suspend fun clearUser() {
        currentUserId = null
        currentAnonymousId = null
        userProperties.clear()
        anonymousIdGenerator.clearCache()
        sessionManager.endSession()
        _isInitialized.value = false
    }

    /**
     * Force flush pending events.
     */
    override suspend fun flush() {
        // In a real implementation, this would sync to the backend
        // For now, just update the last flush time
        lastFlushTime = System.currentTimeMillis()
        pendingEventCount = 0

        // Cleanup old synced events
        localStorage.cleanupSyncedEvents()
    }

    /**
     * Get count of pending events.
     */
    override suspend fun getPendingEventCount(): Int {
        return localStorage.getPendingCount()
    }

    /**
     * Check if tracking is enabled for a consent tier.
     */
    override suspend fun isTrackingEnabled(tier: ConsentTier): Boolean {
        return consentManager.canTrack(tier)
    }

    /**
     * Create event context with current device and session info.
     */
    private suspend fun createEventContext(): EventContext {
        val now = LocalDate.now()
        val time = LocalTime.now()

        // Record activity to keep session alive
        sessionManager.recordActivity()

        return EventContext.create(
            sessionId = sessionManager.getSessionId(),
            sessionDurationMs = sessionManager.getSessionDuration(),
            appVersion = deviceInfoProvider.getAppVersion(),
            osVersion = deviceInfoProvider.getOsVersion(),
            deviceType = deviceInfoProvider.getDeviceType(),
            screenDensityBucket = deviceInfoProvider.getScreenDensityBucket(),
            now = now,
            time = time
        )
    }

    /**
     * Check if we should flush based on count or time.
     */
    private fun checkFlushConditions() {
        val shouldFlush = pendingEventCount >= BATCH_SIZE_THRESHOLD ||
            (System.currentTimeMillis() - lastFlushTime) >= BATCH_TIME_THRESHOLD_MS

        if (shouldFlush) {
            scope.launch {
                flush()
            }
        }
    }

    /**
     * Initialize analytics on app start.
     */
    suspend fun initialize(userId: String?) {
        // Start new session if needed
        sessionManager.getSessionId()

        // Set user if provided
        userId?.let { setUserId(it) }

        _isInitialized.value = true
    }

    /**
     * Handle app going to background.
     */
    suspend fun onAppBackground() {
        flush()
    }

    /**
     * Handle app coming to foreground.
     */
    suspend fun onAppForeground() {
        sessionManager.recordActivity()
    }
}
