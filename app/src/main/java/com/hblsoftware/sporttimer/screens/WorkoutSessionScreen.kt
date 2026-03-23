package com.hblsoftware.sporttimer.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hblsoftware.sporttimer.features.workoutsession.ui.WorkoutSessionAction
import com.hblsoftware.sporttimer.features.workoutsession.ui.WorkoutSessionUiState
import com.hblsoftware.sporttimer.model.WorkoutPhase
import com.hblsoftware.sporttimer.ui.theme.AppColors
import com.hblsoftware.sporttimer.ui.theme.MaterialIconsPause
import com.hblsoftware.sporttimer.ui.theme.MaterialIconsSkip_next
import com.hblsoftware.sporttimer.ui.theme.MaterialIconsStop
import com.hblsoftware.sporttimer.ui.theme.SportTimerTheme

@Composable
fun WorkoutSessionScreen(
    uiState: WorkoutSessionUiState,
    onAction: (WorkoutSessionAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phaseColor = phaseColor(uiState.currentPhase)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF031E12), Color(0xFF061A12), Color(0xFF03140D))
                )
            )
            .padding(horizontal = 22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(profileName = uiState.profileName, onBackClick = onBackClick)
            Spacer(modifier = Modifier.height(22.dp))
            RoundPill(text = uiState.roundLabel, accentColor = phaseColor)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (uiState.isPaused) "Paused • ${uiState.phaseDescription}" else uiState.phaseDescription,
                color = AppColors.TextSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(30.dp))
            ProgressRing(
                timeRemaining = uiState.remainingTime,
                phaseLabel = uiState.phaseLabel,
                progress = uiState.progress,
                phaseColor = phaseColor,
                isPaused = uiState.isPaused
            )
            Spacer(modifier = Modifier.height(30.dp))
            UpNextCard(
                title = if (uiState.isFinished) "WORKOUT COMPLETE" else "UP NEXT",
                name = uiState.nextPhaseLabel,
                time = uiState.nextPhaseTime,
                accentColor = phaseColor
            )
            Spacer(modifier = Modifier.height(30.dp))
            ControlsRow(
                isPaused = uiState.isPaused,
                isFinished = uiState.isFinished,
                onSkipClick = { onAction(WorkoutSessionAction.SkipToNextPhase) },
                onPauseClick = { onAction(WorkoutSessionAction.TogglePause) },
                onStopClick = { onAction(WorkoutSessionAction.Stop) }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TopBar(profileName: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = "X",
                color = AppColors.TextValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = profileName,
            color = AppColors.TextHighlight,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 56.dp)
        )
    }
}

@Composable
private fun RoundPill(text: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accentColor.copy(alpha = 0.18f))
            .padding(horizontal = 28.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = accentColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ProgressRing(
    timeRemaining: String,
    phaseLabel: String,
    progress: Float,
    phaseColor: Color,
    isPaused: Boolean
) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(360.dp)) {
            val ringThickness = 24.dp.toPx()
            drawCircle(
                color = AppColors.Surface.copy(alpha = 0.75f),
                radius = size.minDimension / 2.8f
            )
            drawArc(
                color = AppColors.BorderAlt,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = ringThickness, cap = StrokeCap.Round)
            )
            drawArc(
                color = phaseColor,
                startAngle = -90f,
                sweepAngle = progress.coerceIn(0f, 1f) * 360f,
                useCenter = false,
                style = Stroke(width = ringThickness, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeRemaining,
                color = AppColors.TextHighlight,
                fontSize = 72.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 72.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = phaseLabel,
                color = phaseColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            if (isPaused) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "PAUSED",
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
private fun UpNextCard(title: String, name: String, time: String, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = AppColors.TextMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = name,
                    color = AppColors.TextHighlight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = time,
                color = accentColor,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = MaterialIconsSkip_next,
                contentDescription = "Next phase",
                tint = AppColors.IconMuted,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ControlsRow(
    isPaused: Boolean,
    isFinished: Boolean,
    onSkipClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            onClick = onSkipClick,
            enabled = !isFinished,
            icon = {
                Icon(
                    imageVector = MaterialIconsSkip_next,
                    contentDescription = "Skip",
                    tint = AppColors.TextValue,
                    modifier = Modifier.size(34.dp)
                )
            }
        )

        Box(
            modifier = Modifier
                .size(122.dp)
                .clip(CircleShape)
                .background(if (isFinished) AppColors.BorderAlt else AppColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onPauseClick,
                enabled = !isFinished,
                modifier = Modifier.size(90.dp)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Filled.PlayArrow else MaterialIconsPause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = if (isFinished) AppColors.TextSecondary else AppColors.Background,
                    modifier = Modifier.size(58.dp)
                )
            }
        }

        CircleIconButton(
            onClick = onStopClick,
            enabled = true,
            icon = {
                Icon(
                    imageVector = MaterialIconsStop,
                    contentDescription = "Stop",
                    tint = AppColors.TextValue,
                    modifier = Modifier.size(30.dp)
                )
            }
        )
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(94.dp)
            .clip(CircleShape)
            .background(AppColors.Surface.copy(alpha = if (enabled) 0.9f else 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(72.dp)) {
            icon()
        }
    }
}

private fun phaseColor(phase: WorkoutPhase): Color = when (phase) {
    WorkoutPhase.PREPARATION -> AppColors.AccentYellow
    WorkoutPhase.WORK -> AppColors.Primary
    WorkoutPhase.REST -> AppColors.AccentOrange
    WorkoutPhase.FINISHED -> AppColors.Primary
}

@Preview(showBackground = false, widthDp = 411, heightDp = 891)
@Composable
private fun WorkoutSessionScreenPreview() {
    SportTimerTheme(darkTheme = true, dynamicColor = false) {
        WorkoutSessionScreen(
            uiState = WorkoutSessionUiState(
                profileName = "HIIT Cardio Blast",
                currentRound = 3,
                totalRounds = 8,
                phaseLabel = "WORK",
                phaseDescription = "Work Interval",
                nextPhaseLabel = "Rest Period",
                nextPhaseTime = "00:15",
                remainingTime = "00:45",
                progress = 0.78f,
                currentPhase = WorkoutPhase.WORK
            ),
            onAction = {},
            onBackClick = {}
        )
    }
}
