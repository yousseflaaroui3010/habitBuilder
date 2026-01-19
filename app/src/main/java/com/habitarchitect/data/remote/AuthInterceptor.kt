package com.habitarchitect.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that adds Firebase auth token to requests.
 * Automatically refreshes expired tokens.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for certain endpoints if needed
        if (originalRequest.url.encodedPath.contains("public")) {
            return chain.proceed(originalRequest)
        }

        val token = getAuthToken()

        return if (token != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            Timber.w("No auth token available, proceeding without auth")
            chain.proceed(originalRequest)
        }
    }

    /**
     * Get Firebase ID token, refreshing if necessary.
     */
    private fun getAuthToken(): String? {
        val user = firebaseAuth.currentUser ?: return null

        return try {
            runBlocking {
                val result = user.getIdToken(false).await()
                result.token
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get auth token")
            null
        }
    }
}
