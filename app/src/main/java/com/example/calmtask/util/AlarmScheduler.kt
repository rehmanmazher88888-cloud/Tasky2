package com.example.calmtask.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.calmtask.data.model.Meeting
import com.example.calmtask.receiver.AlarmReceiver

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMeetingReminders(meeting: Meeting) {
        val dayBefore = meeting.dateTime - 24 * 60 * 60 * 1000
        val twoHoursBefore = meeting.dateTime - 2 * 60 * 60 * 1000

        if (dayBefore > System.currentTimeMillis()) {
            val intent1 = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("meeting_id", meeting.id)
                putExtra("type", "1day")
            }
            val pendingIntent1 = PendingIntent.getBroadcast(
                context, meeting.id.hashCode() * 10, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dayBefore, pendingIntent1)
        }

        if (twoHoursBefore > System.currentTimeMillis()) {
            val intent2 = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("meeting_id", meeting.id)
                putExtra("type", "2hr")
            }
            val pendingIntent2 = PendingIntent.getBroadcast(
                context, meeting.id.hashCode() * 10 + 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, twoHoursBefore, pendingIntent2)
        }
    }

    fun cancelMeetingAlarms(meetingId: String) {
        val intent1 = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("meeting_id", meetingId)
            putExtra("type", "1day")
        }
        val pi1 = PendingIntent.getBroadcast(context, meetingId.hashCode() * 10, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pi1)

        val intent2 = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("meeting_id", meetingId)
            putExtra("type", "2hr")
        }
        val pi2 = PendingIntent.getBroadcast(context, meetingId.hashCode() * 10 + 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pi2)
    }
}
