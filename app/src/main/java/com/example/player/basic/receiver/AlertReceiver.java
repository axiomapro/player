package com.example.player.basic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.player.basic.service.PlayerService;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.notification.Notifications;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;
import com.example.player.mvp.main.MainActivity;

import java.io.IOException;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("restart.service")) {
            MainActivity.activity = null;
            context.startService(new Intent(context,PlayerService.class));
        } else {
            int id = intent.getIntExtra("alarmId",1);
            Model model = new Model(context);
            CV cv = new CV();
            Notifications notifications = new Notifications(context);
            model.updateById(Constant.TABLE_CLOCK,cv.status(0),id);
            Cursor cursor = model.getWithArgs(Constant.TABLE_MEDIA,"name,url","id = ?",new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndex("name"));
                String message = "It`s time to play "+title;
                notifications.alarm(message,id);
                if (MainActivity.activity != null) MainActivity.activity.showMessage(message,"receiver");
            }
            cursor.close();
        }
    }


}
