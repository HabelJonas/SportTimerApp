package com.hblsoftware.sporttimer.features.workoutsession.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hblsoftware.sporttimer.data.WorkoutSessionEngine
import com.hblsoftware.sporttimer.data.WorkoutSessionManager
import com.hblsoftware.sporttimer.data.WorkoutSessionState
import com.hblsoftware.sporttimer.model.WorkoutPhase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WorkoutSessionViewModel(
    private val profileId: String,
    private val workoutSessionManager: WorkoutSessionManager
) : ViewModel() {

    val uiState: StateFlow<WorkoutSessionUiState> = workoutSessionManager.sessionState
        .map { state -> state.toUiState(profileId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WorkoutSessionUiState(profileId = profileId)
        )

    fun onAction(action: WorkoutSessionAction) {
        when (action) {
            WorkoutSessionAction.TogglePause -> workoutSessionManager.togglePause()
            WorkoutSessionAction.SkipToNextPhase -> workoutSessionManager.skipToNextPhase()
            WorkoutSessionAction.Stop -> workoutSessionManager.stop()
        }
    }

    class Factory(
        private val profileId: String,
        private val workoutSessionManager: WorkoutSessionManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WorkoutSessionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WorkoutSessionViewModel(profileId, workoutSessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

private fun WorkoutSessionState.toUiState(requestedProfileId: String): WorkoutSessionUiState = when (this) {
    WorkoutSessionState.Idle -> WorkoutSessionUiState(
        profileId = requestedProfileId,
        closeRequested = true
    )

    is WorkoutSessionState.Active -> {
        val currentEntry = currentEntry
        val nextEntry = nextEntry
        WorkoutSessionUiState(
            profileId = profile.id,
            profileName = profile.name,
            currentRound = currentEntry.round,
            totalRounds = profile.rounds,
            phaseLabel = WorkoutSessionEngine.phaseLabel(currentEntry.phase),
            phaseDescription = WorkoutSessionEngine.phaseDescription(currentEntry.phase),
            nextPhaseLabel = nextEntry?.let { WorkoutSessionEngine.phaseDescription(it.phase) } ?: "Finish",
            nextPhaseTime = nextEntry?.durationSeconds?.let(::formatDuration) ?: "00:00",
            remainingTime = formatDuration(remainingSeconds),
            progress = progress,
            currentPhase = currentEntry.phase,
            isPaused = isPaused,
            isFinished = false,
            closeRequested = false
        )
    }

    is WorkoutSessionState.Finished -> WorkoutSessionUiState(
        profileId = profile.id,
        profileName = profile.name,
        currentRound = profile.rounds,
        totalRounds = profile.rounds,
        phaseLabel = WorkoutSessionEngine.phaseLabel(WorkoutPhase.FINISHED),
        phaseDescription = WorkoutSessionEngine.phaseDescription(WorkoutPhase.FINISHED),
        nextPhaseLabel = "All rounds complete",
        nextPhaseTime = "00:00",
        remainingTime = "00:00",
        progress = 1f,
        currentPhase = WorkoutPhase.FINISHED,
        isPaused = false,
        isFinished = true,
        closeRequested = false
    )
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

