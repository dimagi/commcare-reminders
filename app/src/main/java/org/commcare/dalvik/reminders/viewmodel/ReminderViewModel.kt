package org.commcare.dalvik.reminders.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.reminders.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase
import org.commcare.dalvik.reminders.model.Reminder

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val allReminders: LiveData<List<Reminder>>

    init {
        val reminderDao = ReminderRoomDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(reminderDao)
        allReminders = repository.allReminders
    }

    fun saveReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.save(reminder)
    }
}