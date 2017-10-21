package com.example.williamrussa3.petmanagerapplication;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    //Receive broadcast from system

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //Get notification message from intent, set icon, title, and message
        String message = intent.getStringExtra("Message");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("It's that time of the day!")
                .setContentText(message);

        //Set parent stack so notification goes to Pet menu of app when notification is clicked on

        Intent resultIntent = new Intent(context, FeedingActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PetActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Set vibration and color of lights (if device supports it)

        mBuilder.setVibrate(new long[]{1000, 1000});
        mBuilder.setLights(Color.GREEN, 3000, 3000);

        mNotificationManager.notify(1, mBuilder.build());


    }
}