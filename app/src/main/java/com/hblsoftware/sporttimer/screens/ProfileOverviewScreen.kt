package com.hblsoftware.sporttimer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hblsoftware.sporttimer.R
import com.hblsoftware.sporttimer.features.profileoverview.ui.ProfileOverviewUiState
import com.hblsoftware.sporttimer.model.WorkoutProfile
import com.hblsoftware.sporttimer.ui.session.ActiveSessionBannerUiState
import com.hblsoftware.sporttimer.ui.theme.AppColors

@Composable
fun ProfileOverviewScreen(
    uiState: ProfileOverviewUiState,
    onEditProfile: (String?) -> Unit,
    onStartWorkout: (WorkoutProfile) -> Unit,
    activeSessionBanner: ActiveSessionBannerUiState? = null,
    onActiveSessionBannerClick: (String) -> Unit = {}
) {
    val background = AppColors.Background
    Scaffold(containerColor = background, floatingActionButton = {
        FloatingActionButton(
            onClick = { onEditProfile(null) },
            containerColor = AppColors.Primary,
            contentColor = AppColors.Background,
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add profile"
            )
        }
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { OverviewTopBar() }
            if (activeSessionBanner != null) {
                item {
                    ActiveSessionBanner(
                        uiState = activeSessionBanner,
                        onClick = onActiveSessionBannerClick
                    )
                }
            }
            item { StatsRow(totalProfiles = uiState.profiles.size) }
            item { SavedProfilesHeader() }

            if (uiState.profiles.isEmpty()) {
                item {
                    Text(
                        text = "No profiles yet. Tap + to create your first profile.",
                        color = Color(0xFF8DAE98),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }

            items(uiState.profiles, key = { it.id }) { profile ->
                val accentColor = accentForProfile(profile)
                ProfileCard(
                    accentColor = accentColor,
                    name = profile.name,
                    duration = formatDuration(profile.totalSeconds),
                    rounds = profile.rounds.toString(),
                    workRest = "${profile.workSeconds}s / ${profile.restSeconds}s",
                    barSegments = listOf(
                        Color(0xFFF2C94C),
                        AppColors.Primary,
                        Color(0xFF627083)
                    ),
                    barWeights = profileBarWeights(profile),
                    onEditProfile = { onEditProfile(profile.id) },
                    onStartWorkout = { onStartWorkout(profile) }
                )
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }
}

@Composable
private fun OverviewTopBar() {
    val appName = stringResource(R.string.app_name)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = appName,
            color = Color(0xFFEFF7F1),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp)
        )
    }
}

@Composable
private fun StatsRow(totalProfiles: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "TOTAL PROFILES",
            value = totalProfiles.toString(),
            unit = "profiles",
            highlight = AppColors.Primary
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    title: String,
    value: String,
    unit: String,
    highlight: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF173524)),
        border = BorderStroke(1.dp, Color(0xFF234B34))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = highlight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = Color(0xFFF3FAF5),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    color = Color(0xFFB6CBBF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SavedProfilesHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Saved Profiles",
            color = Color(0xFFF1F6F2),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileCard(
    accentColor: Color,
    name: String,
    duration: String,
    rounds: String,
    workRest: String,
    barSegments: List<Color>,
    barWeights: List<Float>,
    onEditProfile: () -> Unit,
    onStartWorkout: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3626)),
        border = BorderStroke(1.dp, Color(0xFF264633))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxSize()
                    .background(accentColor)
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        color = Color(0xFFF1F6F2),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onEditProfile,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF234233), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF8FC0A2),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onStartWorkout,
                        modifier = Modifier
                            .size(40.dp)
                            .background(accentColor, RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start",
                            tint = AppColors.Background,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Spacer(modifier = Modifier.height(14.dp))
                SegmentedBar(segments = barSegments, weights = barWeights)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatLine(label = "DURATION", value = duration, modifier = Modifier.weight(1f))
                    VerticalDivider(
                        color = Color(0xFF2A4A39),
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                    )
                    StatLine(label = "ROUNDS", value = rounds, modifier = Modifier.weight(1f))
                    VerticalDivider(
                        color = Color(0xFF2A4A39),
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                    )
                    StatLine(label = "WORK/REST", value = workRest, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SegmentedBar(segments: List<Color>, weights: List<Float>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF2A3E32)),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        segments.zip(weights).forEach { (color, weight) ->
            Box(
                modifier = Modifier
                    .weight(weight)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun StatLine(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            text = label,
            color = Color(0xFF7E9B89),
            fontSize = 11.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color(0xFFE8F4EC),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun profileBarWeights(profile: WorkoutProfile): List<Float> {
    val preparation = profile.preparationSeconds.toFloat().coerceAtLeast(1f)
    val work = (profile.workSeconds * profile.rounds).toFloat().coerceAtLeast(1f)
    val rest = (profile.restSeconds * profile.rounds).toFloat().coerceAtLeast(1f)
    val total = preparation + work + rest
    return listOf(preparation / total, work / total, rest / total)
}

private fun accentForProfile(profile: WorkoutProfile): Color =
    if (profile.workSeconds >= profile.restSeconds) AppColors.Primary else Color(0xFFFF8A00)
