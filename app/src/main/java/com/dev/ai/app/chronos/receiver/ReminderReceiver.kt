package com.dev.ai.app.chronos.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dev.ai.app.chronos.MainActivity
import com.dev.ai.app.chronos.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        val notes = intent.getStringExtra("notes") ?: ""
        Log.d("ReminderReceiver", "Received alarm for reminder: $title")

        // Check for notification permission (required for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("ReminderReceiver", "Notification permission not granted")
                return
            }
        }

        showNotification(context, title, notes)
    }

    private fun showNotification(context: Context, title: String, notes: String) {
        val channelId = "reminder_channel"
        val notificationId = System.currentTimeMillis().toInt()

        // Create an Intent to launch MainActivity
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create notification channel for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for reminder notifications"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(notes)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Set the PendingIntent to open the app

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
            Log.d("ReminderReceiver", "Notification shown for: $title")
        } catch (e: SecurityException) {
            Log.e("ReminderReceiver", "Failed to show notification: ${e.message}")
        }
    }
}