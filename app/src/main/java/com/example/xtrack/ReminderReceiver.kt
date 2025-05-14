package com.example.xtrack

import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Notification
import android.content.BroadcastReceiver
import androidx.core.app.NotificationCompat
import com.example.xtrack.MainActivity
import com.example.xtrack.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Create an intent to open the app when notification is tapped
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        val notification = NotificationCompat.Builder(context, "workoutReminderChannel")
            .setSmallIcon(R.drawable.bell) // Set your notification icon here
            .setContentTitle("Time for your workout!")
            .setContentText("It's time to track your workout performance!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)  // Automatically dismiss the notification when tapped
            .build()

        // Notify the user
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}
