package com.example.player.basic.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.example.player.mvp.view.MainActivity;
import com.example.player.basic.Constant;
import com.example.player.basic.Param;
import com.example.player.basic.request.MyRequest;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MyJobService extends JobService {

    /*
        links: country, category, media, config/uk
        errors: forbidden link, result is empty (country, category, media), config not exist
    */

    private MyRequest myRequest;
    private Param param;
    private JobParameters jobParameters;
    private Model model;
    private CV cv;
    private Map<String,String> map;
    private String link = Constant.LINK_COUNTRY;
    private int last = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(MainActivity.LOG,"Job started");
        jobParameters = params;
        myRequest = new MyRequest(getApplicationContext());
        param = new Param(getApplicationContext());
        map = new HashMap<>();
        model = new Model(getApplicationContext());
        cv = new CV();
        send();
        return true; // другой поток
    }

    private void send() {
        map.clear();
        // last-update: country, category / date from country table
        String updated = null;
        if (link.equals(Constant.LINK_COUNTRY) || link.equals(Constant.LINK_CATEGORY)) updated = param.getString(Constant.PARAM_UPDATED);
        else {
            Cursor cursor = model.getWithArgs(Constant.TABLE_COUNTRY,"updated","server = ? and del = ?",new String[]{String.valueOf(param.getInt(Constant.PARAM_COUNTRY)), String.valueOf(0)});
            if (cursor.moveToFirst()) updated = cursor.getString(cursor.getColumnIndex("updated"));
        }
        map.put("country", String.valueOf(MainActivity.country));
        map.put("updated", updated == null?"":updated);
        map.put("last", String.valueOf(last));
        myRequest.sendRequest(link, map, new MyRequest.VolleyRequest() {
            @Override
            public void onSuccess(String response) {
                success(response);
            }

            @Override
            public void onError(String message) {
                Log.d(MainActivity.LOG,"error: "+message);
                jobFinished(jobParameters,false);
            }
        });
    }

    private void success(String response) {
        Log.d(MainActivity.LOG,link+" response: "+response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean status = jsonObject.getBoolean("status");
            if (status) {
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                JSONObject column = jsonArray.getJSONObject(jsonArray.length() - 1);
                last = column.getInt("id");
                new MyTask(getApplicationContext(), link, new MyTask.AsyncTask() {
                    @Override
                    public void onTaskFinish() {
                        changeLink(null,false);
                    }
                }).execute(jsonObject);
            }
            else {
                String message = jsonObject.getString("message");
                if (message.equals("result is empty")) {
                    changeLink(jsonObject.getString(Constant.PARAM_UPDATED),true);
                }
                else jobFinished(jobParameters,false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeLink(String updated, boolean finalMedia) {
        Log.d(MainActivity.LOG,"changeLink: "+link);
        if (link.equals(Constant.LINK_COUNTRY)) {
            last = 0;
            link = Constant.LINK_CATEGORY;
            send();
        }
        else if (link.equals(Constant.LINK_CATEGORY)) {
            last = 0;
            link = Constant.LINK_MEDIA;
            send();
        }
        else if (link.equals(Constant.LINK_MEDIA)) {
            if (finalMedia) {
                param.setString(Constant.PARAM_UPDATED,updated);
                model.updateByServer(Constant.TABLE_COUNTRY,cv.country(updated),param.getInt(Constant.PARAM_COUNTRY));
                link = Constant.LINK_CONFIG;
                myRequest.sendRequest(link, map, new MyRequest.VolleyRequest() {
                    @Override
                    public void onSuccess(String response) {
                        updateConfig(response);
                        jobFinished(jobParameters,false);
                    }

                    @Override
                    public void onError(String message) {
                        jobFinished(jobParameters,false);
                    }
                });
            } else send();
        }
    }

    private void updateConfig(String response) {
        try {
            String json = new JSONObject(response).getString("result");
            byte[] result = Base64.decode(json,Base64.DEFAULT);
            Cursor cursor = model.getWithArgs(Constant.TABLE_COUNTRY,"link","server = ?",new String[]{String.valueOf(MainActivity.country)});
            if (cursor.moveToFirst()) {
                Log.d(MainActivity.LOG,"config copied: "+cursor.getString(cursor.getColumnIndex("link")));
                OutputStream output = new FileOutputStream(getApplicationContext().getExternalFilesDir(null)+"/"+cursor.getString(cursor.getColumnIndex("link"))+".json");
                output.write(result,0,result.length);
                output.flush();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(MainActivity.LOG, "Не удалось копировать");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.LOG,"Не удалось получить json: "+response);
        }
        if (MainActivity.activity != null) MainActivity.activity.initAudioFragment();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(MainActivity.LOG, "Job stopped");
        return true; // если прервутся условия, то пробуем по новому
    }

}