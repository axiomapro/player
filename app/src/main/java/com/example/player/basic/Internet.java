package com.example.player.basic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Internet {

    public boolean getStatus(Context context) {
        boolean wifi = false;
        boolean edge = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos) {
            if (info.getTypeName().equalsIgnoreCase("WIFI")) {
                if (info.isConnected()) wifi = true;
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (info.isConnected()) edge = true;
            }
        }

        return wifi||edge;
    }

}
