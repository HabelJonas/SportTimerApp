package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.model.WorkoutPhase
import com.hblsoftware.sporttimer.model.WorkoutProfile

data class WorkoutTimelineEntry(
    val phase: WorkoutPhase,
    val round: Int,
    val durationSeconds: Int
)

object WorkoutSessionEngine {
    fun buildTimeline(profile: WorkoutProfile): List<WorkoutTimelineEntry> {
        val entries = buildList {
            if (profile.preparationSeconds > 0) {
                add(
                    WorkoutTimelineEntry(
                        phase = WorkoutPhase.PREPARATION,
                        round = 1,
                        durationSeconds = profile.preparationSeconds
                    )
                )
            }

            for (round in 1..profile.rounds) {
                if (profile.workSeconds > 0) {
                    add(
                        WorkoutTimelineEntry(
                            phase = WorkoutPhase.WORK,
                            round = round,
                            durationSeconds = profile.workSeconds
                        )
                    )
                }
                if (profile.restSeconds > 0) {
                    add(
                        WorkoutTimelineEntry(
                            phase = WorkoutPhase.REST,
                            round = round,
                            durationSeconds = profile.restSeconds
                        )
                    )
                }
            }
        }

        return if (entries.isNotEmpty()) {
            entries
        } else {
            listOf(
                WorkoutTimelineEntry(
                    phase = WorkoutPhase.WORK,
                    round = 1,
                    durationSeconds = 1
                )
            )
        }
    }

    fun phaseLabel(phase: WorkoutPhase): String = when (phase) {
        WorkoutPhase.PREPARATION -> "PREP"
        WorkoutPhase.WORK -> "WORK"
        WorkoutPhase.REST -> "REST"
        WorkoutPhase.FINISHED -> "DONE"
    }

    fun phaseDescription(phase: WorkoutPhase): String = when (phase) {
        WorkoutPhase.PREPARATION -> "Preparation"
        WorkoutPhase.WORK -> "Work Interval"
        WorkoutPhase.REST -> "Rest Period"
        WorkoutPhase.FINISHED -> "Workout Complete"
    }
}

