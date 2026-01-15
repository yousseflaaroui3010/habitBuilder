package com.habitarchitect.domain.analytics

/**
 * Consent tiers for data collection.
 * Each tier includes all permissions from lower tiers.
 */
enum class ConsentTier {
    /**
     * Essential analytics only - anonymous, aggregated data.
     * No consent required. Used for crash reporting and basic app metrics.
     */
    ESSENTIAL,

    /**
     * Enhanced analytics - detailed behavioral patterns.
     * Requires user opt-in. Enables personalized insights.
     */
    ENHANCED,

    /**
     * Research contribution - de-identified data for ML training.
     * Requires explicit consent. Helps improve recommendations globally.
     */
    RESEARCH,

    /**
     * Personal insights - identity-linked data.
     * Requires consent. Enables personal progress reports and data export.
     */
    PERSONAL
}
