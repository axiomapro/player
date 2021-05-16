package com.example.player.basic.backend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public class Lock {

    private final Context context;
    private final WifiManager wm;
    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakeLock;

    public Lock(Context context) {
        this.context = context;
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @SuppressLint("InvalidWakeLockTag")
    public void action() {
        wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF , "MyWifiLock");
        wifiLock.acquire();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
    }

    public void destroy() {
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
        if (wifiLock != null) {
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }
        }
    }

}

