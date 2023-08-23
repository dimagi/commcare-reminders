package org.commcare.dalvik.reminders

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import org.commcare.dalvik.reminders.utils.PermissionUtil


class PermissionActivity : AppCompatActivity() {

    companion object {
        const val CC_CASE_READ_PERMISSION = "org.commcare.dalvik.provider.cases.read"
        const val GRANTED = "GRANTED"
        const val DENIED = "DENIED"
    }


    private val TAG = "PermissionTest"
    private lateinit var permissionMsgView: TextView
    private lateinit var grantPermissionBtn: Button
    private lateinit var appSettingBtn: Button
    private lateinit var settingCardView: CardView


    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d(TAG, "PERMISSION RESULT : ${isGranted}")

            checkPermission()
            if (!isGranted) {
                showSnackbar()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_activity)

        permissionMsgView = findViewById(R.id.permissionMsg)
        settingCardView = findViewById(R.id.appSettingCard)


        appSettingBtn = findViewById(R.id.appSettingBtn)
        appSettingBtn.setOnClickListener {
            launchAppSettingPage()
        }

        grantPermissionBtn = findViewById(R.id.allowReadPermission)
        grantPermissionBtn.tag = 0
        grantPermissionBtn.setOnClickListener {
            requestMultiplePermissionLauncher.launch(CC_CASE_READ_PERMISSION)
        }

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            dispatchResult(PermissionUtil.hasReadPermission(this))
        }

        checkPermission()

    }

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


    private fun checkPermission() {
        when {
            PermissionUtil.hasReadPermission(this) -> {
                Log.d(TAG, "Permission is GRANTED")
                updateUI(resources.getString(R.string.storage_granted_msg), View.GONE)
                settingCardView.visibility = View.GONE
            }

            PermissionUtil.shouldShowRequestPermissionRationale(this) -> {
                Log.d(TAG, "===>  SHOW RATIONALE MSG ")
                updateUI(resources.getString(R.string.permission_rationale_message), View.VISIBLE)
                settingCardView.visibility = View.GONE
            }

            else -> {
                Log.d(TAG, "===> Check FOR PERMISSION")
                updateUI(resources.getString(R.string.storage_permission_not_granted), View.VISIBLE)
                settingCardView.visibility = View.VISIBLE
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
        if(!PermissionUtil.shouldShowRequestPermissionRationale(this)){
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


}
