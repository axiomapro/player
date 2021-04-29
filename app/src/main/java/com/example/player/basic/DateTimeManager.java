package com.example.player.basic;

import android.util.Log;

import com.example.player.mvp.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeManager {

    public int days(String date) {
        if (date.length() > 10) date = date.substring(0,10);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String[] parseCurrentDate = currentDate.split("-");
        int currentYear = Integer.parseInt(parseCurrentDate[0]);
        int currentMonth = Integer.parseInt(parseCurrentDate[1]);
        int currentDay = Integer.parseInt(parseCurrentDate[2]);
        String[] parseDate = date.split("-");
        int year = Integer.parseInt(parseDate[0]);
        int month = Integer.parseInt(parseDate[1]);
        int day = Integer.parseInt(parseDate[2]);

        int[] months = {31,28,31,30,31,30,31,31,30,31,30,31};
        int resultYear = currentYear - year;
        int resultMonth = currentMonth - month;
        int resultDay = currentDay - day;
        if (resultMonth < 0) {
            resultYear--;
            resultMonth = 12 + resultMonth;
        }
        if (resultDay < 0) {
            resultMonth--;
            resultDay = months[currentMonth - 1] + resultDay;
        }
        int result = (365 * resultYear) + resultMonth * 30 + resultDay;
        Log.d(MainActivity.LOG,"countDays: "+result);
        return result;
    }

    public boolean time(String time) {
        boolean result = false;
        String[] timeCut = time.split(":");
        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);
        int hour = Integer.parseInt(timeCut[0]);
        int minute = Integer.parseInt(timeCut[1]);
        if (hour > nowHour || hour == nowHour && minute > nowMinute) result = true;
        return result;
    }

    public String restoreDateTime(String datetime) {
        String result = datetime;
        String[] side = datetime.split(" ");
        String date = side[0];
        String[] cutDate = date.split("-");
        String year = cutDate[0];
        String month = addZero(cutDate[1]);
        String day = addZero(cutDate[2]);
        String time = side[1];
        String[] cutTime = time.split(":");
        String hour = addZero(cutTime[0]);
        String minute = addZero(cutTime[1]);
        result = year+"-"+month+"-"+day+" "+hour+":"+minute+":00";
        return result;
    }

    private String addZero(String num) {
        String result = num;
        if (num.length() == 1) result = "0"+num;
        return result;
    }

}
