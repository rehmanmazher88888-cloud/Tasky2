package com.example.calmtask.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.calmtask.data.repository.AppRepository
import com.example.calmtask.ui.screens.MorningGreetingActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_USER_PRESENT) {
            val repo = AppRepository(context)
            val profile = repo.getProfileBlocking()
            if (!profile.onboardingComplete || !profile.morningGreetingOn) return
            val now = LocalTime.now()
            val wakeTime = LocalTime.of(profile.wakeTimeHour, profile.wakeTimeMinute)
            if (now.isBefore(wakeTime)) return
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val lastMorning = repo.getLastMorningDateSync()
            if (lastMorning == today) return
            kotlinx.coroutines.runBlocking { repo.saveLastMorningDate(today) }
            val morningIntent = Intent(context, MorningGreetingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(morningIntent)
        }
    }
}
