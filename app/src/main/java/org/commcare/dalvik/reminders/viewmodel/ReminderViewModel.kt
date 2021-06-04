package org.commcare.dalvik.reminders.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.commcare.dalvik.reminders.db.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase
import org.commcare.dalvik.reminders.model.Reminder
import org.commcare.dalvik.reminders.utils.PrefsUtil

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val reminderDao = ReminderRoomDatabase.getDatabase(application).reminderDao()
    private val repository: ReminderRepository = ReminderRepository(reminderDao)
    private val loadTrigger = MutableLiveData(Unit)

    val futureReminders: LiveData<List<Reminder>> =
        Transformations.switchMap(loadTrigger) {
            calculateRemindersInFuture()
        }

    fun refresh() {
        loadTrigger.value = Unit
    }

    fun calculateRemindersInFuture(): LiveData<List<Reminder>> {
        return Transformations.switchMap(repository.observeReminders) { reminders ->
            val filteredReminders = MutableLiveData<List<Reminder>>()
            filteredReminders.value = reminders.filter { it.isInFuture() }
            filteredReminders
        }
    }

    fun syncOnFirstRun() {
        if (PrefsUtil.isSyncPending(getApplication())) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.refreshCasesFromCC(getApplication())
            }
        }
    }
}
