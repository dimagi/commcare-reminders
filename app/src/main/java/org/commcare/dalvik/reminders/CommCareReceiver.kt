package org.commcare.dalvik.reminders;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.commcare.dalvik.reminders.sync.SyncCasesWorker

class CommCareReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        WorkManager.getInstance(context).enqueue(
        OneTimeWorkRequest.from(SyncCasesWorker::class.java))
    }
}
