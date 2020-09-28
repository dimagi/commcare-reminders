package org.commcare.dalvik.reminders.model

import androidx.room.Entity

@Entity(primaryKeys = ["caseId", "date"])
data class Reminder(
    val title: String,
    val detail: String?,
    val caseId: String,
    val date: String
)