package com.hblsoftware.sporttimer.ui.session

import com.hblsoftware.sporttimer.data.WorkoutSessionEngine
import com.hblsoftware.sporttimer.data.WorkoutSessionState

data class ActiveSessionBannerUiState(
    val profileId: String,
    val profileName: String,
    val phaseDescription: String,
    val remainingTime: String,
    val isPaused: Boolean
)

fun WorkoutSessionState.toActiveSessionBannerUiStateOrNull(): ActiveSessionBannerUiState? = when (this) {
    WorkoutSessionState.Idle -> null
    is WorkoutSessionState.Finished -> null
    is WorkoutSessionState.Active -> ActiveSessionBannerUiState(
        profileId = profile.id,
        profileName = profile.name,
        phaseDescription = WorkoutSessionEngine.phaseDescription(currentEntry.phase),
        remainingTime = formatDuration(remainingSeconds),
        isPaused = isPaused
    )
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

