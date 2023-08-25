package org.commcare.dalvik.reminders.sync

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.commcare.commcaresupportlibrary.CaseUtils
import org.commcare.dalvik.reminders.model.Reminder

object CommCareUtils {

    private const val COMMCARE_REMINDER_CASE_TYPE = "commcare-reminder"
    private const val CASE_ID = "case_id"
    private const val CASEID = "caseid"
    private const val CASE_NAME = "case_name"
    private const val DETAIL = "detail"
    private const val TIME = "time"
    private const val SESSION_ENDPOINT = "session_endpoint"
    // This case property will store a list of User IDs, separated by space or a coma, for whom
    // the reminder should not be created
    private const val USERS_TO_SKIP_REMINDER = "users_to_skip_reminder"

    fun getRemindersFromCommCare(context: Context, currentUserId: String?): ArrayList<Reminder> {
        val remindersCaseCursor = CaseUtils.getCaseMetaData(
            context,
            "case_type = ? and status = 'open'",
            arrayOf(COMMCARE_REMINDER_CASE_TYPE)
        )
        val reminders = ArrayList<Reminder>()
        remindersCaseCursor?.use {
            while (remindersCaseCursor.moveToNext()) {
                val props = ArrayList<String>()
                props.add(DETAIL)
                props.add(TIME)
                props.add(SESSION_ENDPOINT)
                props.add(CASEID)
                props.add(USERS_TO_SKIP_REMINDER)
                val caseProperties = CaseUtils.getCaseProperties(
                    context,
                    remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_ID)),
                    props
                )

                val detail = caseProperties[DETAIL] ?: ""
                val time = caseProperties[TIME]
                val sessionEndpoint = caseProperties[SESSION_ENDPOINT] ?: ""
                val caseId = caseProperties[CASEID] ?: ""
                val usersToSkipReminder = caseProperties[USERS_TO_SKIP_REMINDER] ?: ""

                if (currentUserId != null && usersToSkipReminder.isNotEmpty()
                    && usersToSkipReminder.contains(currentUserId, true)){
                    continue
                }
                if (time != null) {
                    val title = remindersCaseCursor.getString(remindersCaseCursor.getColumnIndex(CASE_NAME))
                    reminders.add(Reminder(0, title, detail, caseId, time, sessionEndpoint))
                }
            }
        }
        return reminders
    }
}
