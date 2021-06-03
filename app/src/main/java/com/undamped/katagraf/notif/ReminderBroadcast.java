package com.undamped.katagraf.notif;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.undamped.katagraf.R;

public class ReminderBroadcast extends BroadcastReceiver {

    public ReminderBroadcast(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        String itemName = intent.getStringExtra("ItemName");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyUs")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Time to use some items")
                .setContentText("Please consume " + itemName + " before it expires!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setGroup("Reminder")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}