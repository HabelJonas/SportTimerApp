package com.hblsoftware.sporttimer.data

import com.hblsoftware.sporttimer.model.WorkoutProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.ceil

sealed interface WorkoutSessionState {
    data object Idle : WorkoutSessionState

    data class Active(
        val profile: WorkoutProfile,
        val currentEntry: WorkoutTimelineEntry,
        val nextEntry: WorkoutTimelineEntry?,
        val remainingSeconds: Int,
        val phaseDurationSeconds: Int,
        val progress: Float,
        val isPaused: Boolean
    ) : WorkoutSessionState

    data class Finished(
        val profile: WorkoutProfile
    ) : WorkoutSessionState
}

class WorkoutSessionManager(
    private val nowProvider: () -> Long = android.os.SystemClock::elapsedRealtime,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    tickMillis: Long = 200L
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val tickerDelayMillis = tickMillis.coerceAtLeast(1L)
    private val _sessionState = MutableStateFlow<WorkoutSessionState>(WorkoutSessionState.Idle)
    val sessionState: StateFlow<WorkoutSessionState> = _sessionState.asStateFlow()

    private var tickerJob: Job? = null
    private var activeSession: ActiveSession? = null

    fun start(profile: WorkoutProfile) {
        val timeline = WorkoutSessionEngine.buildTimeline(profile)
        val firstEntry = timeline.first()
        activeSession = ActiveSession(
            profile = profile,
            timeline = timeline,
            entryIndex = 0,
            phaseStartElapsedRealtime = nowProvider(),
            remainingMillisWhenRunning = firstEntry.durationSeconds * 1_000L,
            isPaused = false
        )
        publishState()
        ensureTicker()
    }

    fun togglePause() {
        val session = activeSession ?: return
        val now = nowProvider()
        activeSession = if (session.isPaused) {
            session.copy(
                isPaused = false,
                phaseStartElapsedRealtime = now
            )
        } else {
            session.copy(
                isPaused = true,
                remainingMillisWhenRunning = remainingMillis(session, now)
            )
        }
        publishState()
    }

    fun skipToNextPhase() {
        if (activeSession == null) return
        advanceToNextPhase()
    }

    fun stop() {
        activeSession = null
        tickerJob?.cancel()
        tickerJob = null
        _sessionState.value = WorkoutSessionState.Idle
    }

    private fun ensureTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = scope.launch {
            while (isActive) {
                if (activeSession == null) {
                    _sessionState.value = WorkoutSessionState.Idle
                    break
                }

                val session = activeSession ?: break
                if (!session.isPaused && remainingMillis(session, nowProvider()) <= 0L) {
                    advanceToNextPhase()
                } else {
                    publishState()
                }
                delay(tickerDelayMillis)
            }
        }
    }

    private fun advanceToNextPhase() {
        val session = activeSession ?: return
        val nextIndex = session.entryIndex + 1
        if (nextIndex >= session.timeline.size) {
            activeSession = null
            tickerJob?.cancel()
            tickerJob = null
            _sessionState.value = WorkoutSessionState.Finished(profile = session.profile)
            return
        }

        val nextEntry = session.timeline[nextIndex]
        activeSession = session.copy(
            entryIndex = nextIndex,
            phaseStartElapsedRealtime = nowProvider(),
            remainingMillisWhenRunning = nextEntry.durationSeconds * 1_000L,
            isPaused = false
        )
        publishState()
    }

    private fun publishState() {
        val session = activeSession ?: return
        val currentEntry = session.timeline[session.entryIndex]
        val remainingMillis = remainingMillis(session, nowProvider())
        val remainingSeconds = ceil(remainingMillis / 1_000.0).toInt().coerceAtLeast(0)
        val phaseDurationSeconds = currentEntry.durationSeconds.coerceAtLeast(1)
        val elapsedSeconds = (phaseDurationSeconds - remainingMillis / 1_000f).coerceAtLeast(0f)
        val progress = (elapsedSeconds / phaseDurationSeconds).coerceIn(0f, 1f)

        _sessionState.value = WorkoutSessionState.Active(
            profile = session.profile,
            currentEntry = currentEntry,
            nextEntry = session.timeline.getOrNull(session.entryIndex + 1),
            remainingSeconds = remainingSeconds,
            phaseDurationSeconds = phaseDurationSeconds,
            progress = progress,
            isPaused = session.isPaused
        )
    }

    private fun remainingMillis(session: ActiveSession, now: Long): Long {
        return if (session.isPaused) {
            session.remainingMillisWhenRunning
        } else {
            (session.remainingMillisWhenRunning - (now - session.phaseStartElapsedRealtime)).coerceAtLeast(0L)
        }
    }

    private data class ActiveSession(
        val profile: WorkoutProfile,
        val timeline: List<WorkoutTimelineEntry>,
        val entryIndex: Int,
        val phaseStartElapsedRealtime: Long,
        val remainingMillisWhenRunning: Long,
        val isPaused: Boolean
    )
}

