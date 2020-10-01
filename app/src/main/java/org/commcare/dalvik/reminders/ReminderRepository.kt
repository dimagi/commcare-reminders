package org.commcare.dalvik.reminders

import androidx.lifecycle.LiveData
import org.commcare.dalvik.reminders.db.ReminderDao
import org.commcare.dalvik.reminders.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun updateAllReminders(reminders: List<Reminder>){
        reminderDao.updateAllReminders(reminders)
    }
}