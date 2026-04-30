package com.example.calmtask.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.calmtask.data.model.UserProfile
import com.example.calmtask.ui.components.GradientButton
import com.example.calmtask.ui.components.VoiceWaveform
import com.example.calmtask.ui.theme.*
import com.example.calmtask.util.AudioRecorder
import com.example.calmtask.util.CountryLanguageData
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun OnboardingScreen(onComplete: (UserProfile) -> Unit, profile: UserProfile) {
    var page by remember { mutableIntStateOf(0) }
    var localProfile by remember { mutableStateOf(profile) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            page = 6 // voice recording page
        } else {
            Toast.makeText(context, "Microphone permission needed for voice recording.", Toast.LENGTH_SHORT).show()
        }
    }

    Crossfade(targetState = page) { currentPage ->
        when (currentPage) {
            0 -> SplashPage(onNext = { page = 1 }, onSkip = {
                onComplete(localProfile.copy(onboardingComplete = true))
            })
            1 -> LanguagePage(
                selected = localProfile.language,
                onSelect = { lang, code ->
                    localProfile = localProfile.copy(language = lang, languageCode = code)
                    page = 2
                })
            2 -> CountryPage(
                selected = localProfile.country,
                onSelect = { country ->
                    localProfile = localProfile.copy(country = country)
                    page = 3
                })
            3 -> GenderPage(
                selected = localProfile.gender,
                onSelect = { gender ->
                    localProfile = localProfile.copy(gender = gender)
                    page = 4
                })
            4 -> NamePage(
                name = localProfile.name,
                onNameChange = { localProfile = localProfile.copy(name = it) },
                onNext = { page = 5 }
            )
            5 -> WakeTimePage(
                hour = localProfile.wakeTimeHour,
                minute = localProfile.wakeTimeMinute,
                onTimeSet = { h, m ->
                    localProfile = localProfile.copy(wakeTimeHour = h, wakeTimeMinute = m)
                    // Check audio permission before proceeding to voice recording
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        page = 6
                    }
                }
            )
            6 -> VoiceRecordingPage(
                onVoiceComplete = {
                    localProfile = localProfile.copy(voiceRecordingComplete = true)
                    page = 7
                },
                onBack = { page = 5 }
            )
            7 -> DonePage(
                name = localProfile.name,
                onOpenApp = {
                    onComplete(localProfile.copy(onboardingComplete = true))
                }
            )
        }
    }
}

@Composable
fun SplashPage(onNext: () -> Unit, onSkip: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BackgroundDark, Color(0xFF1A1A3E)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CalmTask Voice", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your day, your voice", color = TextSecondary, fontSize = 18.sp)
            Text("A calmer way to meet the morning.", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(48.dp))
            GradientButton(text = "Begin", gradient = listOf(AccentDefault, Color(0xFF7B9FFF)), onClick = onNext)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onSkip) { Text("Skip for now", color = Color.White) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePage(selected: String, onSelect: (String, String) -> Unit) {
    var search by remember { mutableStateOf("") }
    val languages = CountryLanguageData.languages
    val filtered = languages.filter { it.first.contains(search, ignoreCase = true) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Text("Choose your language", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("I’ll speak to you in the language that feels most natural.", color = TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search...") },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(filtered) { (name, code) ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(name, code) }.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name, color = Color.White, fontSize = 16.sp)
                    if (name == selected) Icon(Icons.Default.Check, "selected", tint = AccentDefault)
                }
            }
        }
    }
}

@Composable
fun CountryPage(selected: String, onSelect: (String) -> Unit) {
    var search by remember { mutableStateOf("") }
    val countries = CountryLanguageData.countries
    val filtered = countries.filter { it.first.contains(search, ignoreCase = true) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Text("Where are you waking up?", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("This helps dates, time, and reminders feel right.", color = TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search country...") },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(filtered) { (name, code) ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(name) }.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name, color = Color.White, fontSize = 16.sp)
                    if (name == selected) Icon(Icons.Default.Check, "selected", tint = AccentDefault)
                }
            }
        }
    }
}

@Composable
fun GenderPage(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Woman", "Man", "Non-binary", "Prefer not to say")
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(), verticalArrangement = Arrangement.Center) {
        Text("How do you identify?", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("Only used to make the app feel more personal.", color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))
        options.forEach { option ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onSelect(option) },
                colors = CardDefaults.cardColors(if (selected == option) CardDark.copy(alpha = 0.8f) else CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(option, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun NamePage(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(), verticalArrangement = Arrangement.Center) {
        Text("What should I call you?", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("This is just for your morning greeting.", color = TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Your name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault)
        ) {
            Text("That’s me", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeTimePage(hour: Int, minute: Int, onTimeSet: (Int, Int) -> Unit) {
    var selectedHour by remember { mutableIntStateOf(hour) }
    var selectedMinute by remember { mutableIntStateOf(minute) }
    val timeText = String.format("%02d:%02d", selectedHour, selectedMinute)
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(), verticalArrangement = Arrangement.Center) {
        Text("When do you usually wake up?", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("I’ll wait until after this time before greeting you.", color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))
        // Simple time picker using selectors
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            NumberPicker(value = selectedHour, range = 0..23, onValueChange = { selectedHour = it })
            Text(":", fontSize = 32.sp, color = Color.White)
            NumberPicker(value = selectedMinute, range = 0..59, onValueChange = { selectedMinute = it })
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onTimeSet(selectedHour, selectedMinute) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentDefault)
        ) {
            Text("Set my morning", color = Color.White)
        }
    }
}

@Composable
fun NumberPicker(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("▲", color = AccentDefault, modifier = Modifier.clickable { if (value < range.last) onValueChange(value + 1) })
        Text(value.toString().padStart(2, '0'), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("▼", color = AccentDefault, modifier = Modifier.clickable { if (value > range.first) onValueChange(value - 1) })
    }
}

@Composable
fun VoiceRecordingPage(onVoiceComplete: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val sentences = listOf(
        "Good morning. Today is going to be a great day.",
        "You have tasks to complete. Let’s get started.",
        "Well done. You completed your task today.",
        "You have a meeting coming up soon. Please prepare.",
        "Good night. Sleep well and rest for tomorrow."
    )
    var currentIndex by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var amplitude by remember { mutableFloatStateOf(0f) }
    var playbackState by remember { mutableStateOf("") }
    val recorder = remember { AudioRecorder() }
    val voicesDir = remember { File(context.filesDir, "voices").apply { mkdirs() } }
    var recordedSentences by remember { mutableStateOf(mutableSetOf<Int>()) }

    LaunchedEffect(Unit) {
        // If all 5 files exist, skip to done
        val allExist = (0..4).all { File(voicesDir, "voice${it+1}.wav").exists() }
        if (allExist) onVoiceComplete()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Now for the magic ✨", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("I’m going to learn your voice...", color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        // Sentence card
        Text(
            sentences[currentIndex],
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        VoiceWaveform(amplitude = amplitude, modifier = Modifier.fillMaxWidth().height(60.dp))
        Spacer(modifier = Modifier.height(24.dp))
        // Mic button
        Box(contentAlignment = Alignment.Center) {
            if (!isRecording && !playbackState.startsWith("play")) {
                IconButton(onClick = {
                    if (currentIndex in recordedSentences) return@IconButton
                    isRecording = true
                    val file = File(voicesDir, "voice${currentIndex + 1}.wav")
                    recorder.startRecording(file) { amp ->
                        amplitude = amp
                    }
                }, modifier = Modifier.size(80.dp)) {
                    Text("🎤", fontSize = 40.sp)
                }
            } else if (isRecording) {
                IconButton(onClick = {
                    recorder.stopRecording()
                    isRecording = false
                    recordedSentences.add(currentIndex)
                    // Playback
                    val file = File(voicesDir, "voice${currentIndex + 1}.wav")
                    val player = MediaPlayer().apply {
                        setDataSource(file.absolutePath)
                        prepare()
                        start()
                        setOnCompletionListener {
                            playbackState = "done"
                        }
                    }
                    playbackState = "play"
                }, modifier = Modifier.size(80.dp)) {
                    Text("⏹️", fontSize = 40.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Progress dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0..4) {
                val color = when {
                    recordedSentences.contains(i) -> Color.Green
                    i == currentIndex && isRecording -> AccentDefault
                    else -> Color.Gray
                }
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (playbackState == "done") {
            Text("Happy with that?", color = Color.White, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Button(onClick = {
                    playbackState = ""
                    recordedSentences.remove(currentIndex)
                }) { Text("Re-record") }
                Button(onClick = {
                    playbackState = ""
                    val encouragements = listOf(
                        "Nice. Your morning voice is taking shape.",
                        "That sounded natural.",
                        "Future-you is going to like this.",
                        "Almost there.",
                        "Perfect. That’s your voice saved. 🎉"
                    )
                    Toast.makeText(context, encouragements[currentIndex], Toast.LENGTH_SHORT).show()
                    if (currentIndex == 4) {
                        onVoiceComplete()
                    } else {
                        currentIndex++
                    }
                }) { Text("Looks good 👍") }
            }
        }
        Text("Your recordings stay on this phone unless you choose to use ElevenLabs.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun DonePage(name: String, onOpenApp: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("You’re all set, $name!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("See you tomorrow morning 🌅", color = TextSecondary)
            Spacer(modifier = Modifier.height(32.dp))
            GradientButton(
                text = "Open CalmTask",
                gradient = listOf(AccentDefault, Color(0xFF7B9FFF)),
                onClick = onOpenApp
            )
        }
    }
}
