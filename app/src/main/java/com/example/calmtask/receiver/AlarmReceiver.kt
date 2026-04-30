package com.example.calmtask.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import com.example.calmtask.MainActivity
import com.example.calmtask.R
import com.example.calmtask.data.repository.AppRepository
import com.example.calmtask.util.SpeechEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val meetingId = intent.getStringExtra("meeting_id") ?: return
        val type = intent.getStringExtra("type") ?: return
        val repo = AppRepository(context)
        val meetings = runBlocking { repo.meetingsFlow.first() }
        val meeting = meetings.firstOrNull { m -> m.id == meetingId } ?: return

        val title = meeting.title
        val location = meeting.location
        val message = when (type) {
            "1day" -> "You have a meeting tomorrow — $title at $location"
            "2hr" -> "You have a meeting — $title — in 2 hours at $location. Are you prepared?"
            else -> "Meeting reminder: $title"
        }

        val voice4File = File(context.filesDir, "voices/voice4.wav")
        if (voice4File.exists()) {
            MediaPlayer().apply {
                setDataSource(voice4File.absolutePath)
                prepare()
                start()
            }
        } else {
            val tts = SpeechEngine(context)
            runBlocking {
                tts.init("en", 1.0f, 1.0f)
                tts.speak(message)
            }
        }
        showNotification(context, meetingId, type, message)
    }

    private fun showNotification(context: Context, meetingId: String, type: String, text: String) {
        val channelId = "meetings"
        val name = context.getString(R.string.channel_meetings)
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Meeting reminders"
            }
            notifManager.createNotificationChannel(channel)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, meetingId.hashCode(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Meeting Reminder")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notifManager.notify(meetingId.hashCode(), notification)
    }
}
