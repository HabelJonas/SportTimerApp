package com.hblsoftware.sporttimer.features.editprofile.ui

import androidx.lifecycle.ViewModel
import com.hblsoftware.sporttimer.model.WorkoutProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var isInitialized = false

    fun initialize(profile: WorkoutProfile?) {
        if (isInitialized) return
        isInitialized = true
        if (profile != null) {
            _uiState.value = EditProfileUiState(
                profileId = profile.id,
                name = profile.name,
                preparationSeconds = profile.preparationSeconds,
                workSeconds = profile.workSeconds,
                restSeconds = profile.restSeconds,
                rounds = profile.rounds
            )
        }
    }

    fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.NameChanged -> _uiState.update {
                it.copy(name = action.value, errorMessage = null)
            }

            EditProfileAction.PreparationMinus -> _uiState.update {
                it.copy(preparationSeconds = maxOf(0, it.preparationSeconds - 1))
            }

            EditProfileAction.PreparationPlus -> _uiState.update {
                it.copy(preparationSeconds = it.preparationSeconds + 1)
            }

            EditProfileAction.WorkMinus -> _uiState.update {
                it.copy(workSeconds = maxOf(1, it.workSeconds - 1))
            }

            EditProfileAction.WorkPlus -> _uiState.update {
                it.copy(workSeconds = it.workSeconds + 1)
            }

            EditProfileAction.RestMinus -> _uiState.update {
                it.copy(restSeconds = maxOf(0, it.restSeconds - 1))
            }

            EditProfileAction.RestPlus -> _uiState.update {
                it.copy(restSeconds = it.restSeconds + 1)
            }

            EditProfileAction.RoundsMinus -> _uiState.update {
                it.copy(rounds = maxOf(1, it.rounds - 1))
            }

            EditProfileAction.RoundsPlus -> _uiState.update {
                it.copy(rounds = it.rounds + 1)
            }

            EditProfileAction.Reset -> {
                val profileId = _uiState.value.profileId
                _uiState.value = EditProfileUiState(profileId = profileId)
            }

            EditProfileAction.Save -> {
                val state = _uiState.value
                if (state.name.isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Profile name is required") }
                } else {
                    _uiState.update { it.copy(saveRequested = true, errorMessage = null) }
                }
            }

            EditProfileAction.SaveHandled -> _uiState.update { it.copy(saveRequested = false) }
            EditProfileAction.ErrorHandled -> _uiState.update { it.copy(errorMessage = null) }
        }
    }
}
