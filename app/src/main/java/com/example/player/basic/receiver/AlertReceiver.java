package com.example.player.basic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;

import com.example.player.basic.Constant;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;

import java.io.IOException;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("alarmId",1);
        Model model = new Model(context);
        CV cv = new CV();
        model.updateById(Constant.TABLE_CLOCK,cv.status(0),id);
        Cursor cursor = model.getWithArgs(Constant.TABLE_MEDIA,"url","id = ?",new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String url = cursor.getString(cursor.getColumnIndex("url"));
            MediaPlayer player = new MediaPlayer();
            try {
                player.setDataSource(url);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                }
            });
        }
        cursor.close();
    }

}
