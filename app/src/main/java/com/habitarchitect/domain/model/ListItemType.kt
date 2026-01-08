package com.habitarchitect.domain.model

/**
 * Type of list item:
 * - RESISTANCE: reasons NOT to do bad habit (shown during temptation)
 * - ATTRACTION: reasons TO do good habit
 * - TRIGGER: what triggered a failure (used for pattern detection)
 */
enum class ListItemType {
    RESISTANCE,
    ATTRACTION,
    TRIGGER
}
