package com.habitarchitect.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitarchitect.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NeedsOnboarding : SplashUiState()
    object NeedsSignIn : SplashUiState()
    object Authenticated : SplashUiState()
}

/**
 * ViewModel for splash screen logic.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                _uiState.value = SplashUiState.NeedsSignIn
                return@launch
            }

            val localUser = userRepository.getUserById(firebaseUser.uid).first()
            if (localUser == null) {
                _uiState.value = SplashUiState.NeedsOnboarding
            } else if (!localUser.onboardingCompleted) {
                _uiState.value = SplashUiState.NeedsOnboarding
            } else {
                userRepository.updateLastActive(firebaseUser.uid)
                _uiState.value = SplashUiState.Authenticated
            }
        }
    }
}
