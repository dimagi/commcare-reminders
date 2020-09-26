package org.commcare.dalvik.reminders;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class CommCareReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        WorkManager.getInstance(context).enqueue(
        OneTimeWorkRequest.from(SyncCases::class.java))
    }
}
