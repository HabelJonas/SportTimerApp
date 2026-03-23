package com.hblsoftware.sporttimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hblsoftware.sporttimer.MainActivity
import com.hblsoftware.sporttimer.R
import com.hblsoftware.sporttimer.SportTimerApplication
import com.hblsoftware.sporttimer.data.WorkoutSessionEngine
import com.hblsoftware.sporttimer.data.WorkoutSessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkoutSessionForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val workoutSessionManager by lazy {
        (application as SportTimerApplication).workoutSessionManager
    }

    private var observeJob: Job? = null
    private var lastNotificationSignature: String? = null
    private val playedCountdownSeconds = mutableSetOf<Int>()
    private var lastObservedRemainingSeconds: Int? = null
    private var toneGenerator: ToneGenerator? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                workoutSessionManager.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            ACTION_STOP_FOREGROUND_ONLY -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            else -> {
                startAsForegroundService()
                observeSessionState()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        observeJob?.cancel()
        toneGenerator?.release()
        toneGenerator = null
        super.onDestroy()
    }

    private fun observeSessionState() {
        if (observeJob?.isActive == true) return
        observeJob = serviceScope.launch {
            workoutSessionManager.sessionState.collectLatest { state ->
                when (state) {
                    WorkoutSessionState.Idle -> {
                        lastNotificationSignature = null
                        resetCountdownSoundTracking()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }

                    is WorkoutSessionState.Active -> {
                        notifyIfChanged(state)
                        maybePlayCountdownTone(state)
                    }

                    is WorkoutSessionState.Finished -> {
                        lastNotificationSignature = null
                        resetCountdownSoundTracking()
                        val notificationManager = getSystemService(NotificationManager::class.java)
                        notificationManager.notify(NOTIFICATION_ID, buildNotification(state))
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
        }
    }

    private fun buildNotification(state: WorkoutSessionState) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(notificationTitle(state))
        .setContentText(notificationText(state))
        .setContentIntent(contentPendingIntent(state))
        .setOnlyAlertOnce(true)
        .setOngoing(state is WorkoutSessionState.Active)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun notificationTitle(state: WorkoutSessionState): String = when (state) {
        WorkoutSessionState.Idle -> getString(R.string.notification_workout_idle)
        is WorkoutSessionState.Active -> state.profile.name
        is WorkoutSessionState.Finished -> state.profile.name
    }

    private fun notificationText(state: WorkoutSessionState): String = when (state) {
        WorkoutSessionState.Idle -> getString(R.string.notification_workout_idle)
        is WorkoutSessionState.Active -> {
            val phase = WorkoutSessionEngine.phaseDescription(state.currentEntry.phase)
            val status = if (state.isPaused) getString(R.string.notification_paused) else formatDuration(state.remainingSeconds)
            "$phase • $status"
        }

        is WorkoutSessionState.Finished -> getString(R.string.notification_finished)
    }

    private fun contentPendingIntent(state: WorkoutSessionState): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (state is WorkoutSessionState.Active) {
                putExtra(EXTRA_OPEN_WORKOUT_SESSION, true)
                putExtra(EXTRA_WORKOUT_PROFILE_ID, state.profile.id)
            }
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun startAsForegroundService() {
        val notification = buildNotification(workoutSessionManager.sessionState.value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun notifyIfChanged(state: WorkoutSessionState.Active) {
        val signature = "${state.profile.id}:${state.currentEntry.phase}:${state.remainingSeconds}:${state.isPaused}"
        if (signature == lastNotificationSignature) return

        lastNotificationSignature = signature
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(state))
    }

    private fun maybePlayCountdownTone(state: WorkoutSessionState.Active) {
        if (state.isPaused) return

        val previousRemaining = lastObservedRemainingSeconds
        if (previousRemaining != null && state.remainingSeconds > previousRemaining) {
            // Remaining seconds increasing indicates a phase transition.
            playedCountdownSeconds.clear()
        }

        val seconds = state.remainingSeconds
        if (seconds in 0..3 && playedCountdownSeconds.add(seconds)) {
            val toneType = if (seconds == 0) {
                ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            } else {
                ToneGenerator.TONE_PROP_BEEP
            }
            val generator = toneGenerator ?: ToneGenerator(AudioManager.STREAM_MUSIC, 100).also {
                toneGenerator = it
            }
            generator.startTone(toneType, COUNTDOWN_BEEP_DURATION_MS)
        }

        lastObservedRemainingSeconds = seconds
    }

    private fun resetCountdownSoundTracking() {
        playedCountdownSeconds.clear()
        lastObservedRemainingSeconds = null
    }

    companion object {
        private const val TAG = "WorkoutSessionService"
        private const val CHANNEL_ID = "workout_session_channel"
        private const val NOTIFICATION_ID = 1001
        private const val ACTION_START_SERVICE = "com.hblsoftware.sporttimer.action.START_WORKOUT_SERVICE"
        private const val ACTION_STOP_SERVICE = "com.hblsoftware.sporttimer.action.STOP_WORKOUT_SERVICE"
        private const val ACTION_STOP_FOREGROUND_ONLY = "com.hblsoftware.sporttimer.action.STOP_FOREGROUND_ONLY"
        private const val COUNTDOWN_BEEP_DURATION_MS = 120
        const val EXTRA_OPEN_WORKOUT_SESSION = "com.hblsoftware.sporttimer.extra.OPEN_WORKOUT_SESSION"
        const val EXTRA_WORKOUT_PROFILE_ID = "com.hblsoftware.sporttimer.extra.WORKOUT_PROFILE_ID"

        fun start(context: Context) {
            val intent = Intent(context, WorkoutSessionForegroundService::class.java).apply {
                action = ACTION_START_SERVICE
            }
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (exception: IllegalStateException) {
                Log.w(TAG, "Could not start foreground service from current app state", exception)
            }
        }

        fun stopForegroundService(context: Context) {
            val intent = Intent(context, WorkoutSessionForegroundService::class.java).apply {
                action = ACTION_STOP_FOREGROUND_ONLY
            }
            context.startService(intent)
        }

        private fun formatDuration(totalSeconds: Int): String {
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
    }
}
