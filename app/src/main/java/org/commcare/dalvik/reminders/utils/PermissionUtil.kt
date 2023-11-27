package org.commcare.dalvik.reminders.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
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


    fun hasNotificationPermission(context: Context) = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED


    fun shouldShowReadPermissionRationale(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            PermissionActivity.CC_CASE_READ_PERMISSION
        )

    fun shouldShowNotificationPermissionRationale(activity: Activity) =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            PermissionActivity.POST_NOTIFICATION_PERMISSION
        )
}