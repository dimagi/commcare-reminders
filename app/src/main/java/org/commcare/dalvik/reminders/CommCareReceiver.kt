package org.commcare.dalvik.reminders;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.commcare.dalvik.reminders.sync.SyncCasesWorker

class CommCareReceiver : BroadcastReceiver() {

    companion object {
        const val CC_LOGGED_IN_USER_ID_KEY = "cc-logged-in-user-id"
    }
    override fun onReceive(context: Context, intent: Intent) {
        val data = Data.Builder()
        if (intent.hasExtra(CC_LOGGED_IN_USER_ID_KEY)){
            data.putString(CC_LOGGED_IN_USER_ID_KEY, intent.getStringExtra(CC_LOGGED_IN_USER_ID_KEY))
        }
        val syncCasesWorkerRequest = OneTimeWorkRequest.Builder(SyncCasesWorker::class.java)
            .setInputData(data.build()).build()

        WorkManager.getInstance(context).enqueue(syncCasesWorkerRequest);
    }
}
