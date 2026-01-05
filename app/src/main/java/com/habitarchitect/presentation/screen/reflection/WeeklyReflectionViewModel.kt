package com.habitarchitect.presentation.screen.reflection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeeklyReflectionUiState(
    val wentWell: String = "",
    val didntGoWell: String = "",
    val learned: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class WeeklyReflectionViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReflectionUiState())
    val uiState: StateFlow<WeeklyReflectionUiState> = _uiState.asStateFlow()

    fun updateWentWell(value: String) {
        _uiState.value = _uiState.value.copy(wentWell = value)
    }

    fun updateDidntGoWell(value: String) {
        _uiState.value = _uiState.value.copy(didntGoWell = value)
    }

    fun updateLearned(value: String) {
        _uiState.value = _uiState.value.copy(learned = value)
    }

    fun saveReflection() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            // TODO: Save to database/repository
            // For now, just mark as saved
            kotlinx.coroutines.delay(500)

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }
}
