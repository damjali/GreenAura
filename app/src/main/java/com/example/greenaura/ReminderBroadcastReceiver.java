package com.example.greenaura;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

//show notification
public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("NotificationPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String locationDetails = intent.getStringExtra("locationDetails");

        // Show a simple notification to the user
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                //  .setSmallIcon(R.drawable.ic_reminder)  // Set the appropriate icon
                .setContentTitle("Reminder!")
                .setContentText("Reminder for: " + locationDetails)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("reminder_channel",
                    "Reminder Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, builder.build()); // Use an appropriate notification ID
    }
}