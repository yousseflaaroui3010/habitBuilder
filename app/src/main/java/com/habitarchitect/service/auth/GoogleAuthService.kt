package com.habitarchitect.service.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.habitarchitect.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service handling Google Sign-In authentication via Firebase.
 */
@Singleton
class GoogleAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getWebClientId())
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    private fun getWebClientId(): String {
        // This is the Web Client ID from google-services.json (client_type: 3)
        return "1052528331486-2e5jk1eepu21p4oqj08vevuicn73f50l.apps.googleusercontent.com"
    }

    /**
     * Initiates the Google Sign-In flow.
     * @return IntentSender to launch the sign-in UI, or null if failed.
     */
    suspend fun beginSignIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Handles the result from the Google Sign-In intent.
     * @param intent The result intent from the sign-in UI.
     * @return FirebaseUser if successful, null otherwise.
     */
    suspend fun handleSignInResult(intent: Intent): FirebaseUser? {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                authResult.user
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            firebaseAuth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            firebaseAuth.signOut()
        }
    }

    /**
     * Gets the currently signed-in user.
     */
    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    /**
     * Checks if a user is currently signed in.
     */
    fun isSignedIn(): Boolean = firebaseAuth.currentUser != null
}
