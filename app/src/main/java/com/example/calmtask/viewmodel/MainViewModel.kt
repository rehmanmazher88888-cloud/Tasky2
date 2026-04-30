package com.example.calmtask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmtask.data.model.Meeting
import com.example.calmtask.data.model.Task
import com.example.calmtask.data.model.UserProfile
import com.example.calmtask.data.repository.AppRepository
import com.example.calmtask.util.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AppRepository(application)
    private val alarmScheduler = AlarmScheduler(application)

    val profile: StateFlow<UserProfile> = repo.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val tasks: StateFlow<List<Task>> = repo.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val meetings: StateFlow<List<Meeting>> = repo.meetingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch { repo.saveProfile(profile) }
    }

    fun addTask(task: Task) {
        viewModelScope.launch { repo.addTask(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repo.updateTask(task) }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch { repo.deleteTask(taskId) }
    }

    fun markTaskDone(taskId: String) {
        viewModelScope.launch { repo.markTaskDone(taskId) }
    }

    fun markTaskLater(taskId: String) {
        viewModelScope.launch { repo.markTaskLater(taskId) }
    }

    fun skipTask(taskId: String) {
        viewModelScope.launch { repo.skipTask(taskId) }
    }

    fun addMeeting(meeting: Meeting) {
        viewModelScope.launch {
            repo.addMeeting(meeting)
            if (profile.value.meetingRemindersOn) {
                alarmScheduler.scheduleMeetingReminders(meeting)
            }
        }
    }

    fun updateMeeting(meeting: Meeting) {
        viewModelScope.launch {
            repo.updateMeeting(meeting)
            // Re-schedule alarms (cancel old via alarmScheduler.cancelMeetingAlarms)
            alarmScheduler.cancelMeetingAlarms(meeting.id)
            if (profile.value.meetingRemindersOn) {
                alarmScheduler.scheduleMeetingReminders(meeting)
            }
        }
    }

    fun deleteMeeting(meetingId: String) {
        viewModelScope.launch {
            repo.deleteMeeting(meetingId)
            alarmScheduler.cancelMeetingAlarms(meetingId)
        }
    }

    fun completeOnboarding(profile: UserProfile) {
        viewModelScope.launch {
            repo.saveProfile(profile.copy(onboardingComplete = true))
        }
    }

    fun saveLastMorningDate(date: String) {
        viewModelScope.launch { repo.saveLastMorningDate(date) }
    }

    fun reRegisterAlarms() {
        viewModelScope.launch { repo.reRegisterMeetingAlarms(alarmScheduler) }
    }

    suspend fun getTasksForDate(date: String): List<Task> = repo.getTasksForDate(date)
    suspend fun getMeetingsForDate(date: String): List<Meeting> = repo.getMeetingsForDate(date)
    suspend fun computeStreak(): Int = repo.calculateStreak()
    suspend fun computeTodayCompletion(): Float = repo.calculateTodayCompletion()
}
