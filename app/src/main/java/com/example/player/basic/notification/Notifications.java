package com.example.player.basic.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.player.R;
import com.example.player.mvp.main.MainActivity;

public class Notifications {

    private final Context context;
    private NotificationManagerCompat notificationManagerCompat;
    private final int PLAYER_ID = 100;

    public Notifications(Context context) {
        this.context = context;
    }

    public void player(String name,String desc,int iconAudio) {

        Intent intentPause = new Intent("player.pause");
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 1, intentPause, 0);

        notificationManagerCompat = NotificationManagerCompat.from(context);
        Bitmap largeImage = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        Notification channel = new NotificationCompat.Builder(context,App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_track)
                .setContentTitle(name)
                .setContentText(desc)
                .setLargeIcon(largeImage)
                .addAction(new NotificationCompat.Action(R.drawable.ic_pause,"pause",pendingIntentPause))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
                .setOngoing(true)
                .build();
        notificationManagerCompat.notify(PLAYER_ID,channel);
    }

    public void alarm(String title,int id) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

        notificationManager.notify(id,builder.build());
    }

    public void cancel() {
        notificationManagerCompat.cancel(PLAYER_ID);
    }

}
