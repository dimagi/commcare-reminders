package org.commcare.dalvik.reminders.sync

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
        private const val DUMMY_REMINDER_URI = "content://org.commcare.dalvik.reminders//reminder/"
        private const val CC_SESSION_ACTION = "org.commcare.dalvik.action.CommCareSession"
    }


    val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // delete alarms corresponding to old reminders and reschedule for given list of reminders
    fun refreshAlarms(oldReminders: List<Reminder>?, reminders: List<Reminder>) {
        deleteScheduledAlarms(oldReminders)
        scheduleAlarms(reminders)
    }

    private fun deleteScheduledAlarms(oldReminders: List<Reminder>?) {
        oldReminders?.forEach { reminder ->
            val alarmItent = Intent(context, RemindersNotificationReceiver::class.java)
            alarmItent.data = getDummyReminderUri(reminder.id)
            alarmItent.putExtra(EXTRA_NOTIFICATION, buildNotification(reminder))
            alarmItent.putExtra(EXTRA_NOTIFICATION_ID, reminder.id)
            val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_NOTIFICATION_REQUEST,
                alarmItent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmMgr.cancel(alarmPendingIntent)
        }
    }

    private fun buildNotification(reminder: Reminder): Notification {
        val notifyIntent = Intent(CC_SESSION_ACTION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if(reminder.sessionEndpoint != "") {
            notifyIntent.putExtra("ccodk_session_endpoint_id", reminder.sessionEndpoint)
            notifyIntent.putExtra("ccodk_exit_after_form_submission", false)
            val sessionEndpointArgs = ArrayList<String>()
            sessionEndpointArgs.add(reminder.caseId)
            notifyIntent.putStringArrayListExtra(
                "ccodk_session_endpoint_arguments_list",
                sessionEndpointArgs
            )
        }


        return NotificationCompat.Builder(
            context,
            ReminderApplication.DEFAULT_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_action_bell)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.reminder_launcher
                )
            )
            .setChannelId(ReminderApplication.DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(reminder.title)
            .setContentText(reminder.detail)
            .setContentIntent(PendingIntent.getActivity(context, reminder.id.toInt(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true).build()
    }

    fun scheduleAlarms(reminders: List<Reminder>) {
        reminders.filter { it.isInFuture() }
            .forEach { reminder ->
                try {
                    val alarmItent = Intent(context, RemindersNotificationReceiver::class.java)
                    alarmItent.putExtra(EXTRA_NOTIFICATION, buildNotification(reminder))
                    alarmItent.putExtra(EXTRA_NOTIFICATION_ID, reminder.id.toInt())

                    // this is required to uniquely differentiate this Intent from other
                    // scheduled reminders while getting a Pending Intent
                    alarmItent.data = getDummyReminderUri(reminder.id)

                    val reminderTime = TimeUtils.parseDate(reminder.date)
                    val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                        context,
                        REMINDER_NOTIFICATION_REQUEST,
                        alarmItent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmMgr.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime.time,
                        alarmPendingIntent
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
    }

    private fun getDummyReminderUri(id: Long): Uri {
        return Uri.parse(DUMMY_REMINDER_URI + id)
    }
}
