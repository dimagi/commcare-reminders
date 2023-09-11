package org.commcare.dalvik.reminders.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.commcare.dalvik.reminders.PermissionActivity

object PermissionUtil {

    fun getReadPermissionStatus(context: Context) = ContextCompat.checkSelfPermission(
        context,
        PermissionActivity.CC_CASE_READ_PERMISSION
    )

    fun hasReadPermission(context: Context) = ContextCompat.checkSelfPermission(
        context,
        PermissionActivity.CC_CASE_READ_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED


    fun shouldShowRequestPermissionRationale(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            PermissionActivity.CC_CASE_READ_PERMISSION
        )
}