package org.commcare.dalvik.reminders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.commcare.dalvik.reminders.ui.RemindersAdapter
import org.commcare.dalvik.reminders.viewmodel.ReminderViewModel

// todo request permission and Schedule sync
class MainActivity : AppCompatActivity() {

    companion object {
        private const val COLUMN_COUNT: Int = 2
    }

    private lateinit var reminderViewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val remindersRecyclerView = findViewById<RecyclerView>(R.id.reminder_rv)
        val remindersAdapter = RemindersAdapter(this)
        remindersRecyclerView.adapter = remindersAdapter
        remindersRecyclerView.layoutManager = GridLayoutManager(this, COLUMN_COUNT)

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(this, Observer { reminders ->
            reminders?.let { remindersAdapter.setReminders(reminders) }
        })
    }
}