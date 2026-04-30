package com.example.calmtask.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calmtask.data.model.UserProfile
import com.example.calmtask.ui.components.SectionCard
import com.example.calmtask.ui.theme.*

@Composable
fun SettingsScreen(
    profile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onReRecordVoice: () -> Unit,
    onTestVoice: () -> Unit,
    onDeleteVoice: () -> Unit,
    onTestElevenLabs: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        SectionCard(title = "Profile") {
            OutlinedTextField(
                value = profile.name,
                onValueChange = { onUpdateProfile(profile.copy(name = it)) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Wake time: ${String.format("%02d:%02d", profile.wakeTimeHour, profile.wakeTimeMinute)}",
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionCard(title = "Voice") {
            Button(onClick = onTestVoice) { Text("Test my voice") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onReRecordVoice) { Text("Re-record voice") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDeleteVoice) { Text("Delete local voice recordings") }
            Spacer(modifier = Modifier.height(12.dp))
            Text("TTS Speed", color = Color.White)
            Slider(
                value = profile.ttsSpeed,
                onValueChange = { onUpdateProfile(profile.copy(ttsSpeed = it)) },
                valueRange = 0.5f..2f
            )
            Text("TTS Pitch", color = Color.White)
            Slider(
                value = profile.ttsPitch,
                onValueChange = { onUpdateProfile(profile.copy(ttsPitch = it)) },
                valueRange = 0.5f..2f
            )
            OutlinedTextField(
                value = profile.elevenLabsApiKey,
                onValueChange = { onUpdateProfile(profile.copy(elevenLabsApiKey = it)) },
                label = { Text("ElevenLabs API Key") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            OutlinedTextField(
                value = profile.elevenLabsVoiceId,
                onValueChange = { onUpdateProfile(profile.copy(elevenLabsVoiceId = it)) },
                label = { Text("Voice ID") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Button(onClick = {
                onTestElevenLabs(profile.elevenLabsApiKey, profile.elevenLabsVoiceId)
            }) { Text("Test ElevenLabs") }
            Text(
                "Local recordings stay on this phone. ElevenLabs is only used when you add your own API key.",
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionCard(title = "Notifications") {
            SwitchRow("Morning greeting", profile.morningGreetingOn) {
                onUpdateProfile(profile.copy(morningGreetingOn = it))
            }
            SwitchRow("Task reminders", profile.taskRemindersOn) {
                onUpdateProfile(profile.copy(taskRemindersOn = it))
            }
            SwitchRow("Meeting reminders", profile.meetingRemindersOn) {
                onUpdateProfile(profile.copy(meetingRemindersOn = it))
            }
            SwitchRow("Evening summary", profile.eveningSummaryOn) {
                onUpdateProfile(profile.copy(eveningSummaryOn = it))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionCard(title = "Appearance") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("#4F7DF3", "#FF6B6B", "#11998E", "#834D9B", "#F7971E", "#D04ED6").forEach { hex ->
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(AndroidColor.parseColor(hex)))
                            .clickable { onUpdateProfile(profile.copy(accentColor = hex)) }
                    )
                }
            }
        }
    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Text(label, color = Color.White)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}
