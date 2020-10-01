package org.commcare.dalvik.reminders.sync

import android.content.Context
import org.commcare.commcaresupportlibrary.CaseUtils
import org.commcare.dalvik.reminders.model.Reminder

object CommCareUtils {

    private const val COMMCARE_REMINDER_CASE_TYPE = "commcare-reminder"
    private const val CASE_ID = "case_id"
    private const val CASE_NAME = "case_name"
    private const val DETAIL = "detail"
    private const val TIME = "time"

    fun getRemindersFromCommCare(context : Context): ArrayList<Reminder> {
        val remindersCaseCursor = CaseUtils.getCaseMetaData(
            context,
            "case_type = ? and status = 'open'",
            arrayOf(COMMCARE_REMINDER_CASE_TYPE)
        )
        val reminders = ArrayList<Reminder>()
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

                val detail = caseProperties[DETAIL] ?: ""
                val time = caseProperties[TIME]

                if (time != null) {
                    val title = remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_NAME))
                    val caseId = remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_ID))
                    reminders.add(Reminder(0, title, detail, caseId, time))
                }
            }
        }
        return reminders
    }
}