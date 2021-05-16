package com.example.player.mvp.main;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.DateTimeManager;
import com.example.player.basic.backend.Internet;
import com.example.player.basic.backend.Param;
import com.example.player.basic.backend.Permission;
import com.example.player.basic.config.Config;
import com.example.player.basic.list.Item;
import com.example.player.basic.sqlite.DatabaseHelper;
import com.example.player.basic.sqlite.Model;
import com.example.player.basic.service.MyJobService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class MainModel extends Model {

    private final ModelListener listener;
    private final Context context;
    private final Param param;
    private final Permission permission;
    private final Internet internet;
    private final DateTimeManager checkDateTime;
    private final DatabaseHelper databaseHelper;
    private String folderBackup = Environment.getExternalStorageDirectory()+"/MediaPlayer";
    private String backupFile = folderBackup+"/database.db";
    private String link;
    private int sortingActivePosition;

    public interface ModelListener {
        void parseConfig(String link);
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

    public List<Item> getListMenu() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(0,0,"Audio", R.drawable.ic_menu_audio,false,true));
        list.add(new Item(0,0,"Favourite",R.drawable.ic_menu_favourite,false,true));
        list.add(new Item(0,1,null,0,false,true));
        list.add(new Item(0,1,"Clock",R.drawable.ic_menu_clock,false,true));
        list.add(new Item(0,1,"Add clock",R.drawable.ic_menu_add,false,true));
        list.add(new Item(0,2,null,0,false,true));
        list.add(new Item(0,2,"Own",R.drawable.ic_menu_own,false,true));
        list.add(new Item(0,2,"Add own",R.drawable.ic_menu_add,false,true));
        list.add(new Item(0,3,null,0,false,true));
        list.add(new Item(0,3,"Change",R.drawable.ic_menu_change,false,true));
        list.add(new Item(0,0,null,0,false,true));
        list.add(new Item(0,0,"About",R.drawable.ic_menu_about,false,true));
        list.add(new Item(0,0,"Share",R.drawable.ic_menu_share,false,true));
        list.add(new Item(0,0,"Rate",R.drawable.ic_menu_rate,false,true));
        list.add(new Item(0,0,"Other apps",R.drawable.ic_menu_other_apps,false,true));
        list.add(new Item(0,0,"Bonus apps",R.drawable.ic_menu_bonus_apps,false,true));
        return list;
    }

    public List<Item> getListSorting() {
        int sorting = param.getInt(Config.param().sorting());
        List<Item> list = new ArrayList<>();
        Cursor cursorCategories = get(Config.table().country(),"categories","id = "+Constant.country);
        if (cursorCategories.moveToFirst()) {
            String categories = cursorCategories.getString(cursorCategories.getColumnIndex("categories")).substring(1);
            Cursor cursor = get(Config.table().category(),"id,name","id in ("+categories+")");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                list.add(new Item(id,0,name,0,id == sorting,true));
                if (id == sorting) sortingActivePosition = list.size();
            }
            cursor.close();
        }
        cursorCategories.close();

        if (list.size() > 0) list.add(0,new Item(0,0,"All",0,sorting == 0,true));
        return list;
    }

    public boolean checkSortingItems() {
        return getListSorting().size() > 0;
    }

    public void restoreDatabase() {
        if (param.getBoolean(Config.param().checkImportDb())) {
            Log.d(Config.log().basic(),"restoreDatabase: это не первый запуск");
            sendRequest();
        } else {
            if (permission.check()) {
                Log.d(Config.log().basic(),"restoreDatabase: разрешение получено");
                param.setBoolean(Config.param().checkImportDb(),true);
                File backup = new File(backupFile);
                if (backup.exists()) {
                    Log.d(Config.log().basic(),"restoreDatabase: импортировано");
                    copyFile(backupFile,context.getDatabasePath("database.db").toString());
                    Cursor cursor = getWithArgs(Config.table().country(),"updated","del = ? order by updated desc limit 1",new String[]{String.valueOf(param.getInt(Config.param().country()))});
                    if (cursor.moveToFirst()) param.setString(Config.param().updated(),cursor.getString(cursor.getColumnIndex("updated")));
                    cursor.close();
                }
                sendRequest();
            } else {
                Log.d(Config.log().basic(),"restoreDatabase: покажи окно разрешения");
                permission.show();
            }
        }
    }

    public void sendRequest() {
        Log.d(Config.log().basic(),"sendRequest");
        Cursor cursor = getWithArgs(Config.table().country(),"link,updated","server = ?",new String[]{String.valueOf(Constant.country)});
        cursor.moveToFirst();
        link = cursor.getString(cursor.getColumnIndex("link"));
        String updated = cursor.getString(cursor.getColumnIndex("updated"));
        if ((updated == null || checkDateTime.days(updated) >= 0)) {
            if (internet.getStatus(context)) startJobService();
            if (updated != null) listener.parseConfig(link);
            else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = getWithArgs(Config.table().country(),"link,updated","server = ?",new String[]{String.valueOf(Constant.country)});
                        if (cursor.moveToFirst()) listener.parseConfig(cursor.getString(cursor.getColumnIndex("link")));
                        cursor.close();
                    }
                },4000);
            }
        } else listener.parseConfig(link);
        cursor.close();
    }

    public String getConfigFile() {
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
        Log.d(Config.log().basic(),"exportDatabase: экспортировано");
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
            if (resultCode == JobScheduler.RESULT_SUCCESS) Log.d(Config.log().basic(), "checkSchedule: запланировано");
            else Log.d(Config.log().basic(), "checkSchedule: не удалось запланировать");
        } else Log.d(Config.log().basic(), "checkSchedule: было запланировано ранее");
    }

    private String getLinkOfCountry() {
        Cursor cursor = getWithArgs(Config.table().country(),"link","server = ?",new String[]{String.valueOf(param.getInt(Config.param().country()))});
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("link"));
        cursor.close();
        return result;
    }

    public int getSortingActivePosition() {
        return sortingActivePosition;
    }
}
