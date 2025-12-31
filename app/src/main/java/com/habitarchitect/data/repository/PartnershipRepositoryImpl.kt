package com.habitarchitect.data.repository

import com.habitarchitect.data.local.database.dao.PartnershipDao
import com.habitarchitect.data.mapper.toDomain
import com.habitarchitect.data.mapper.toEntity
import com.habitarchitect.domain.model.Partnership
import com.habitarchitect.domain.model.PartnershipStatus
import com.habitarchitect.domain.repository.PartnershipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of PartnershipRepository using Room database.
 */
class PartnershipRepositoryImpl @Inject constructor(
    private val partnershipDao: PartnershipDao
) : PartnershipRepository {

    override fun getPartnershipsForUser(userId: String): Flow<List<Partnership>> {
        return partnershipDao.getPartnershipsForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActivePartnersAsOwner(userId: String): Flow<List<Partnership>> {
        return partnershipDao.getActivePartnersAsOwner(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActivePartnersAsPartner(userId: String): Flow<List<Partnership>> {
        return partnershipDao.getActivePartnersAsPartner(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPartnershipByInviteCode(code: String): Partnership? {
        return partnershipDao.getPartnershipByInviteCode(code)?.toDomain()
    }

    override suspend fun createPartnership(ownerId: String): Result<Partnership> {
        return runCatching {
            val partnership = Partnership(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                partnerId = "",
                inviteCode = generateInviteCode(),
                inviteExpiresAt = System.currentTimeMillis() + INVITE_EXPIRY_MS,
                status = PartnershipStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )
            partnershipDao.insertPartnership(partnership.toEntity())
            partnership
        }
    }

    override suspend fun acceptPartnership(inviteCode: String, partnerId: String): Result<Unit> {
        return runCatching {
            val partnership = partnershipDao.getPartnershipByInviteCode(inviteCode)
                ?: throw IllegalArgumentException("Invalid invite code")

            if (partnership.inviteExpiresAt != null && System.currentTimeMillis() > partnership.inviteExpiresAt) {
                throw IllegalStateException("Invite has expired")
            }

            val updated = partnership.copy(
                partnerId = partnerId,
                status = PartnershipStatus.ACTIVE.name,
                inviteExpiresAt = null
            )
            partnershipDao.updatePartnership(updated)
        }
    }

    override suspend fun updateStatus(partnershipId: String, status: PartnershipStatus): Result<Unit> {
        return runCatching {
            partnershipDao.updateStatus(partnershipId, status.name)
        }
    }

    override suspend fun revokePartnership(partnershipId: String): Result<Unit> {
        return runCatching {
            partnershipDao.updateStatus(partnershipId, PartnershipStatus.REVOKED.name)
        }
    }

    override suspend fun deletePartnership(partnershipId: String): Result<Unit> {
        return runCatching {
            partnershipDao.deletePartnership(partnershipId)
        }
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..8).map { chars.random() }.joinToString("")
    }

    companion object {
        private const val INVITE_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L // 7 days
    }
}
