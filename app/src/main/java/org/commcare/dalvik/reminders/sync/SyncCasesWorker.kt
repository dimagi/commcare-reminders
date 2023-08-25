package org.commcare.dalvik.reminders.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.commcare.dalvik.reminders.CommCareReceiver.Companion.CC_LOGGED_IN_USER_ID_KEY
import org.commcare.dalvik.reminders.db.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase

class SyncCasesWorker(appContext: Context, private val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val data = workerParams.inputData
        val currentUserId = data.getString(CC_LOGGED_IN_USER_ID_KEY)
        val reminderDao = ReminderRoomDatabase.getDatabase(applicationContext).reminderDao()
        ReminderRepository(reminderDao).refreshCasesFromCC(applicationContext, currentUserId)
        return Result.success()
    }
}
