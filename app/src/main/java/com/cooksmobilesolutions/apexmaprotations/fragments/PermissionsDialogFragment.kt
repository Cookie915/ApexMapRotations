package com.cooksmobilesolutions.apexmaprotations.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment

class PermissionDialogFragment : DialogFragment() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Please enable Alerts & reminders")
                .setPositiveButton("Okay") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    this.startActivity(intent)
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    Toast.makeText(
                        it,
                        "Please enable Alerts & reminders in Settings to schedule alarms",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity Cannot Be Null")
    }
}