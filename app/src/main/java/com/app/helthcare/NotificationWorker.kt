package com.app.helthcare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val medicineName = inputData.getString("medicine_name") ?: "Medicine"
        val medicineDescription = inputData.getString("medicine_description") ?: "Don't forget to take your medicine."
        val medicineId = inputData.getString("medicine_id") ?: ""

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("medicine_channel", "Medicine Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Unique ID for notification
        val notificationId = medicineId.hashCode()

        // Create "Taken" action intent
        val takeIntent = Intent(applicationContext, MedicineActionReceiver::class.java).apply {
            action = "ACTION_TAKE_MEDICINE"
            putExtra("medicine_id", medicineId)
            putExtra("notification_id", notificationId)
        }
        
        val takePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            takeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "medicine_channel")
            .setContentTitle(medicineName)
            .setContentText("It's time to take your medicine: $medicineDescription")
            .setSmallIcon(R.drawable.ic_add)
            .addAction(R.drawable.ic_add, "Taken", takePendingIntent) // Using ic_add temporarily if no check icon
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)

        return Result.success()
    }
}