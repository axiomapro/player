package com.example.player.basic.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.player.mvp.view.MainActivity;
import com.example.player.basic.Constant;

import java.util.Map;

public class MyRequest {

    private final Context context;

    public interface VolleyRequest {
        void onSuccess(String response);
        void onError(String message);
    }

    public MyRequest(Context context) {
        this.context = context;
    }

    public void sendRequest(final String from, final Map<String, String> params, VolleyRequest listener) {
        final String url = Constant.JSON+"/"+from;
        Log.d(MainActivity.LOG,"url: "+url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}
