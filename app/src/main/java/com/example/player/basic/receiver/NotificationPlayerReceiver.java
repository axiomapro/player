package com.example.player.basic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.player.basic.service.PlayerService;

public class NotificationPlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PlayerService.statusRunning) {
            context.stopService(new Intent(context, PlayerService.class));
        } else {
            context.startService(new Intent(context, PlayerService.class));
        }
    }
}
