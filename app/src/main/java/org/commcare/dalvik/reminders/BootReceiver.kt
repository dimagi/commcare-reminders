package org.commcare.dalvik.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.commcare.dalvik.reminders.db.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase
import org.commcare.dalvik.reminders.sync.AlarmScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            val reminderDao = ReminderRoomDatabase.getDatabase(context).reminderDao()
            val repository = ReminderRepository(reminderDao)
            GlobalScope.launch(Dispatchers.Default) {
                repository.getAllReminders()?.let { reminders ->
                    AlarmScheduler(context).scheduleAlarms(reminders)
                }
            }
        }
    }
}