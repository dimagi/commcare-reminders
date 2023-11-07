package org.commcare.dalvik.reminders

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import org.commcare.dalvik.reminders.utils.PermissionUtil


class PermissionActivity : AppCompatActivity() {

    companion object {
        const val CC_CASE_READ_PERMISSION = "org.commcare.dalvik.provider.cases.read"
        const val POST_NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
        const val GRANTED = "GRANTED"
        const val DENIED = "DENIED"
        val PEMISSION_ARR = arrayOf(CC_CASE_READ_PERMISSION, POST_NOTIFICATION_PERMISSION)
    }


    private val TAG = "PermissionTest"
    private lateinit var permissionMsgView: TextView
    private lateinit var readPermissionTitle: TextView
    private lateinit var grantPermissionBtn: Button
    private lateinit var appSettingBtn: Button
    private lateinit var settingCardView: CardView

    private var isReadPermissionGranted = false
    private var isNotifcationRequestGranted = false


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d(TAG, "PERMISSION RESULT : ${isGranted}")

            checkPermission()
            if (!isGranted) {
                showSnackbar()
            }
        }

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            Log.d(TAG, "PERMISSION RESULT : ${permissions}")
            isNotifcationRequestGranted =
                permissions[POST_NOTIFICATION_PERMISSION] ?: isNotifcationRequestGranted
            isReadPermissionGranted =
                permissions[CC_CASE_READ_PERMISSION] ?: isReadPermissionGranted

            if (!isNotifcationRequestGranted || !isReadPermissionGranted) {
                showSnackbar()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_activity)

        permissionMsgView = findViewById(R.id.permissionMsg)
        readPermissionTitle = findViewById(R.id.readPermissionTitle)
        settingCardView = findViewById(R.id.appSettingCard)


        appSettingBtn = findViewById(R.id.appSettingBtn)
        appSettingBtn.setOnClickListener {
            launchAppSettingPage()
        }

        grantPermissionBtn = findViewById(R.id.allowReadPermission)

        grantPermissionBtn.setOnClickListener {
            requestMultiplePermissionLauncher.launch(PEMISSION_ARR)
        }

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            dispatchResult(PermissionUtil.hasReadPermission(this))
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dispatchResult(PermissionUtil.hasReadPermission(this@PermissionActivity))
            }
        }
        onBackPressedDispatcher.addCallback(callback)

        checkPermission()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun updateUI(permissionMsg: String, btnVisibility: Int = View.GONE) {
        permissionMsgView.text = permissionMsg
        grantPermissionBtn.visibility = btnVisibility
    }

    private fun dispatchResult(isGranted: Boolean) {
        val resultIntent = Intent().apply {
            putExtra("permissionName", CC_CASE_READ_PERMISSION)
            putExtra(
                "isPermissionGranted", if (isGranted) GRANTED else
                    DENIED
            )
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        when {
            PermissionUtil.hasReadPermission(this) -> {
                if (hasNotificationPermission()) {
                    Log.d(TAG, "READ AND NOTIFICATION permissions are GRANTED")
                    updateUI(resources.getString(R.string.storage_granted_msg), View.GONE)
                    settingCardView.visibility = View.GONE
                    readPermissionTitle.visibility = View.GONE
                } else {
                    updateUI(
                        resources.getString(R.string.notification_permission_not_granted),
                        View.VISIBLE
                    )
                    settingCardView.visibility = View.VISIBLE
                    readPermissionTitle.visibility = View.VISIBLE
                }
            }

            PermissionUtil.shouldShowReadPermissionRationale(this) -> {
                Log.d(TAG, "===>  SHOW RATIONALE MSG ")
                val rationaleMsg =
                    if (shouldShowNotificationPermissionRationaleMsg())
                        R.string.read_notification_permission_rationale_message
                    else
                        R.string.read_permission_rationale_message

                updateUI(
                    resources.getString(rationaleMsg),
                    View.VISIBLE
                )
                settingCardView.visibility = View.GONE
                readPermissionTitle.visibility = View.VISIBLE
            }

            else -> {
                Log.d(TAG, "===> Check FOR PERMISSION")
                updateUI(resources.getString(R.string.storage_permission_not_granted), View.VISIBLE)

                if (!hasNotificationPermission()) {
                    updateUI(
                        permissionMsgView.text.toString()
                            .plus(resources.getString(R.string.notification_permission_not_granted)),
                        View.VISIBLE
                    )
                }


                settingCardView.visibility = View.VISIBLE
                readPermissionTitle.visibility = View.VISIBLE
            }
        }

    }


    private fun launchAppSettingPage() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showSnackbar() {
        if (!PermissionUtil.shouldShowReadPermissionRationale(this)) {
            val snackbar = Snackbar.make(
                findViewById(R.id.layout),
                resources.getString(R.string.noPermissionNote),
                Snackbar.LENGTH_LONG
            ).setAction(resources.getString(R.string.goToSetting)) {
                launchAppSettingPage()
            }

            snackbar.show()
        }

    }

    private fun hasNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionUtil.hasNotificationPermission(this)
        } else {
            true
        }

    private fun shouldShowNotificationPermissionRationaleMsg(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionUtil.shouldShowNotificationPermissionRationale(this)
        } else {
            false
        }
    }


}
