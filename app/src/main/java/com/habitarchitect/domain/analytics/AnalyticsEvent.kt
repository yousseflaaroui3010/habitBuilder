package com.habitarchitect.domain.analytics

import java.util.UUID

/**
 * Represents an analytics event to be tracked.
 *
 * @property eventId Unique identifier for this event instance
 * @property eventName Name of the event (use constants from AnalyticsEvents)
 * @property timestamp Unix timestamp in milliseconds when the event occurred
 * @property anonymousUserId Hashed user identifier (not the actual user ID)
 * @property properties Event-specific key-value properties
 * @property context Automatic contextual information
 * @property requiredConsentTier Minimum consent level needed to track this event
 */
data class AnalyticsEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val eventName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val anonymousUserId: String,
    val properties: Map<String, Any> = emptyMap(),
    val context: EventContext,
    val requiredConsentTier: ConsentTier = ConsentTier.ESSENTIAL
) {
    /**
     * Builder for creating analytics events with a fluent API.
     */
    class Builder(private val eventName: String) {
        private var anonymousUserId: String = ""
        private var properties: MutableMap<String, Any> = mutableMapOf()
        private var context: EventContext? = null
        private var requiredConsentTier: ConsentTier = ConsentTier.ESSENTIAL

        fun userId(anonymousUserId: String) = apply { this.anonymousUserId = anonymousUserId }

        fun property(key: String, value: Any) = apply { properties[key] = value }

        fun properties(props: Map<String, Any>) = apply { properties.putAll(props) }

        fun context(context: EventContext) = apply { this.context = context }

        fun consentTier(tier: ConsentTier) = apply { this.requiredConsentTier = tier }

        fun build(): AnalyticsEvent {
            requireNotNull(context) { "EventContext must be set" }
            require(anonymousUserId.isNotBlank()) { "Anonymous user ID must be set" }

            return AnalyticsEvent(
                eventName = eventName,
                anonymousUserId = anonymousUserId,
                properties = properties.toMap(),
                context = context!!,
                requiredConsentTier = requiredConsentTier
            )
        }
    }

    companion object {
        fun builder(eventName: String) = Builder(eventName)
    }
}
