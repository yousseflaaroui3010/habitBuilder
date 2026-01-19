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
import com.habitarchitect.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    /**
     * Initiates the Google Sign-In flow.
     * @return IntentSender to launch the sign-in UI, or null if failed.
     */
    suspend fun beginSignIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            Timber.e(e, "Failed to begin Google Sign-In")
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
                Timber.d("User signed in: ${authResult.user?.uid}")
                authResult.user
            } else {
                Timber.w("Google ID token is null")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to handle sign-in result")
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
            Timber.d("User signed out")
        } catch (e: Exception) {
            Timber.e(e, "Error during sign out")
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
