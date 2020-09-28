package org.commcare.dalvik.reminders.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncCasesWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        SyncCasesHelper(applicationContext).sync()
        return Result.success()
    }
}
