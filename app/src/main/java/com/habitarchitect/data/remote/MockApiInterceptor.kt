package com.habitarchitect.data.remote

import com.google.gson.Gson
import com.habitarchitect.BuildConfig
import com.habitarchitect.data.remote.dto.AnalyticsBatchResponse
import com.habitarchitect.data.remote.dto.HabitSyncResponse
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock API interceptor for development/testing when no backend is available.
 * Returns successful responses for all API calls, simulating a working backend.
 * Only active in DEBUG builds.
 */
@Singleton
class MockApiInterceptor @Inject constructor(
    private val gson: Gson
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Only mock in debug builds
        if (!BuildConfig.DEBUG) {
            return chain.proceed(request)
        }

        Timber.d("MockApi: ${request.method} $path")

        // Simulate network delay
        Thread.sleep(200)

        val responseBody = when {
            path.contains("/analytics/batch") -> mockAnalyticsBatchResponse()
            path.contains("/habits/sync") -> mockHabitSyncResponse()
            path.contains("/habits/") -> mockHabitSyncResponse()
            path.contains("/partnerships") -> mockPartnershipsResponse()
            else -> {
                // Pass through for unknown endpoints
                return chain.proceed(request)
            }
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseBody.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun mockAnalyticsBatchResponse(): String {
        val response = AnalyticsBatchResponse(
            success = true,
            processedCount = 1,
            failedEventIds = null
        )
        return gson.toJson(response)
    }

    private fun mockHabitSyncResponse(): String {
        val response = HabitSyncResponse(
            habits = emptyList(),
            dailyLogs = emptyList(),
            syncTimestamp = System.currentTimeMillis(),
            conflicts = null
        )
        return gson.toJson(response)
    }

    private fun mockPartnershipsResponse(): String {
        return "[]"
    }
}
