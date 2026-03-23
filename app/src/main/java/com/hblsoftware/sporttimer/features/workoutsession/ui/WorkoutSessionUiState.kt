package com.hblsoftware.sporttimer.features.workoutsession.ui

import com.hblsoftware.sporttimer.model.WorkoutPhase

data class WorkoutSessionUiState(
    val profileId: String? = null,
    val profileName: String = "Workout",
    val currentRound: Int = 0,
    val totalRounds: Int = 0,
    val phaseLabel: String = "PREP",
    val phaseDescription: String = "Preparation",
    val nextPhaseLabel: String = "Work Interval",
    val nextPhaseTime: String = "00:00",
    val remainingTime: String = "00:00",
    val progress: Float = 0f,
    val currentPhase: WorkoutPhase = WorkoutPhase.PREPARATION,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val closeRequested: Boolean = false
) {
    val roundLabel: String
        get() = "ROUND ${currentRound.coerceAtLeast(1)} / ${totalRounds.coerceAtLeast(1)}"
}

