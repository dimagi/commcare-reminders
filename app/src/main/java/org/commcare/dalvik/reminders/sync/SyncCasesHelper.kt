package org.commcare.dalvik.reminders.sync

import android.content.Context
import android.util.Log
import org.commcare.commcaresupportlibrary.CaseUtils
import org.commcare.dalvik.reminders.ReminderRepository
import org.commcare.dalvik.reminders.db.ReminderRoomDatabase
import org.commcare.dalvik.reminders.model.Reminder

class SyncCasesHelper(private val context: Context) {

    companion object {
        private const val COMMCARE_REMINDER_CASE_TYPE = "commcare-reminder"
        private const val CASE_ID = "case_id"
        private const val CASE_NAME = "case_name"
        private const val DETAIL = "detail"
        private const val TIME = "time"
    }

    private val repository: ReminderRepository

    init {
        val reminderDao = ReminderRoomDatabase.getDatabase(context).reminderDao()
        repository = ReminderRepository(reminderDao)
    }

    suspend fun sync() {
        val remindersCaseCursor = CaseUtils.getCaseMetaData(
            context,
            "case_type = ? and status = 'open'",
            arrayOf(COMMCARE_REMINDER_CASE_TYPE)
        )

        remindersCaseCursor.use {
            while (remindersCaseCursor.moveToNext()) {
                val props = ArrayList<String>()
                props.add(DETAIL)
                props.add(TIME)
                val caseProperties = CaseUtils.getCaseProperties(
                    context,
                    remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_ID)),
                    props
                )

                val detail = caseProperties[DETAIL]
                val time = caseProperties[TIME]

                if (time != null) {
                    val title = remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_NAME))
                    val caseId = remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_ID))
                    val reminder = Reminder(title, detail, caseId, time)
                    repository.save(reminder)
                }
            }
        }
    }

}
