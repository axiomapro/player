package com.example.player.mvp.model;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.example.player.mvp.view.MainActivity;
import com.example.player.basic.Constant;
import com.example.player.basic.DateTimeManager;
import com.example.player.basic.Internet;
import com.example.player.basic.Param;
import com.example.player.basic.Permission;
import com.example.player.basic.sqlite.DatabaseHelper;
import com.example.player.basic.sqlite.Model;
import com.example.player.basic.sync.MyJobService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class MainModel extends Model {

    private ModelListener listener;
    private final Context context;
    private final Param param;
    private final Permission permission;
    private final Internet internet;
    private final DateTimeManager checkDateTime;
    private final DatabaseHelper databaseHelper;
    private String folderBackup = Environment.getExternalStorageDirectory()+"/MediaPlayer";
    private String backupFile = folderBackup+"/database.db";

    public interface ModelListener {
        void parseConfig();
    }

    public MainModel(Context context, ModelListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.param = new Param(context);
        this.permission = new Permission(context);
        this.internet = new Internet();
        this.checkDateTime = new DateTimeManager();
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    public void restoreDatabase() {
        if (param.getBoolean(Constant.PARAM_CHECK_IMPORT_DB)) {
            Log.d(MainActivity.LOG,"restoreDatabase: это не первый запуск");
            sendRequest();
        } else {
            if (permission.check()) {
                Log.d(MainActivity.LOG,"restoreDatabase: разрешение получено");
                param.setBoolean(Constant.PARAM_CHECK_IMPORT_DB,true);
                File backup = new File(backupFile);
                if (backup.exists()) {
                    Log.d(MainActivity.LOG,"restoreDatabase: импортировано");
                    copyFile(backupFile,context.getDatabasePath("database.db").toString());
                    if (!databaseHelper.isOpenDb()) databaseHelper.openDb();
                    Cursor cursor = getWithArgs(Constant.TABLE_COUNTRY,"updated","del = ? order by updated desc limit 1",new String[]{String.valueOf(param.getInt(Constant.PARAM_COUNTRY))});
                    if (cursor.moveToFirst()) param.setString(Constant.PARAM_UPDATED,cursor.getString(cursor.getColumnIndex("updated")));
                    cursor.close();
                }
                sendRequest();
            } else {
                Log.d(MainActivity.LOG,"restoreDatabase: покажи окно разрешения");
                permission.show();
            }
        }
    }

    public void sendRequest() {
        Log.d(MainActivity.LOG,"sendRequest");
        Cursor cursor = getWithArgs(Constant.TABLE_COUNTRY,"updated","server = ?",new String[]{String.valueOf(MainActivity.country)});
        if (cursor.moveToFirst()) {
            String updated = cursor.getString(cursor.getColumnIndex("updated"));
            if (updated == null || checkDateTime.days(updated) == 0 || checkDateTime.days(updated) >= 3) {
                if (internet.getStatus(context)) startJobService();
                else listener.parseConfig();
            } else listener.parseConfig();
        } else startJobService();
    }

    public String getConfigFile() {
        Log.d(MainActivity.LOG,"getConfigFile: получить uk.json");

        String result = "";
        try {
            InputStream stream = new FileInputStream(context.getExternalFilesDir(null)+File.separator+getLinkOfCountry()+".json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            result = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void exportDatabase() {
        if (!new File(folderBackup).isDirectory()) new File(folderBackup).mkdirs();
        copyFile(context.getDatabasePath("database.db").toString(),backupFile);
        Log.d(MainActivity.LOG,"exportDatabase: экспортировано");
    }

    private void copyFile(String input,String output) {
        if (permission.check()) {
            try (InputStream in = new FileInputStream(input)){
                try (OutputStream out = new FileOutputStream(output)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while((len = in.read(buf)) > 0) {
                        out.write(buf,0,len);
                    }

                    out.flush();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startJobService() {
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        int exist = jobScheduler.getAllPendingJobs().size();
        if (exist == 0) {
            ComponentName componentName = new ComponentName(context, MyJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(1,componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // wifi or mobile
                    .build();
            JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) Log.d(MainActivity.LOG, "checkSchedule: запланировано");
            else Log.d(MainActivity.LOG, "checkSchedule: не удалось запланировать");
        } else Log.d(MainActivity.LOG, "checkSchedule: было запланировано ранее");
    }

    private String getLinkOfCountry() {
        Cursor cursor = getWithArgs(Constant.TABLE_COUNTRY,"link","server = ?",new String[]{String.valueOf(param.getInt(Constant.PARAM_COUNTRY))});
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("link"));
        cursor.close();
        return result;
    }

}
