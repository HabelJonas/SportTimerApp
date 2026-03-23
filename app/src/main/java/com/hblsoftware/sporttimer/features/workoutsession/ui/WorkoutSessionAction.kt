package com.hblsoftware.sporttimer.features.workoutsession.ui

sealed interface WorkoutSessionAction {
    data object TogglePause : WorkoutSessionAction
    data object SkipToNextPhase : WorkoutSessionAction
    data object Stop : WorkoutSessionAction
}

