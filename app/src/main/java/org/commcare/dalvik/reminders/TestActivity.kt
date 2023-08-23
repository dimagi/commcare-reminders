package org.commcare.dalvik.reminders

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        findViewById<Button>(R.id.buttonMain).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.buttonPermission).setOnClickListener {
            startPermissionForResult.launch(Intent(this, PermissionActivity::class.java))
        }
    }

    private val startPermissionForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {

                    Toast.makeText(this, it.getStringExtra("permissionName") +
                            " ---> " +
                            it.getStringExtra("isPermissionGranted"),Toast.LENGTH_LONG).show()

                }
            }
        }


}