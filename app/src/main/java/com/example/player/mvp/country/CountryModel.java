package com.example.player.mvp.country;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.player.basic.config.Config;
import com.example.player.mvp.main.MainActivity;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Param;
import com.example.player.basic.list.Item;
import com.example.player.basic.sqlite.Model;
import com.example.player.basic.service.MyCountryJobService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class CountryModel extends Model {

    private final Context context;

    public CountryModel(Context context) {
        super(context);
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
            if (resultCode == JobScheduler.RESULT_SUCCESS) Log.d(Config.log().basic(), "checkSchedule: запланировано");
            else Log.d(Config.log().basic(), "JobScheduler: не удалось запланировать");
        } else Log.d(Config.log().basic(), "JobScheduler: было запланировано ранее");
    }

    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = getWithArgs(table,"server,name,img","del = ?",new String[]{String.valueOf(0)});
        while (cursor.moveToNext()) {
            list.add(new Item(cursor.getInt(cursor.getColumnIndex("server")),cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("img"))));
        }
        return list;
    }

    public void saveCountry(Context context, int id) {
        Param param = new Param(context);
        param.setInt(Config.param().country(), id);
    }

}
