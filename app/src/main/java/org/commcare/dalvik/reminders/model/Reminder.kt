package org.commcare.dalvik.reminders.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.commcare.dalvik.reminders.utils.TimeUtils
import java.text.ParseException
import java.util.*

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val title: String,
    val detail: String,
    val caseId: String,
    val date: String,
    val sessionEndpoint: String
) {

    fun isInFuture(): Boolean {
        try {
            val date = TimeUtils.parseDate(date)
            return date.time >= Date().time
        } catch (e: ParseException) {
            // do nothing
        }
        return false
    }
}
