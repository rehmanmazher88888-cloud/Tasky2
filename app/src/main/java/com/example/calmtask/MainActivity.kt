package com.example.calmtask

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.calmtask.data.model.UserProfile
import com.example.calmtask.service.CalmTaskForegroundService
import com.example.calmtask.ui.components.BottomNavBar
import com.example.calmtask.ui.screens.*
import com.example.calmtask.ui.theme.CalmTaskTheme
import com.example.calmtask.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CalmTaskForegroundService.start(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        setContent {
            CalmTaskTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val profile by viewModel.profile.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val meetings by viewModel.meetings.collectAsState()
    val context = LocalContext.current

    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val todayTasks = remember(tasks) { tasks.filter { it.dueDate == today } }
    val completionPercent = remember(todayTasks) {
        if (todayTasks.isEmpty()) 0f
        else todayTasks.count { it.status == "completed" }.toFloat() / todayTasks.size
    }
    var streak by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { streak = viewModel.computeStreak() }

    if (profile.onboardingComplete) {
        Scaffold(
            bottomBar = { BottomNavBar(currentRoute, onItemClick = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }) }
        ) { innerPadding ->
            NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
                composable("home") {
                    HomeScreen(
                        tasks = tasks, profile = profile,
                        onMarkDone = { viewModel.markTaskDone(it.id) },
                        onMarkLater = { viewModel.markTaskLater(it.id) },
                        onSkip = { viewModel.skipTask(it.id) },
                        onAddTask = { /* show add task dialog */ },
                        completionPercent = completionPercent,
                        streak = streak
                    )
                }
                composable("calendar") {
                    CalendarScreen(
                        tasks = tasks, meetings = meetings,
                        onDateSelected = { /* handle */ },
                        selectedDate = LocalDate.now(),
                        onAddTask = { /* */ },
                        onAddMeeting = { /* */ },
                        onDeleteTask = { viewModel.deleteTask(it.id) },
                        onDeleteMeeting = { viewModel.deleteMeeting(it.id) }
                    )
                }
                composable("achievements") {
                    AchievementsScreen(
                        averageCompletion = completionPercent,
                        streak = streak,
                        weeklyData = listOf(0.8f, 0.5f, 0.9f, 0.6f, 0.7f, 0.3f, 1.0f),
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "I'm building calmer mornings with CalmTask Voice — ${(completionPercent*100).toInt()}% complete this week.")
                            }
                            context.startActivity(shareIntent)
                        }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        profile = profile,
                        onUpdateProfile = { viewModel.updateProfile(it) },
                        onReRecordVoice = { /* open voice recording */ },
                        onTestVoice = { /* play all recordings */ },
                        onDeleteVoice = { /* delete files */ },
                        onTestElevenLabs = { apiKey, voiceId -> /* test API */ }
                    )
                }
                composable("voice_chat") {
                    VoiceChatScreen(profile = profile, tasks = todayTasks, onCommand = { cmd ->
                        when {
                            cmd.contains("done") || cmd.contains("mark done") -> viewModel.markTaskDone(todayTasks.firstOrNull()?.id ?: "")
                            cmd.contains("skip") -> viewModel.skipTask(todayTasks.firstOrNull()?.id ?: "")
                            cmd.contains("later") -> viewModel.markTaskLater(todayTasks.firstOrNull()?.id ?: "")
                            else -> Toast.makeText(context, "Command: $cmd", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    } else {
        OnboardingScreen(
            profile = profile,
            onComplete = { updatedProfile ->
                viewModel.completeOnboarding(updatedProfile)
            }
        )
    }
}
