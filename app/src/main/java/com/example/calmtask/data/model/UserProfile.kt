package com.example.calmtask.data.model

data class UserProfile(
    val name: String = "",
    val gender: String = "",
    val country: String = "",
    val language: String = "English",
    val languageCode: String = "en",
    val wakeTimeHour: Int = 5,
    val wakeTimeMinute: Int = 0,
    val accentColor: String = "#4F7DF3",
    val darkMode: Boolean = true,
    val morningGreetingOn: Boolean = true,
    val taskRemindersOn: Boolean = true,
    val meetingRemindersOn: Boolean = true,
    val eveningSummaryOn: Boolean = true,
    val reminderFrequencyHours: Int = 4,
    val onboardingComplete: Boolean = false,
    val voiceRecordingComplete: Boolean = false,
    val elevenLabsApiKey: String = "",
    val elevenLabsVoiceId: String = "",
    val ttsSpeed: Float = 1.0f,
    val ttsPitch: Float = 1.0f
)
