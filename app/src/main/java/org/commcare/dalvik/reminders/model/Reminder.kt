package org.commcare.dalvik.reminders.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val detail: String,
    val caseId: String,
    val date: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(detail)
        parcel.writeString(caseId)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }

}