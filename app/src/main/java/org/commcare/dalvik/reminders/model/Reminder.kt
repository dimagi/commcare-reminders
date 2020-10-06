package org.commcare.dalvik.reminders.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.commcare.dalvik.reminders.utils.TimeUtils
import java.text.ParseException
import java.util.*

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val detail: String,
    val caseId: String,
    val date: String
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