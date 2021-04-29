package com.example.player.basic.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.example.player.mvp.view.MainActivity;
import com.example.player.basic.Constant;
import com.example.player.basic.Param;
import com.example.player.basic.request.MyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyCountryJobService extends JobService {

    private MyRequest myRequest;
    private JobParameters jobParameters;
    private Param param;
    private Map<String, String> map;
    private final String link = Constant.LINK_COUNTRY;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(MainActivity.LOG, "Job started");
        jobParameters = params;
        myRequest = new MyRequest(getApplicationContext());
        map = new HashMap<>();
        param = new Param(getApplicationContext());
        send();
        return true; // другой поток
    }

    private void send() {
        map.clear();
        String updated = param.getString(Constant.PARAM_UPDATED);
        map.put("updated", updated == null ? "" : updated);
        myRequest.sendRequest(link, map, new MyRequest.VolleyRequest() {
            @Override
            public void onSuccess(String response) {
                success(response);
            }

            @Override
            public void onError(String message) {
                Log.d(MainActivity.LOG, "error: " + message);
                jobFinished(jobParameters, false);
            }
        });
    }

    private void success(String response) {
        Log.d(MainActivity.LOG, link + " response: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean status = jsonObject.getBoolean("status");
            if (status) {
                new MyTask(getApplicationContext(), link, new MyTask.AsyncTask() {
                    @Override
                    public void onTaskFinish() {
                        jobFinished(jobParameters, false);
                    }
                }).execute(jsonObject);
            } else jobFinished(jobParameters,false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(MainActivity.LOG, "Job stopped");
        return true; // если прервутся условия, то пробуем по новому
    }
}
