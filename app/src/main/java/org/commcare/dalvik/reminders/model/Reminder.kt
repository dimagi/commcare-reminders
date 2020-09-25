package org.commcare.dalvik.reminders.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Reminder(@PrimaryKey(autoGenerate = true) val id: Int, val title: String, val date: Date)