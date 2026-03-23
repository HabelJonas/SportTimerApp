package com.hblsoftware.sporttimer

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hblsoftware.sporttimer.features.editprofile.ui.EditProfileRoute
import com.hblsoftware.sporttimer.features.profileoverview.ui.ProfileOverviewRoute
import com.hblsoftware.sporttimer.features.workoutsession.ui.WorkoutSessionRoute
import com.hblsoftware.sporttimer.model.WorkoutProfile
import com.hblsoftware.sporttimer.service.WorkoutSessionForegroundService

class MainActivity : ComponentActivity() {
    private var pendingNavigationRoute by mutableStateOf<String?>(null)
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sportTimerApplication = application as SportTimerApplication
        val profilesRepository = sportTimerApplication.profilesRepository
        val workoutSessionManager = sportTimerApplication.workoutSessionManager
        pendingNavigationRoute = routeFromIntent(intent)

        setContent {
            val navController = rememberNavController()
            val resumeSession: (String) -> Unit = { sessionProfileId ->
                navController.navigate("workout_session/$sessionProfileId") {
                    launchSingleTop = true
                }
            }

            LaunchedEffect(pendingNavigationRoute) {
                pendingNavigationRoute?.let { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                    pendingNavigationRoute = null
                }
            }

            NavHost(navController = navController, startDestination = "profile_overview") {
                composable("profile_overview") {
                    ProfileOverviewRoute(
                        profilesRepository = profilesRepository,
                        workoutSessionManager = workoutSessionManager,
                        onEditProfile = { profileId: String? ->
                            val route = if (profileId != null) {
                                "edit_profile/$profileId"
                            } else {
                                "edit_profile/new"
                            }
                            navController.navigate(route = route)
                        },
                        onStartWorkout = { profile ->
                            ensureNotificationPermissionIfNeeded()
                            workoutSessionManager.start(profile)
                            navController.navigate("workout_session/${profile.id}")
                        },
                        onResumeSession = resumeSession
                    )
                }
                composable("edit_profile/{profileId}") { backStackEntry ->
                    val profileId = backStackEntry.arguments?.getString("profileId")
                    val initialProfile = profileId?.let { profilesRepository.findById(it) }
                    EditProfileRoute(
                        profileId = profileId,
                        initialProfile = initialProfile,
                        workoutSessionManager = workoutSessionManager,
                        onBackClick = { navController.popBackStack() },
                        onResumeSession = resumeSession,
                        onSaveClick = { id, name, prep, work, rest, rounds ->
                            val profile = if (id != null) {
                                WorkoutProfile(
                                    id = id,
                                    name = name,
                                    preparationSeconds = prep,
                                    workSeconds = work,
                                    restSeconds = rest,
                                    rounds = rounds
                                )
                            } else {
                                WorkoutProfile(
                                    name = name,
                                    preparationSeconds = prep,
                                    workSeconds = work,
                                    restSeconds = rest,
                                    rounds = rounds
                                )
                            }
                            profilesRepository.upsert(profile)
                        }
                    )
                }
                composable("edit_profile/new") {
                    EditProfileRoute(
                        profileId = null,
                        initialProfile = null,
                        workoutSessionManager = workoutSessionManager,
                        onBackClick = { navController.popBackStack() },
                        onResumeSession = resumeSession,
                        onSaveClick = { id, name, prep, work, rest, rounds ->
                            val profile = if (id != null) {
                                WorkoutProfile(
                                    id = id,
                                    name = name,
                                    preparationSeconds = prep,
                                    workSeconds = work,
                                    restSeconds = rest,
                                    rounds = rounds
                                )
                            } else {
                                WorkoutProfile(
                                    name = name,
                                    preparationSeconds = prep,
                                    workSeconds = work,
                                    restSeconds = rest,
                                    rounds = rounds
                                )
                            }
                            profilesRepository.upsert(profile)
                        }
                    )
                }
                composable("workout_session/{profileId}") { backStackEntry ->
                    val profileId = backStackEntry.arguments?.getString("profileId").orEmpty()
                    WorkoutSessionRoute(
                        profileId = profileId,
                        workoutSessionManager = workoutSessionManager,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingNavigationRoute = routeFromIntent(intent)
    }

    private fun routeFromIntent(intent: Intent?): String? {
        if (intent?.getBooleanExtra(WorkoutSessionForegroundService.EXTRA_OPEN_WORKOUT_SESSION, false) != true) {
            return null
        }
        val profileId = intent.getStringExtra(WorkoutSessionForegroundService.EXTRA_WORKOUT_PROFILE_ID)
            ?: return null
        return "workout_session/$profileId"
    }

    private fun ensureNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) return

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

}
