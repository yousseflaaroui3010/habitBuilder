package com.habitarchitect.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitarchitect.data.local.database.entity.PartnershipEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for partnership operations.
 */
@Dao
interface PartnershipDao {

    @Query("SELECT * FROM partnerships WHERE ownerId = :userId OR partnerId = :userId")
    fun getPartnershipsForUser(userId: String): Flow<List<PartnershipEntity>>

    @Query("SELECT * FROM partnerships WHERE ownerId = :userId AND status = 'ACTIVE'")
    fun getActivePartnersAsOwner(userId: String): Flow<List<PartnershipEntity>>

    @Query("SELECT * FROM partnerships WHERE partnerId = :userId AND status = 'ACTIVE'")
    fun getActivePartnersAsPartner(userId: String): Flow<List<PartnershipEntity>>

    @Query("SELECT * FROM partnerships WHERE inviteCode = :code LIMIT 1")
    suspend fun getPartnershipByInviteCode(code: String): PartnershipEntity?

    @Query("SELECT * FROM partnerships WHERE id = :id LIMIT 1")
    suspend fun getPartnershipById(id: String): PartnershipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartnership(partnership: PartnershipEntity)

    @Update
    suspend fun updatePartnership(partnership: PartnershipEntity)

    @Query("UPDATE partnerships SET status = :status WHERE id = :partnershipId")
    suspend fun updateStatus(partnershipId: String, status: String)

    @Query("DELETE FROM partnerships WHERE id = :partnershipId")
    suspend fun deletePartnership(partnershipId: String)

    @Query("DELETE FROM partnerships WHERE ownerId = :userId OR partnerId = :userId")
    suspend fun deletePartnershipsForUser(userId: String)
}
