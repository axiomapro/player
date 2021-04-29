package com.example.player.basic;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.example.player.basic.request.MyRequest;
import com.example.player.mvp.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigJson {

    private Context context;

    public ConfigJson(Context context) {
        this.context = context;
    }

    public void download(MyRequest.VolleyRequest listener) {
        MyRequest myRequest = new MyRequest(context);
        Map<String,String> map = new HashMap<>();
        map.put("country", String.valueOf(MainActivity.country));
        myRequest.sendRequest("config",map,listener);
    }

    public void update(String linkCountry, String response) {
        try {
            String json = new JSONObject(response).getString("result");
            byte[] result = Base64.decode(json,Base64.DEFAULT);
            Log.d(MainActivity.LOG,"config copied: "+linkCountry);
            OutputStream output = new FileOutputStream(context.getExternalFilesDir(null)+"/"+linkCountry+".json");
            output.write(result,0,result.length);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(MainActivity.LOG, "Не удалось копировать");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(MainActivity.LOG,"Не удалось получить json: "+response);
        }
        if (MainActivity.activity != null) MainActivity.activity.initAudioFragment();
    }

}
