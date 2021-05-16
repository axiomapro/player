package com.example.player.basic.backend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.player.basic.receiver.AlertReceiver;

import java.util.Calendar;

public class Alarm {

    private final Context context;

    public Alarm(Context context) {
        this.context = context;
    }

    public void set(long time,int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("alarmId",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        }
    }

    public void cancel(int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        alarmManager.cancel(pendingIntent);
    }

    public long parseDateTime(String datetime) {
        Calendar calendar = Calendar.getInstance();
        String[] side = datetime.split(" ");
        String[] date = side[0].split("-");
        String[] time = side[1].split(":");
        calendar.set(Calendar.YEAR, Integer.parseInt(date[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

}
