package com.habitarchitect.domain.repository

import com.habitarchitect.domain.model.Partnership
import com.habitarchitect.domain.model.PartnershipStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for partnership operations.
 */
interface PartnershipRepository {

    fun getPartnershipsForUser(userId: String): Flow<List<Partnership>>

    fun getActivePartnersAsOwner(userId: String): Flow<List<Partnership>>

    fun getActivePartnersAsPartner(userId: String): Flow<List<Partnership>>

    suspend fun getPartnershipByInviteCode(code: String): Partnership?

    suspend fun createPartnership(ownerId: String): Result<Partnership>

    suspend fun acceptPartnership(inviteCode: String, partnerId: String): Result<Unit>

    suspend fun updateStatus(partnershipId: String, status: PartnershipStatus): Result<Unit>

    suspend fun revokePartnership(partnershipId: String): Result<Unit>

    suspend fun deletePartnership(partnershipId: String): Result<Unit>
}
