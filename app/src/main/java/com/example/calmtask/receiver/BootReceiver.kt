package com.example.calmtask.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.calmtask.service.CalmTaskForegroundService
import com.example.calmtask.data.repository.AppRepository
import com.example.calmtask.util.AlarmScheduler
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CalmTaskForegroundService.start(context)
            val repo = AppRepository(context)
            val scheduler = AlarmScheduler(context)
            runBlocking { repo.reRegisterMeetingAlarms(scheduler) }
        }
    }
}
