package com.habitarchitect.domain.model

/**
 * Domain model representing an accountability partnership.
 * Partners can view shared habits but NEVER resistance lists.
 */
data class Partnership(
    val id: String,
    val ownerId: String,
    val partnerId: String,
    val inviteCode: String,
    val inviteExpiresAt: Long?,
    val status: PartnershipStatus,
    val createdAt: Long
)
