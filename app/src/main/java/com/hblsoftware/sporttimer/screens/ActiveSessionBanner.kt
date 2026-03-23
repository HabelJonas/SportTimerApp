package com.hblsoftware.sporttimer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hblsoftware.sporttimer.ui.session.ActiveSessionBannerUiState
import com.hblsoftware.sporttimer.ui.theme.AppColors

@Composable
fun ActiveSessionBanner(
    uiState: ActiveSessionBannerUiState,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A3A29))
            .clickable { onClick(uiState.profileId) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Session running: ${uiState.profileName}",
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = buildStatusText(uiState),
                color = AppColors.TextSecondary,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = uiState.remainingTime,
            color = AppColors.Primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Open workout session",
            tint = AppColors.Primary
        )
    }
}

private fun buildStatusText(uiState: ActiveSessionBannerUiState): String {
    val status = if (uiState.isPaused) "Paused" else "Running"
    return "${uiState.phaseDescription} - $status"
}

