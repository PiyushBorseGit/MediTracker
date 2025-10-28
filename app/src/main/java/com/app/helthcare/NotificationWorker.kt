package com.app.helthcare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val medicineName = inputData.getString("medicine_name") ?: "Medicine"
        val medicineDescription = inputData.getString("medicine_description") ?: "Don't forget to take your medicine."

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("medicine_channel", "Medicine Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "medicine_channel")
            .setContentTitle(medicineName)
            .setContentText("its time for medicine")
            .setSmallIcon(R.drawable.ic_add)
            .build()

        notificationManager.notify(1, notification)

        return Result.success()
    }
}