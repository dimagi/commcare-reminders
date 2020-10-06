package org.commcare.dalvik.reminders.sync

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.commcare.dalvik.reminders.MainActivity
import org.commcare.dalvik.reminders.R
import org.commcare.dalvik.reminders.ReminderApplication
import org.commcare.dalvik.reminders.model.Reminder
import org.commcare.dalvik.reminders.notification.RemindersNotificationReceiver
import org.commcare.dalvik.reminders.notification.RemindersNotificationReceiver.Companion.EXTRA_NOTIFICATION
import org.commcare.dalvik.reminders.notification.RemindersNotificationReceiver.Companion.EXTRA_NOTIFICATION_ID
import org.commcare.dalvik.reminders.utils.TimeUtils
import java.text.ParseException

class AlarmScheduler(private val context: Context) {

    companion object {
        private const val REMINDER_NOTIFICATION_REQUEST = 111111
    }

    val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // delete alarms corresponding to old reminders and reschedule for given list of reminders
    fun refreshAlarms(oldReminders: List<Reminder>?, reminders: List<Reminder>) {
        deleteScheduledAlarms(alarmMgr, oldReminders)
        scheduleAlarms(alarmMgr, reminders)
    }

    private fun deleteScheduledAlarms(alarmMgr: AlarmManager, oldReminders: List<Reminder>?) {
        oldReminders?.forEach { reminder ->
            val alarmItent = Intent(context, RemindersNotificationReceiver::class.java)
            alarmItent.putExtra(EXTRA_NOTIFICATION, buildNotification(reminder))
            alarmItent.putExtra(EXTRA_NOTIFICATION_ID, reminder.id)
            val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_NOTIFICATION_REQUEST,
                alarmItent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmMgr.cancel(alarmPendingIntent)
        }
    }

    private fun buildNotification(reminder: Reminder): Notification {
        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return NotificationCompat.Builder(
            context,
            ReminderApplication.DEFAULT_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.reminder_launcher)
            .setContentTitle(reminder.title)
            .setContentText(reminder.detail)
            .setContentIntent(PendingIntent.getActivity(context, 0, notifyIntent, 0))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true).build()
    }

    private fun scheduleAlarms(alarmMgr: AlarmManager, reminders: List<Reminder>) {
        reminders.filter { it.isInFuture() }
            .forEach { reminder ->
                try {
                    val alarmItent = Intent(context, RemindersNotificationReceiver::class.java)
                    alarmItent.putExtra(EXTRA_NOTIFICATION, buildNotification(reminder))
                    alarmItent.putExtra(EXTRA_NOTIFICATION_ID, reminder.id)
                    val reminderTime = TimeUtils.parseDate(reminder.date)
                    val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                        context,
                        REMINDER_NOTIFICATION_REQUEST,
                        alarmItent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    alarmMgr.set(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime.time,
                        alarmPendingIntent
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
    }
}