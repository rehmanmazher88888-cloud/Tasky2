package com.example.calmtask.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.calmtask.MainActivity
import com.example.calmtask.R

class CalmTaskForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "foreground_service",
                getString(R.string.channel_foreground),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "CalmTask background checks" }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification() = NotificationCompat.Builder(this, "foreground_service")
        .setContentTitle("CalmTask is active")
        .setContentText("Morning and reminder checks are on")
        .setSmallIcon(R.drawable.ic_notification)
        .setContentIntent(
            PendingIntent.getActivity(
                this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .setSilent(true)
        .build()

    companion object {
        fun start(context: Context) {
            context.startForegroundService(Intent(context, CalmTaskForegroundService::class.java))
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, CalmTaskForegroundService::class.java))
        }
    }
}
