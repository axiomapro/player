package com.example.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.player.mvp.view.MainActivity;

import java.io.IOException;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainActivity.LOG,"onStartCommand: "+intent.getStringExtra("track"));
        try {
            player.setDataSource(intent.getStringExtra("track"));
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
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
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(MainActivity.LOG,"TASK REMOVED 1");
        super.onTaskRemoved(rootIntent);
        Log.d(MainActivity.LOG,"TASK REMOVED 2");
        Intent intent = new Intent("restartMe");
        sendBroadcast(intent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }
}
