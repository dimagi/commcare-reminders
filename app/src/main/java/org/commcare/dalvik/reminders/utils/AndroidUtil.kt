package org.commcare.dalvik.reminders.utils;

import android.os.Build
import android.view.View
import android.view.WindowInsets.Type
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat

object AndroidUtil {

    // Edge-to-edge insets handling for Android 15 and above
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun attachWindowInsetsListener(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            val windowInsets = view.rootWindowInsets
            if (windowInsets != null) {
                val systemBars = windowInsets.getInsets(Type.systemBars())

                // Apply padding so content doesn't overlap with system bars
                view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
            }
            insets
        }
    }
}
