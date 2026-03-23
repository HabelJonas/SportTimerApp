package com.hblsoftware.sporttimer.features.profileoverview.ui

import com.hblsoftware.sporttimer.model.WorkoutProfile

data class ProfileOverviewUiState(
    val profiles: List<WorkoutProfile> = emptyList()
)

