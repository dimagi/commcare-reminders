package org.commcare.dalvik.reminders.notification

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class RemindersNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_NOTIFICATION = "extra_notification"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val DEFAULT_NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, intent: Intent?) {
         intent?.getParcelableExtra<Notification>(EXTRA_NOTIFICATION)?.let { notification ->
            with(NotificationManagerCompat.from(context)) {
                notify(intent.getIntExtra(EXTRA_NOTIFICATION_ID, DEFAULT_NOTIFICATION_ID), notification)
            }
        }
    }
}