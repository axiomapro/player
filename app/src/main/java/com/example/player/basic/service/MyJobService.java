package com.example.player.basic.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.util.Log;

import com.example.player.basic.backend.ConfigJson;
import com.example.player.basic.config.Config;
import com.example.player.basic.sync.MyTask;
import com.example.player.mvp.main.MainActivity;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Param;
import com.example.player.basic.request.MyRequest;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ConfigJson configJson;
    private Map<String,String> map;
    private String link = Config.link().country();
    private int last = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(Config.log().basic(),"Job started");
        jobParameters = params;
        myRequest = new MyRequest(getApplicationContext());
        param = new Param(getApplicationContext());
        map = new HashMap<>();
        model = new Model(getApplicationContext());
        cv = new CV();
        configJson = new ConfigJson(getApplicationContext());
        send();
        return true; // другой поток
    }

    private void send() {
        map.clear();
        // last-update: country, category / date from country table
        String updated = null;
        if (link.equals(Config.link().country()) || link.equals(Config.link().category())) updated = param.getString(Config.param().updated());
        else {
            Cursor cursor = model.getWithArgs(Config.table().country(),"updated","server = ? and del = ?",new String[]{String.valueOf(param.getInt(Config.param().country())), String.valueOf(0)});
            if (cursor.moveToFirst()) updated = cursor.getString(cursor.getColumnIndex("updated"));
        }
        map.put("country", String.valueOf(Constant.country));
        map.put("updated", updated == null?"":updated);
        map.put("last", String.valueOf(last));
        myRequest.sendRequest(link, map, new MyRequest.VolleyRequest() {
            @Override
            public void onSuccess(String response) {
                success(response);
            }

            @Override
            public void onError(String message) {
                Log.d(Config.log().basic(),"error: "+message);
                if (MainActivity.activity != null) MainActivity.activity.showMessage("Сервер недоступен","jobService");
                jobFinished(jobParameters,false);
            }
        });
    }

    private void success(String response) {
        Log.d(Config.log().basic(),link+" response: "+response);
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
                    changeLink(link.equals(Config.link().media())?jsonObject.getString(Config.param().updated()):null,link.equals(Config.link().media()));
                }
                else jobFinished(jobParameters,false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeLink(String updated, boolean finalMedia) {
        Log.d(Config.log().basic(),"changeLink: "+link);
        if (link.equals(Config.link().country())) {
            last = 0;
            link = Config.link().category();
            send();
        }
        else if (link.equals(Config.link().category())) {
            last = 0;
            link = Config.link().media();
            send();
        }
        else if (link.equals(Config.link().media())) {
            if (finalMedia) {
                param.setString(Config.param().updated(),updated);
                model.updateByServer(Config.table().country(),cv.country(updated),param.getInt(Config.param().country()));
                link = Config.link().config();
                myRequest.sendRequest(link, map, new MyRequest.VolleyRequest() {
                    @Override
                    public void onSuccess(String response) {
                        Cursor cursor = model.getWithArgs(Config.table().country(),"link","server = ?",new String[]{String.valueOf(Constant.country)});
                        if (cursor.moveToFirst()) {
                            configJson.download(new MyRequest.VolleyRequest() {
                                @Override
                                public void onSuccess(String response) {
                                    configJson.update(cursor.getString(cursor.getColumnIndex("link")),response);
                                }

                                @Override
                                public void onError(String message) {
                                    if (MainActivity.activity != null) MainActivity.activity.showMessage("Сервер недоступен","jobService");
                                }
                            });
                        }
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

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(Config.log().basic(), "Job stopped");
        return true; // если прервутся условия, то пробуем по новому
    }

}