package com.hblsoftware.sporttimer.features.profileoverview.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hblsoftware.sporttimer.data.ProfilesRepository
import com.hblsoftware.sporttimer.data.WorkoutSessionManager
import com.hblsoftware.sporttimer.model.WorkoutProfile
import com.hblsoftware.sporttimer.screens.ProfileOverviewScreen
import com.hblsoftware.sporttimer.ui.session.toActiveSessionBannerUiStateOrNull

@Composable
fun ProfileOverviewRoute(
    profilesRepository: ProfilesRepository,
    workoutSessionManager: WorkoutSessionManager,
    onEditProfile: (String?) -> Unit,
    onStartWorkout: (WorkoutProfile) -> Unit,
    onResumeSession: (String) -> Unit,
    profileOverviewViewModel: ProfileOverviewViewModel = viewModel(
        factory = ProfileOverviewViewModelFactory(profilesRepository)
    )
) {
    val uiState by profileOverviewViewModel.uiState.collectAsState()
    val sessionState by workoutSessionManager.sessionState.collectAsState()

    ProfileOverviewScreen(
        uiState = uiState,
        onEditProfile = onEditProfile,
        onStartWorkout = onStartWorkout,
        activeSessionBanner = sessionState.toActiveSessionBannerUiStateOrNull(),
        onActiveSessionBannerClick = onResumeSession
    )
}

