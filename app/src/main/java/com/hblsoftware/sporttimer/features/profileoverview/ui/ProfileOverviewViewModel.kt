package com.hblsoftware.sporttimer.features.profileoverview.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hblsoftware.sporttimer.data.ProfilesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileOverviewViewModel(
    profilesRepository: ProfilesRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileOverviewUiState> = profilesRepository.profiles
        .map { profiles -> ProfileOverviewUiState(profiles = profiles) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileOverviewUiState()
        )
}

class ProfileOverviewViewModelFactory(
    private val profilesRepository: ProfilesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileOverviewViewModel(profilesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

