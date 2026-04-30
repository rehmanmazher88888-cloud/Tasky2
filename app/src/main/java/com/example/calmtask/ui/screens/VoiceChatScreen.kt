package com.example.calmtask.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.calmtask.data.model.Task
import com.example.calmtask.data.model.UserProfile
import com.example.calmtask.ui.components.MicButton
import com.example.calmtask.ui.theme.*
import com.example.calmtask.util.SpeechRecognizerManager
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VoiceChatScreen(
    profile: UserProfile,
    tasks: List<Task>,
    onCommand: (String) -> Unit
) {
    val context = LocalContext.current
    var transcript by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Tap to talk") }
    var isListening by remember { mutableStateOf(false) }
    val speechManager = remember { SpeechRecognizerManager(context) }
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            permissionGranted = true
        }
    }

    LaunchedEffect(isListening) {
        if (isListening) {
            speechManager.startListening()
        }
    }

    LaunchedEffect(speechManager) {
        speechManager.results.collectLatest { result ->
            isListening = false
            speechManager.destroy()
            when {
                result == "__error__" -> response =
                    "I missed that — try again when you're ready."
                result.isBlank() -> response = "I missed that."
                else -> {
                    transcript = result
                    onCommand(result)
                    response = "Got it!"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Voice Chat",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))
        MicButton(
            isListening = isListening,
            onClick = {
                if (!permissionGranted) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    return@MicButton
                }
                isListening = !isListening
                if (!isListening) speechManager.destroy()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (isListening) "Listening..." else response,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (transcript.isNotEmpty()) {
            Text(
                "You: $transcript",
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
