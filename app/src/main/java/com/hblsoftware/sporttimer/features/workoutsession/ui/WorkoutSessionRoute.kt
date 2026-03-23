package com.hblsoftware.sporttimer.features.workoutsession.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hblsoftware.sporttimer.data.WorkoutSessionManager
import com.hblsoftware.sporttimer.screens.WorkoutSessionScreen

@Composable
fun WorkoutSessionRoute(
    profileId: String,
    workoutSessionManager: WorkoutSessionManager,
    onBackClick: () -> Unit,
    workoutSessionViewModel: WorkoutSessionViewModel = viewModel(
        key = profileId,
        factory = WorkoutSessionViewModel.Factory(
            profileId = profileId,
            workoutSessionManager = workoutSessionManager
        )
    )
) {
    val uiState by workoutSessionViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.closeRequested) {
        if (uiState.closeRequested) {
            onBackClick()
        }
    }

    WorkoutSessionScreen(
        uiState = uiState,
        onAction = workoutSessionViewModel::onAction,
        onBackClick = onBackClick
    )
}

