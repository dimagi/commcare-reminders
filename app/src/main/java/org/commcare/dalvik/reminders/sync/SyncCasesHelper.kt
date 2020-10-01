package org.commcare.dalvik.reminders.sync

import android.content.Context
import org.commcare.dalvik.reminders.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase

class SyncCasesHelper(private val context: Context) {

    private val repository: ReminderRepository

    init {
        val reminderDao = ReminderRoomDatabase.getDatabase(context).reminderDao()
        repository = ReminderRepository(reminderDao)
    }

    suspend fun sync() {
        CommCareUtils.getRemindersFromCommCare(context).let { reminders ->
            val oldReminders = repository.allReminders.value
            repository.updateAllReminders(reminders)
            AlarmScheduler(context).refreshAlarms(oldReminders, reminders)
        }
    }
}
