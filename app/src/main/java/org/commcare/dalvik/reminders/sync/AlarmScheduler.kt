package org.commcare.dalvik.reminders.sync

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.commcare.dalvik.reminders.model.Reminder
import org.commcare.dalvik.reminders.notification.RemindersNotificationReceiver
import org.commcare.dalvik.reminders.notification.RemindersNotificationReceiver.Companion.EXTRA_REMINDER
import org.commcare.dalvik.reminders.utils.TimeUtils

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
        val alarmItent = Intent(context, RemindersNotificationReceiver::class.java)
        oldReminders?.forEach { reminder ->
            alarmItent.putExtra(EXTRA_REMINDER, reminder)
            val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_NOTIFICATION_REQUEST,
                alarmItent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmMgr.cancel(alarmPendingIntent)
        }
    }

    private fun scheduleAlarms(alarmMgr: AlarmManager, reminders: List<Reminder>) {

        val alarmItent = Intent(context, RemindersNotificationReceiver::class.java)

        reminders.forEach { reminder ->
            val reminderTime = TimeUtils.parseDate(reminder.date)
            alarmItent.putExtra(EXTRA_REMINDER, reminder)
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
        }
    }
}