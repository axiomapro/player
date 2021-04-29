package com.example.player.basic.sync;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class MyTask extends AsyncTask<JSONObject,Void,Void> {

    private final AsyncTask listener;
    private final Sync sync;
    private final String from;

    public interface AsyncTask {
        void onTaskFinish();
    }

    public MyTask(Context context, String from, AsyncTask listener) {
        this.sync = new Sync(context);
        this.from = from;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(JSONObject... jsonObjects) {
        JSONObject jsonObject = jsonObjects[0];
        try {
            sync.parse(from,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskFinish();
    }
}