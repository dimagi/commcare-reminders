package org.commcare.dalvik.reminders.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.commcare.dalvik.reminders.db.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase

class SyncCasesWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val reminderDao = ReminderRoomDatabase.getDatabase(applicationContext).reminderDao()
        ReminderRepository(reminderDao).refreshCasesFromCC(applicationContext)
        return Result.success()
    }
}
