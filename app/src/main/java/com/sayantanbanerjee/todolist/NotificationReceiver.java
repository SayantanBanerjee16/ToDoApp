package com.sayantanbanerjee.todolist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {


    private static final String CHANNEL_ID = "com.sayantanbanerjee.todolist";

    @Override
    public void onReceive(Context context, Intent intent) {

        int ID;
        int active;
        String title;
        String message;

        ID = intent.getIntExtra("ID", 0);
        title = intent.getStringExtra("Title");
        message = intent.getStringExtra("Message");
        active = intent.getIntExtra("SwitchChecked", 2);

        if (ID != 0) {
            Intent notificationIntent = new Intent(context, ToDoActivity.class);
            notificationIntent.putExtra("ID",ID);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(ToDoActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(ID, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(context);

            Notification notification = builder.setContentTitle(title)
                    .setContentText(message)
                    .setTicker("New ToDo Alert!")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(new long[]{1000, 500, 1000, 500, 1000})
                    .setContentIntent(pendingIntent).build();


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "To Do",
                        NotificationManager.IMPORTANCE_HIGH
                );

                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(ID, notification);

            Log.i("NOTIFICATION RECIEVER  ", Integer.toString(active));

            if (active == 0 || active == 2) {
                notificationManager.cancel(ID);
            }
        }
    }
}
