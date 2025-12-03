package com.app.helthcare

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MedicineActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_TAKE_MEDICINE") {
            val medicineId = intent.getStringExtra("medicine_id")
            if (medicineId != null) {
                val repository = MedicineRepository(context)
                repository.markAsTaken(medicineId)

                // Cancel the notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationId = intent.getIntExtra("notification_id", 0)
                notificationManager.cancel(notificationId)
            }
        }
    }
}