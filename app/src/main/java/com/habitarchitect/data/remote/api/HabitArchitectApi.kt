package com.habitarchitect.data.remote.api

import com.habitarchitect.data.remote.dto.AnalyticsBatchRequest
import com.habitarchitect.data.remote.dto.AnalyticsBatchResponse
import com.habitarchitect.data.remote.dto.HabitSyncRequest
import com.habitarchitect.data.remote.dto.HabitSyncResponse
import com.habitarchitect.data.remote.dto.PartnerInviteRequest
import com.habitarchitect.data.remote.dto.PartnerInviteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for HabitArchitect backend.
 * Defines all API endpoints for data synchronization.
 * Auth is handled by AuthInterceptor.
 */
interface HabitArchitectApi {

    // ==================== Analytics ====================

    @POST("api/v1/analytics/batch")
    suspend fun uploadAnalyticsBatch(
        @Body request: AnalyticsBatchRequest
    ): Response<AnalyticsBatchResponse>

    // ==================== Habits Sync ====================

    @POST("api/v1/habits/sync")
    suspend fun syncHabits(
        @Body request: HabitSyncRequest
    ): Response<HabitSyncResponse>

    @GET("api/v1/habits/{userId}")
    suspend fun getHabits(
        @Path("userId") userId: String
    ): Response<HabitSyncResponse>

    // ==================== Partnerships ====================

    @POST("api/v1/partnerships/invite")
    suspend fun createPartnerInvite(
        @Body request: PartnerInviteRequest
    ): Response<PartnerInviteResponse>

    @POST("api/v1/partnerships/accept/{code}")
    suspend fun acceptPartnerInvite(
        @Path("code") inviteCode: String
    ): Response<PartnerInviteResponse>

    @GET("api/v1/partnerships")
    suspend fun getPartnerships(): Response<List<PartnerInviteResponse>>

    companion object {
        const val BASE_URL = "https://api.habitarchitect.app/"
    }
}
