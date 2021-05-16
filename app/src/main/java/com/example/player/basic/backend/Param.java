package com.example.player.basic.backend;

import android.content.Context;
import android.content.SharedPreferences;

public class Param {

    private SharedPreferences sPref;
    private final Context context;

    public Param(Context context) {
        this.context = context;
    }

    public int getInt(String type) {
        sPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        return sPref.getInt(type, 0);
    }

    public String getString(String type) {
        sPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        return sPref.getString(type, null);
    }

    public boolean getBoolean(String type) {
        sPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        return sPref.getBoolean(type,false);
    }

    public void setInt(String type, int num) {
        sPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(type, num);
        ed.apply();
    }

    public void setString(String type, String param) {
        sPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(type, param);
        ed.apply();
    }

    public void setBoolean(String type, boolean status) {
        sPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(type, status);
        ed.apply();
    }

}
