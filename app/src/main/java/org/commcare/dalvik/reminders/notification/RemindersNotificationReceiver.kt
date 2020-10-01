package org.commcare.dalvik.reminders.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.commcare.dalvik.reminders.MainActivity
import org.commcare.dalvik.reminders.R
import org.commcare.dalvik.reminders.ReminderApplication.Companion.DEFAULT_NOTIFICATION_CHANNEL_ID
import org.commcare.dalvik.reminders.model.Reminder

class RemindersNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER = "extra_reminder"
    }

    override fun onReceive(context: Context, intent: Intent?) {
         intent?.getParcelableExtra<Reminder>(EXTRA_REMINDER)?.let { reminder ->

            val notifyIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val notification = NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(reminder.title)
                .setContentText(reminder.detail)
                .setContentIntent(PendingIntent.getActivity(context, 0, notifyIntent, 0))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true).build()

            with(NotificationManagerCompat.from(context)) {
                notify(reminder.id, notification)
            }

        }
    }
}