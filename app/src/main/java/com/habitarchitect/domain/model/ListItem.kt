package com.habitarchitect.domain.model

/**
 * Domain model representing a resistance or attraction list item.
 * Resistance lists contain reasons NOT to do a bad habit.
 * Attraction lists contain reasons TO do a good habit.
 */
data class ListItem(
    val id: String,
    val habitId: String,
    val type: ListItemType,
    val content: String,
    val orderIndex: Int,
    val isFromTemplate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
