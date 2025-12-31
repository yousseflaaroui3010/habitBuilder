package com.habitarchitect.data.mapper

import com.habitarchitect.data.local.database.entity.PartnershipEntity
import com.habitarchitect.domain.model.Partnership
import com.habitarchitect.domain.model.PartnershipStatus

/**
 * Mapper functions between Partnership domain model and PartnershipEntity.
 */
fun PartnershipEntity.toDomain(): Partnership {
    return Partnership(
        id = id,
        ownerId = ownerId,
        partnerId = partnerId,
        inviteCode = inviteCode,
        inviteExpiresAt = inviteExpiresAt,
        status = PartnershipStatus.valueOf(status),
        createdAt = createdAt
    )
}

fun Partnership.toEntity(): PartnershipEntity {
    return PartnershipEntity(
        id = id,
        ownerId = ownerId,
        partnerId = partnerId,
        inviteCode = inviteCode,
        inviteExpiresAt = inviteExpiresAt,
        status = status.name,
        createdAt = createdAt
    )
}
