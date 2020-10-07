package org.commcare.dalvik.reminders.db

import android.content.Context
import androidx.lifecycle.LiveData
import org.commcare.dalvik.reminders.model.Reminder
import org.commcare.dalvik.reminders.sync.AlarmScheduler
import org.commcare.dalvik.reminders.sync.CommCareUtils
import org.commcare.dalvik.reminders.utils.PrefsUtil

class ReminderRepository(private val reminderDao: ReminderDao) {

    val observeReminders: LiveData<List<Reminder>> = reminderDao.observeReminders()

    suspend fun refreshCasesFromCC(context: Context) {
        CommCareUtils.getRemindersFromCommCare(context).let { reminders ->
            val oldReminders = getAllReminders()
            reminderDao.updateAllReminders(reminders)
            PrefsUtil.markSuccessfulSync(context)
            AlarmScheduler(context).refreshAlarms(oldReminders, reminders)
        }
    }

    suspend fun getAllReminders(): List<Reminder> {
        return reminderDao.getAllReminders()
    }
}