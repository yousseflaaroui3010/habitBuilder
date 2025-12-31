package com.habitarchitect.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing partnership data.
 */
@Entity(
    tableName = "partnerships",
    indices = [
        Index(value = ["ownerId"]),
        Index(value = ["partnerId"]),
        Index(value = ["inviteCode"])
    ]
)
data class PartnershipEntity(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val partnerId: String,
    val inviteCode: String,
    val inviteExpiresAt: Long?,
    val status: String,
    val createdAt: Long
)
