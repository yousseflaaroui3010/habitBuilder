package com.habitarchitect.presentation.screen.auth

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.model.AuthProvider
import com.habitarchitect.domain.model.User
import com.habitarchitect.domain.repository.UserRepository
import com.habitarchitect.service.auth.GoogleAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignInUiState {
    object Idle : SignInUiState()
    object Loading : SignInUiState()
    data class SignInIntent(val intentSender: IntentSender) : SignInUiState()
    object Success : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}

/**
 * ViewModel for sign in screen.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val googleAuthService: GoogleAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading

            val intentSender = googleAuthService.beginSignIn()
            if (intentSender != null) {
                _uiState.value = SignInUiState.SignInIntent(intentSender)
            } else {
                _uiState.value = SignInUiState.Error("Could not start Google Sign-In. Please try again.")
            }
        }
    }

    fun handleSignInResult(intent: Intent) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading

            val firebaseUser = googleAuthService.handleSignInResult(intent)
            if (firebaseUser != null) {
                // Create or update user in local database
                val now = System.currentTimeMillis()
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    authProvider = AuthProvider.GOOGLE,
                    createdAt = now,
                    lastActiveAt = now,
                    onboardingCompleted = true,
                    notificationsEnabled = true
                )

                userRepository.saveUser(user)
                _uiState.value = SignInUiState.Success
            } else {
                _uiState.value = SignInUiState.Error("Sign-in failed. Please try again.")
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading

            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password)
                authResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            val firebaseUser = firebaseAuth.currentUser
                            if (firebaseUser != null) {
                                val now = System.currentTimeMillis()
                                val user = User(
                                    id = firebaseUser.uid,
                                    email = firebaseUser.email ?: "",
                                    displayName = firebaseUser.displayName,
                                    photoUrl = firebaseUser.photoUrl?.toString(),
                                    authProvider = AuthProvider.EMAIL,
                                    createdAt = now,
                                    lastActiveAt = now,
                                    onboardingCompleted = true,
                                    notificationsEnabled = true
                                )
                                userRepository.saveUser(user)
                                _uiState.value = SignInUiState.Success
                            }
                        }
                    } else {
                        _uiState.value = SignInUiState.Error(
                            task.exception?.message ?: "Sign-in failed"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error(e.message ?: "Sign-in failed")
            }
        }
    }

    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading

            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
                authResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            val firebaseUser = firebaseAuth.currentUser
                            if (firebaseUser != null) {
                                val now = System.currentTimeMillis()
                                val user = User(
                                    id = firebaseUser.uid,
                                    email = firebaseUser.email ?: "",
                                    displayName = null,
                                    photoUrl = null,
                                    authProvider = AuthProvider.EMAIL,
                                    createdAt = now,
                                    lastActiveAt = now,
                                    onboardingCompleted = true,
                                    notificationsEnabled = true
                                )
                                userRepository.saveUser(user)
                                _uiState.value = SignInUiState.Success
                            }
                        }
                    } else {
                        _uiState.value = SignInUiState.Error(
                            task.exception?.message ?: "Account creation failed"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error(e.message ?: "Account creation failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = SignInUiState.Idle
    }
}
