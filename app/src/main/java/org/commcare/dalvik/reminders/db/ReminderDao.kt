package org.commcare.dalvik.reminders.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.commcare.dalvik.reminders.model.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * from reminder ORDER BY date ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Transaction
    suspend fun updateAllReminders(reminders: List<Reminder>) {
        deleteAllReminders()
        insertReminders(reminders)
    }

    @Query("DELETE FROM reminder")
    suspend fun deleteAllReminders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<Reminder>)

}