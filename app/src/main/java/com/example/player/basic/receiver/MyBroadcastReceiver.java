package com.example.player.basic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.player.basic.backend.Alarm;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Model model = new Model(context);
            CV cv = new CV();
            Alarm alarm = new Alarm(context);
            Cursor cursor = model.get(Constant.TABLE_CLOCK,"id","date <= current_date and status = 1");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                model.updateById(Constant.TABLE_CLOCK,cv.status(0),id);
                alarm.cancel(id);
            }

            Cursor cursorTwo = model.get(Constant.TABLE_CLOCK,"id,date","status = 1 and del = 0");
            while (cursorTwo.moveToNext()) {
                int id = cursorTwo.getInt(cursorTwo.getColumnIndex("id"));
                String datetime = cursorTwo.getString(cursorTwo.getColumnIndex("date"));
                alarm.set(alarm.parseDateTime(datetime),id);
            }
        }
    }

}
