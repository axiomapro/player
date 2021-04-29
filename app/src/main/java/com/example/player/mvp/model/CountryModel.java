package com.example.player.mvp.model;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.player.mvp.view.MainActivity;
import com.example.player.basic.Constant;
import com.example.player.basic.Param;
import com.example.player.basic.item.Item;
import com.example.player.basic.sqlite.Model;
import com.example.player.basic.sync.MyCountryJobService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class CountryModel {

    private final Context context;
    private final Model model;

    public CountryModel(Context context) {
        this.model = new Model(context);
        this.context = context;
    }

    public void sendRequest() {
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        int exist = jobScheduler.getAllPendingJobs().size();
        if (exist == 0) {
            ComponentName componentName = new ComponentName(context, MyCountryJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(1,componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // wifi or mobile
                    .build();
            JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) Log.d(MainActivity.LOG, "checkSchedule: запланировано");
            else Log.d(MainActivity.LOG, "checkSchedule: не удалось запланировать");
        } else Log.d(MainActivity.LOG, "checkSchedule: было запланировано ранее");
    }

    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = model.getWithArgs(Constant.TABLE_COUNTRY,"server,name,img","del = ?",new String[]{String.valueOf(0)});
        while (cursor.moveToNext()) {
            list.add(new Item(cursor.getInt(cursor.getColumnIndex("server")),cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("img"))));
        }
        return list;
    }

    public void saveCountry(Context context, int id) {
        Param param = new Param(context);
        param.setInt(Constant.PARAM_COUNTRY, id);
    }

}
