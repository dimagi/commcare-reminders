package org.commcare.dalvik.reminders.utils

import android.content.Context
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object TimeUtils {

    fun getUserFriendlyTime(context: Context, date: String): CharSequence {
        val date = parseDate(date)
        return DateUtils.getRelativeDateTimeString(
            context,
            date.time,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.WEEK_IN_MILLIS,
            0
        )
    }

    fun parseDate(dateStr: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(dateStr)
        } catch (e: ParseException) {
            try {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).parse(dateStr)
            } catch (e: ParseException) {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)
            }
        }
    }
}