package org.commcare.dalvik.reminders

import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.commcare.dalvik.reminders.ui.RemindersAdapter
import org.commcare.dalvik.reminders.viewmodel.ReminderViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val COLUMN_COUNT = 2
        private const val CC_CASE_READ_PERMISSION = "org.commcare.dalvik.provider.cases.read"
        private const val CC_PERMISSION_REQUEST_CODE = 1
    }

    private val ALARM_PERMISSION_REQUEST_CODE: Int = 1111;
    private lateinit var reminderViewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        validateAlarmPermission()
        validatePermissionsAndSync()
        setUpUI()
    }

    private fun validateAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivityForResult(intent, ALARM_PERMISSION_REQUEST_CODE)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reminderViewModel.refresh()
    }

    private fun setUpUI() {
        val remindersAdapter = RemindersAdapter(this)
        remindersRecyclerView.adapter = remindersAdapter
        remindersRecyclerView.layoutManager = GridLayoutManager(this, COLUMN_COUNT)

        reminderViewModel.futureReminders.observe(this, Observer { reminders ->
            if (reminders == null || reminders.isEmpty()) {
                setStatus(R.string.no_reminders_message)
            } else {
                statusTv.visibility = GONE
            }
            reminders?.let { remindersAdapter.setReminders(reminders) }
        })
    }

    private fun validatePermissionsAndSync() {
        if (PermissionChecker.checkSelfPermission(
                this,
                CC_CASE_READ_PERMISSION
            ) == PERMISSION_DENIED
        ) {
            if (shouldShowRequestPermissionRationale(this, CC_CASE_READ_PERMISSION)) {
                showPermissionRationale()
            } else {
                requestPermissions(
                    this,
                    arrayOf(CC_CASE_READ_PERMISSION),
                    CC_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            reminderViewModel.syncOnFirstRun()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CC_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                reminderViewModel.syncOnFirstRun()
            } else {
                setStatus(R.string.no_permission_message)
            }
        }
    }

    private fun setStatus(stringResource: Int) {
        statusTv.text = getString(stringResource)
        statusTv.visibility = VISIBLE
    }

    private fun showPermissionRationale() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(R.string.permission_rationale_message)
            .setTitle(R.string.permission_rationale_title)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ALARM_PERMISSION_REQUEST_CODE) {
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager?.canScheduleExactAlarms()!!) {
                showAlarmPermissionNotGranted()
            } else {
                validatePermissionsAndSync()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showAlarmPermissionNotGranted() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(R.string.alarm_permission_rationale_message)
            .setTitle(R.string.permission_rationale_title)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                // we can't work without Alarm permissions, ask user to grant permissions again
                validateAlarmPermission()
            }
            .create()
        dialog.show()
    }
}
