package com.example.calmtask

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmtask.data.repository.AppRepository
import com.example.calmtask.ui.components.GradientButton
import com.example.calmtask.ui.theme.AccentDefault
import com.example.calmtask.ui.theme.CalmTaskTheme
import com.example.calmtask.ui.theme.TextSecondary
import com.example.calmtask.util.SpeechEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MorningGreetingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalmTaskTheme {
                MorningGreetingContent()
            }
        }
    }
}

@Composable
fun MorningGreetingContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { AppRepository(context) }
    val profile = repo.getProfileBlocking()
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
    var taskSummary by remember { mutableStateOf("Loading...") }
    var tasksCount by remember { mutableIntStateOf(0) }
    var firstTask by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val tasks = repo.tasksFlow.first()
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val todayTasks = tasks.filter { task -> task.dueDate == todayStr }
        tasksCount = todayTasks.size
        firstTask = todayTasks.firstOrNull()?.title ?: "none"
        taskSummary = "Today is $today. You have $tasksCount tasks. Your first task is $firstTask."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D0F1A), Color(0xFF1A1A3E)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🌅", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Good morning", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 32.sp)
            if (profile.name.isNotBlank())
                Text(profile.name, color = Color.White, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(taskSummary, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
            GradientButton(
                text = "Let's Start",
                gradient = listOf(AccentDefault, Color(0xFF7B9FFF)),
                onClick = {
                    val voice1 = File(context.filesDir, "voices/voice1.wav")
                    if (voice1.exists()) {
                        MediaPlayer().apply {
                            setDataSource(voice1.absolutePath)
                            prepare()
                            start()
                        }
                    } else {
                        scope.launch {
                            val tts = SpeechEngine(context)
                            tts.init(profile.languageCode, profile.ttsSpeed, profile.ttsPitch)
                            tts.speak("Good morning. Today is going to be a great day.")
                        }
                    }
                    scope.launch {
                        val tts = SpeechEngine(context)
                        tts.init(profile.languageCode, profile.ttsSpeed, profile.ttsPitch)
                        tts.speak(taskSummary)
                    }
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = {
                Toast.makeText(context, "I'll remind you in 30 minutes.", Toast.LENGTH_SHORT).show()
                (context as? ComponentActivity)?.finish()
            }) {
                Text("Remind me in 30 min", color = Color.White)
            }
        }
    }
}
