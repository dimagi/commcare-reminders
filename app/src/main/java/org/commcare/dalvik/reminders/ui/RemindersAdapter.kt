package org.commcare.dalvik.reminders.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.commcare.dalvik.reminders.R
import org.commcare.dalvik.reminders.model.Reminder
import org.commcare.dalvik.reminders.utils.TimeUtils
import java.text.ParseException

class RemindersAdapter internal constructor(private val context: Context) :
    RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var reminders = emptyList<Reminder>()

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.reminder_title)
        val detail: TextView = itemView.findViewById(R.id.reminder_detail)
        val time: TextView = itemView.findViewById(R.id.reminder_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val reminderView = inflater.inflate(R.layout.reminder_list_item, parent, false)
        return ReminderViewHolder(reminderView)
    }

    override fun getItemCount() = reminders.size

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.title.text = reminder.title
        holder.detail.text = reminder.detail
        try {
            holder.time.text = TimeUtils.getUserFriendlyTime(context, reminder.date)
        } catch (e: ParseException) {
            holder.time.text = reminder.date
        }
    }

    internal fun setReminders(reminders: List<Reminder>) {
        this.reminders = reminders
        notifyDataSetChanged()
    }
}