package org.commcare.dalvik.reminders.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.commcare.dalvik.reminders.model.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * from reminder ORDER BY date ASC")
    fun observeReminders(): LiveData<List<Reminder>>

    @Query("SELECT * from reminder ORDER BY date ASC")
    suspend fun getAllReminders(): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Transaction
    suspend fun updateAllReminders(reminders: List<Reminder>) {
        deleteAllReminders()
        var ids = insertReminders(reminders)
        reminders.forEachIndexed { index, reminder -> reminder.id = ids[index] }
    }

    @Query("DELETE FROM reminder")
    suspend fun deleteAllReminders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<Reminder>) : List<Long>

}
