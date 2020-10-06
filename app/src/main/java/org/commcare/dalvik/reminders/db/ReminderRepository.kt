package org.commcare.dalvik.reminders.db

import android.content.Context
import androidx.lifecycle.LiveData
import org.commcare.dalvik.reminders.model.Reminder
import org.commcare.dalvik.reminders.sync.AlarmScheduler
import org.commcare.dalvik.reminders.sync.CommCareUtils
import org.commcare.dalvik.reminders.utils.PrefsUtil

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun refreshCasesFromCC(context: Context) {
        CommCareUtils.getRemindersFromCommCare(context).let { reminders ->
            val oldReminders = allReminders.value
            reminderDao.updateAllReminders(reminders)
            PrefsUtil.markSuccessfulSync(context)
            AlarmScheduler(context).refreshAlarms(oldReminders, reminders)
        }
    }
}