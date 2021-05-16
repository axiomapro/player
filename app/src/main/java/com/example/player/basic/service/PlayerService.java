package com.example.player.basic.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Lock;
import com.example.player.basic.backend.Param;
import com.example.player.basic.config.Config;
import com.example.player.basic.notification.Notifications;
import com.example.player.basic.sqlite.Model;
import com.example.player.mvp.main.MainActivity;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer player;
    private Param param;
    private Notifications notifications;
    private Lock lock;
    private Model model;
    public static boolean statusRunning;
    private boolean reset;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Config.log().service(),"onCreate");
        model = new Model(getApplicationContext());
        lock = new Lock(getApplicationContext());
        notifications = new Notifications(getApplicationContext());
        param = new Param(getApplicationContext());
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnErrorListener((mp, what, extra) -> {
            Log.d(Config.log().service(),"onError");
            if (MainActivity.activity != null) MainActivity.activity.showMessage("Не удалось загрузить аудио","playService");
            return false;
        });
        player.setOnPreparedListener(mp -> {
            Log.d(Config.log().service(),"onPrepared");
            if (MainActivity.activity != null && Constant.screen.equals(Config.screen().material())) MainActivity.activity.getMaterialFragment().play(player.getAudioSessionId());
            if (reset) player.seekTo(0);
            player.start();
        });
        player.setOnCompletionListener(mp -> {
            Log.d(Config.log().service(),"onCompletion");
            stopSelf();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Config.log().service(),"onStartCommand: id = "+startId+"; intent: "+intent);
        statusRunning = true;
        Cursor cursor = model.get(Config.table().media(),"name,description,url","id = "+param.getInt(Config.param().track()));
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String desc = cursor.getString(cursor.getColumnIndex("description"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            notifications.player(name,desc,R.drawable.ic_pause);
            lock.action();

            if (intent != null) reset = intent.getBooleanExtra("reset",false);
            try {
                player.setDataSource(url);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cursor.close();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Config.log().service(),"onDestroy");
        statusRunning = false;
        notifications.cancel();
        lock.destroy();
        if (MainActivity.activity != null && Constant.screen.equals(Config.screen().material())) MainActivity.activity.getMaterialFragment().stop();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(Config.log().service(),"onTaskRemoved");
        sendBroadcast(new Intent("restart.service"));
        super.onTaskRemoved(rootIntent);
    }
}
