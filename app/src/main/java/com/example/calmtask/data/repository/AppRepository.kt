package com.example.calmtask.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.calmtask.data.model.Meeting
import com.example.calmtask.data.model.Task
import com.example.calmtask.data.model.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calmtask")

class AppRepository(private val context: Context) {
    private val gson = Gson()

    companion object {
        val KEY_PROFILE = stringPreferencesKey("user_profile")
        val KEY_TASKS = stringPreferencesKey("tasks")
        val KEY_MEETINGS = stringPreferencesKey("meetings")
        val KEY_LAST_MORNING = stringPreferencesKey("lastmorningdate")
        val KEY_VOICE_COMPLETE = booleanPreferencesKey("voice_recording_complete")
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_ELEVEN_LABS_KEY = stringPreferencesKey("eleven_labs_key")
        val KEY_ELEVEN_LABS_VOICE_ID = stringPreferencesKey("eleven_labs_voice_id")
        val KEY_TTS_SPEED = floatPreferencesKey("tts_speed")
        val KEY_TTS_PITCH = floatPreferencesKey("tts_pitch")
        val KEY_MORNING_GREETING = booleanPreferencesKey("morning_greeting_on")
        val KEY_TASK_REMINDERS = booleanPreferencesKey("task_reminders_on")
        val KEY_MEETING_REMINDERS = booleanPreferencesKey("meeting_reminders_on")
        val KEY_EVENING_SUMMARY = booleanPreferencesKey("evening_summary_on")
        val KEY_REMINDER_FREQ_HOURS = intPreferencesKey("reminder_freq_hours")
    }

    // ---------- Profile ----------
    val userProfileFlow: Flow<UserProfile> = context.dataStore.data
        .map { prefs ->
            val json = prefs[KEY_PROFILE] ?: ""
            if (json.isBlank()) UserProfile()
            else gson.fromJson(json, UserProfile::class.java)
        }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PROFILE] = gson.toJson(profile)
        }
    }

    // ---------- Tasks ----------
    val tasksFlow: Flow<List<Task>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[KEY_TASKS] ?: "[]"
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson<List<Task>>(json, type) ?: emptyList()
        }

    private suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TASKS] = gson.toJson(tasks)
        }
    }

    suspend fun addTask(task: Task) {
        val tasks = tasksFlow.first().toMutableList()
        tasks.add(task)
        saveTasks(tasks)
    }

    suspend fun updateTask(task: Task) {
        val tasks = tasksFlow.first().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) tasks[index] = task
        saveTasks(tasks)
    }

    suspend fun deleteTask(taskId: String) {
        val tasks = tasksFlow.first().filter { it.id != taskId }
        saveTasks(tasks)
    }

    suspend fun markTaskDone(taskId: String) {
        val tasks = tasksFlow.first().map { if (it.id == taskId) it.copy(status = "completed") else it }
        saveTasks(tasks)
    }

    suspend fun markTaskLater(taskId: String) {
        val tasks = tasksFlow.first().map { if (it.id == taskId) it.copy(status = "later") else it }
        saveTasks(tasks)
    }

    suspend fun skipTask(taskId: String) {
        val tasks = tasksFlow.first().map { if (it.id == taskId) it.copy(status = "skipped") else it }
        saveTasks(tasks)
    }

    // ---------- Meetings ----------
    val meetingsFlow: Flow<List<Meeting>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[KEY_MEETINGS] ?: "[]"
            val type = object : TypeToken<List<Meeting>>() {}.type
            gson.fromJson<List<Meeting>>(json, type) ?: emptyList()
        }

    private suspend fun saveMeetings(meetings: List<Meeting>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MEETINGS] = gson.toJson(meetings)
        }
    }

    suspend fun addMeeting(meeting: Meeting) {
        val meetings = meetingsFlow.first().toMutableList()
        meetings.add(meeting)
        saveMeetings(meetings)
    }

    suspend fun updateMeeting(meeting: Meeting) {
        val meetings = meetingsFlow.first().toMutableList()
        val index = meetings.indexOfFirst { it.id == meeting.id }
        if (index != -1) meetings[index] = meeting
        saveMeetings(meetings)
    }

    suspend fun deleteMeeting(meetingId: String) {
        val meetings = meetingsFlow.first().filter { it.id != meetingId }
        saveMeetings(meetings)
    }

    // ---------- Morning / State ----------
    suspend fun saveLastMorningDate(date: String) {
        context.dataStore.edit { prefs -> prefs[KEY_LAST_MORNING] = date }
    }

    fun getLastMorningDateSync(): String? {
        return runBlocking {
            context.dataStore.data.map { prefs -> prefs[KEY_LAST_MORNING] }.first()
        }
    }

    suspend fun calculateTodayCompletion(): Float {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val tasks = tasksFlow.first().filter { it.dueDate == today }
        if (tasks.isEmpty()) return 0f
        val completed = tasks.count { it.status == "completed" }
        return completed.toFloat() / tasks.size
    }

    suspend fun calculateStreak(): Int {
        val dates = tasksFlow.first()
            .filter { it.status == "completed" }
            .map { it.dueDate }
            .distinct()
            .sortedDescending()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        var streak = 0
        var checkDate = LocalDate.now()
        if (!dates.contains(today)) {
            checkDate = checkDate.minusDays(1)
        }
        while (dates.contains(checkDate.format(DateTimeFormatter.ISO_LOCAL_DATE))) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    suspend fun getTasksForDate(date: String): List<Task> {
        return tasksFlow.first().filter { it.dueDate == date }
    }

    suspend fun getMeetingsForDate(date: String): List<Meeting> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return meetingsFlow.first().filter {
            LocalDate.ofEpochDay(it.dateTime / 86400000).format(formatter) == date
        }
    }

    suspend fun reRegisterMeetingAlarms(alarmScheduler: com.example.calmtask.util.AlarmScheduler) {
        val profile = userProfileFlow.first()
        if (profile.meetingRemindersOn) {
            meetingsFlow.first().forEach { meeting ->
                alarmScheduler.scheduleMeetingReminders(meeting)
            }
        }
    }

    // ---------- Helper for BroadcastReceiver ----------
    fun getProfileBlocking(): UserProfile {
        return runBlocking {
            val json = context.dataStore.data.map { prefs -> prefs[KEY_PROFILE] }.first() ?: ""
            if (json.isBlank()) UserProfile()
            else gson.fromJson(json, UserProfile::class.java)
        }
    }
}
