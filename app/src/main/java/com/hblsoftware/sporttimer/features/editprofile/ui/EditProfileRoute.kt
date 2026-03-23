package com.hblsoftware.sporttimer.features.editprofile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hblsoftware.sporttimer.data.WorkoutSessionManager
import com.hblsoftware.sporttimer.model.WorkoutProfile
import com.hblsoftware.sporttimer.screens.EditProfileScreen
import com.hblsoftware.sporttimer.ui.session.toActiveSessionBannerUiStateOrNull

@Composable
fun EditProfileRoute(
    profileId: String?,
    initialProfile: WorkoutProfile?,
    workoutSessionManager: WorkoutSessionManager,
    onBackClick: () -> Unit,
    onResumeSession: (String) -> Unit,
    onSaveClick: (
        profileId: String?,
        name: String,
        prep: Int,
        work: Int,
        rest: Int,
        rounds: Int
    ) -> Unit = { _, _, _, _, _, _ -> },
    editProfileViewModel: EditProfileViewModel = viewModel()
) {
    val uiState by editProfileViewModel.uiState.collectAsState()
    val sessionState by workoutSessionManager.sessionState.collectAsState()

    LaunchedEffect(initialProfile?.id) {
        editProfileViewModel.initialize(initialProfile)
    }

    LaunchedEffect(uiState.saveRequested) {
        if (uiState.saveRequested) {
            onSaveClick(
                profileId,
                uiState.name,
                uiState.preparationSeconds,
                uiState.workSeconds,
                uiState.restSeconds,
                uiState.rounds
            )
            editProfileViewModel.onAction(EditProfileAction.SaveHandled)
            onBackClick()
        }
    }

    EditProfileScreen(
        onBackClick = onBackClick,
        uiState = uiState,
        onAction = editProfileViewModel::onAction,
        activeSessionBanner = sessionState.toActiveSessionBannerUiStateOrNull(),
        onActiveSessionBannerClick = onResumeSession
    )
}
