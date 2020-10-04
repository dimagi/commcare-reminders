package org.commcare.dalvik.reminders

import android.content.Context
import androidx.preference.PreferenceManager

object PrefsUtil {
    private const val SUCCESFUL_CC_SYNC = "successful_cc_sync"

    fun markSuccessfulSync(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(SUCCESFUL_CC_SYNC, true)
            .apply()
    }

    fun isSyncPending(context: Context): Boolean {
        return !PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SUCCESFUL_CC_SYNC, false)
    }


}