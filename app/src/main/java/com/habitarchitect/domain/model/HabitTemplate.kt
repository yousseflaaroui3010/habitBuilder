package com.habitarchitect.domain.model

/**
 * Domain model representing a pre-built habit template.
 */
data class HabitTemplate(
    val id: String,
    val name: String,
    val type: HabitType,
    val category: String,
    val iconEmoji: String,
    val description: String,

    // For BREAK habits
    val defaultResistanceItems: List<String> = emptyList(),
    val defaultFrictionStrategies: List<String> = emptyList(),
    val defaultTriggerContexts: List<String> = emptyList(),

    // For BUILD habits
    val defaultAttractionItems: List<String> = emptyList(),
    val defaultMinimumVersion: String? = null,
    val defaultStackAnchors: List<String> = emptyList(),
    val defaultRewards: List<String> = emptyList()
)
