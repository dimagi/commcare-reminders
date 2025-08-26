package org.commcare.dalvik.reminders

import android.app.Activity
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.remindersRecyclerView
import kotlinx.android.synthetic.main.activity_main.statusTv
import org.commcare.dalvik.reminders.ui.RemindersAdapter
import org.commcare.dalvik.reminders.utils.AndroidUtil
import org.commcare.dalvik.reminders.utils.PermissionUtil
import org.commcare.dalvik.reminders.viewmodel.ReminderViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val COLUMN_COUNT = 2
    }

    private val ALARM_PERMISSION_REQUEST_CODE: Int = 1111;
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var requestPermissionBtn: Button

    private val startPermissionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    intent.getStringExtra("isPermissionGranted")?.let {permissionResult ->
                        if (permissionResult == PermissionActivity.GRANTED) {
                            if (hasNotificationPermission()) {
                                getStarted()
                            } else {
                                requestPermissionBtn.visibility = VISIBLE
                                updatePermissionStatus(true,false)
                            }
                        } else {
                            requestPermissionBtn.visibility = VISIBLE
                            updatePermissionStatus(false,hasNotificationPermission())
                        }
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionBtn = findViewById(R.id.requestSetting)
        requestPermissionBtn.setOnClickListener {
            launchPermissionActivity()
        }

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        validateAlarmPermission()
        setUpUI()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            // Edge-to-edge extends the view behind system bars and given that the app theme is
            // Light, this is to ensure that the status bar appearance is set to light mode
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightStatusBars = true

            val contentView = findViewById<View>(android.R.id.content)
            AndroidUtil.attachWindowInsetsListener(contentView)
        }
    }

    private fun validateAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivityForResult(intent, ALARM_PERMISSION_REQUEST_CODE)
                }
            } else {
                validateReadPermissionsAndSync()
            }
        } else {
            validateReadPermissionsAndSync()
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
                if (PermissionUtil.hasReadPermission(this)) {
                    if (hasNotificationPermission()) {
                        updatePermissionStatus(true,true)
                    } else {
                        updatePermissionStatus(true,false)
                    }

                } else {
                    requestPermissionBtn.visibility = VISIBLE
                    updatePermissionStatus(false,hasNotificationPermission())
                }

            } else {
                statusTv.visibility = GONE
            }
            reminders?.let { remindersAdapter.setReminders(reminders) }
        })
    }

    private fun getStarted() {
        requestPermissionBtn.visibility = GONE
        (application as ReminderApplication).createNotificationChannel()
        reminderViewModel.syncOnFirstRun()
    }

    private fun launchPermissionActivity(){
        startPermissionActivityForResult.launch(Intent(this, PermissionActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ALARM_PERMISSION_REQUEST_CODE) {
            val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager?.canScheduleExactAlarms()!!) {
                showAlarmPermissionNotGranted()
            } else {
                validateReadPermissionsAndSync()
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


    private fun validateReadPermissionsAndSync() {
        if (PermissionUtil.hasReadPermission(this) ) {
            if(hasNotificationPermission()) {
                getStarted()
            }else{
                updatePermissionStatus(true,false)
                launchPermissionActivity()
            }
        } else {
            updatePermissionStatus(false,hasNotificationPermission())
            launchPermissionActivity()
        }
    }

    private fun hasNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionUtil.hasNotificationPermission(this)
        } else {
            true
        }

    private fun updatePermissionStatus(
        hasStoragePermission: Boolean,
        hasNotificationPermission: Boolean
    ) {
        var msg = resources.getString(R.string.following_permission_not_granted)
        if (!hasStoragePermission) {
            msg = msg.plus(getString(R.string.storage_permission_not_granted))
        }

        if (!hasNotificationPermission) {
            msg = msg.plus(getString(R.string.notification_permission_not_granted))
        }
        if (hasStoragePermission && hasNotificationPermission) {
            msg = getString(R.string.storage_granted_msg)
        }
        statusTv.text = msg
        statusTv.visibility = VISIBLE
    }
}
