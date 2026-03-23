package com.hblsoftware.sporttimer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hblsoftware.sporttimer.features.editprofile.ui.EditProfileAction
import com.hblsoftware.sporttimer.features.editprofile.ui.EditProfileUiState
import com.hblsoftware.sporttimer.ui.session.ActiveSessionBannerUiState
import com.hblsoftware.sporttimer.ui.theme.AppColors
import com.hblsoftware.sporttimer.ui.theme.SportTimerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    uiState: EditProfileUiState = EditProfileUiState(),
    onAction: (EditProfileAction) -> Unit = {},
    activeSessionBanner: ActiveSessionBannerUiState? = null,
    onActiveSessionBannerClick: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        bottomBar = {
            Surface(
                tonalElevation = 0.dp,
                color = AppColors.Background,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { onAction(EditProfileAction.Save) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        contentColor = Color(0xFF03200F)
                    )
                ) {
                    Text(text = "Save Profile", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 8.dp)
        ) {
            item {
                TopBar(
                    onBackClick = onBackClick,
                    onResetClick = { onAction(EditProfileAction.Reset) }
                )
            }
            if (activeSessionBanner != null) {
                item {
                    ActiveSessionBanner(
                        uiState = activeSessionBanner,
                        onClick = onActiveSessionBannerClick
                    )
                }
            }
            item {
                SectionLabel()
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { onAction(EditProfileAction.NameChanged(it)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF163826),
                        unfocusedContainerColor = Color(0xFF163826),
                        disabledContainerColor = Color(0xFF163826),
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = Color(0xFF224737),
                        cursorColor = AppColors.Primary,
                        focusedTextColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary
                    )
                )
            }
            item {
                SectionTitle(title = "Timings")
            }
            item {
                TimingControlCard(
                    title = "Preparation",
                    subtitle = "Get ready before starting",
                    value = uiState.preparationSeconds,
                    unitSuffix = "s",
                    onMinus = { onAction(EditProfileAction.PreparationMinus) },
                    onPlus = { onAction(EditProfileAction.PreparationPlus) },
                    onMinusHold = { onAction(EditProfileAction.PreparationMinus) },
                    onPlusHold = { onAction(EditProfileAction.PreparationPlus) }
                )
            }
            item {
                TimingControlCard(
                    title = "Work Interval",
                    subtitle = "High intensity period",
                    value = uiState.workSeconds,
                    unitSuffix = "s",
                    onMinus = { onAction(EditProfileAction.WorkMinus) },
                    onPlus = { onAction(EditProfileAction.WorkPlus) },
                    onMinusHold = { onAction(EditProfileAction.WorkMinus) },
                    onPlusHold = { onAction(EditProfileAction.WorkPlus) },
                    emphasized = true
                )
            }
            item {
                TimingControlCard(
                    title = "Rest Interval",
                    subtitle = "Recovery period",
                    value = uiState.restSeconds,
                    unitSuffix = "s",
                    onMinus = { onAction(EditProfileAction.RestMinus) },
                    onPlus = { onAction(EditProfileAction.RestPlus) },
                    onMinusHold = { onAction(EditProfileAction.RestMinus) },
                    onPlusHold = { onAction(EditProfileAction.RestPlus) }
                )
            }
            item {
                TimingControlCard(
                    title = "Rounds",
                    subtitle = "Total sets",
                    value = uiState.rounds,
                    unitSuffix = "",
                    onMinus = { onAction(EditProfileAction.RoundsMinus) },
                    onPlus = { onAction(EditProfileAction.RoundsPlus) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Duration: ${formatDuration(uiState.totalSeconds)}",
                        color = AppColors.TextMuted,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit, onResetClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.TextPrimary)
        }
        Text(
            text = "Edit Profile",
            color = AppColors.TextPrimary,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onResetClick) {
            Text(
                text = "Reset",
                color = AppColors.Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SectionLabel() {
    Text(
        text = "Profile Name",
        color = AppColors.TextSecondary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = AppColors.TextPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimingControlCard(
    title: String,
    subtitle: String,
    value: Int,
    unitSuffix: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onMinusHold: (() -> Unit)? = null,
    onPlusHold: (() -> Unit)? = null,
    emphasized: Boolean = false
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF163826), Color(0xFF193E2A))
                )
            )
            .border(
                width = if (emphasized) 2.dp else 1.dp,
                color = if (emphasized) Color(0xFFEFF7F1) else Color(0xFF224737),
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = AppColors.TextSecondary, fontSize = 14.sp)
        }
        QuantityStepper(
            valueText = "$value$unitSuffix",
            emphasized = emphasized,
            onMinus = onMinus,
            onPlus = onPlus,
            onMinusHold = onMinusHold,
            onPlusHold = onPlusHold
        )
    }
}

@Composable
private fun QuantityStepper(
    valueText: String,
    emphasized: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onMinusHold: (() -> Unit)? = null,
    onPlusHold: (() -> Unit)? = null
) {
    val borderColor = Color(0xFF0F2C1C)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF143321))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(label = "-", onClick = onMinus, onHoldStep = onMinusHold)
        Text(
            text = valueText,
            color = if (emphasized) AppColors.Primary else AppColors.TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
        StepperButton(label = "+", onClick = onPlus, onHoldStep = onPlusHold)
    }
}

@Composable
private fun StepperButton(
    label: String,
    onClick: () -> Unit,
    onHoldStep: (() -> Unit)? = null
) {
    val stepperModifier = remember(onClick, onHoldStep) {
        Modifier.pointerInput(onClick, onHoldStep) {
            detectTapGestures(
                onPress = {
                    if (onHoldStep == null) {
                        val released = tryAwaitRelease()
                        if (released) onClick()
                        return@detectTapGestures
                    }

                    coroutineScope {
                        var holdTriggered = false
                        val repeatJob = launch {
                            // Keep regular tap behavior, then accelerate while holding.
                            delay(350)
                            holdTriggered = true
                            var intervalMs = 140L
                            while (isActive) {
                                onHoldStep()
                                delay(intervalMs)
                                intervalMs = maxOf(60L, intervalMs - 10L)
                            }
                        }

                        val released = tryAwaitRelease()
                        repeatJob.cancel()
                        if (released && !holdTriggered) {
                            onClick()
                        }
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A3B28))
            .then(stepperModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = AppColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}m ${seconds}s"
}

@Preview(showBackground = false, widthDp = 411, heightDp = 891)
@Composable
private fun EditProfileScreenPreview() {
    SportTimerTheme(darkTheme = true, dynamicColor = false) {
        EditProfileScreen(
            uiState = EditProfileUiState(name = "Test Name")
        )
    }
}
