package com.example.player.basic.sync;

import android.content.Context;
import android.database.Cursor;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sync extends Model {

    private final CV cv;

    public Sync(Context context) {
        super(context);
        cv = new CV();
    }

    private void media(JSONObject column) throws JSONException {
        // server: id, country, type, cat, name, description, url, date, del
        // mobile: id, server, uid, country, type, cat, name, description, url, date, favourite, del
        int server = column.getInt("id");
        int country = column.getInt("country");
        int type = column.getInt("type");
        int cat = column.getInt("cat");
        int del = column.getInt("del");
        String name = column.getString("name");
        String description = column.getString("description");
        String url = column.getString("url");
        String date = column.getString("date");

        Cursor cursor = get(Constant.TABLE_MEDIA,"favourite,date","server = "+server);
        if (cursor.moveToFirst()) {
            int favouriteMedia = cursor.getInt(cursor.getColumnIndex("favourite"));
            String dateMedia = cursor.getString(cursor.getColumnIndex("date"));
            if (del == 0) {
                if (!dateMedia.equals(date)) favouriteMedia = 0;
                updateByServer(Constant.TABLE_MEDIA,cv.editMedia(country,type,cat,name,description,url,date,favouriteMedia),server);
            }
            else updateByServer(Constant.TABLE_MEDIA,cv.delete(),server);
        }
        else if (del == 0) insertAndReplace(Constant.TABLE_MEDIA,cv.addMedia(server,0, country, type, cat, name, description, url, date, del));
        cursor.close();
    }

    private void category(JSONObject column) throws JSONException {
        // server: id, name, sort, del
        // mobile: id, server, name, sort, del
        int server = column.getInt("id");
        int sort = column.getInt("sort");
        int del = column.getInt("del");
        String name = column.getString("name");

        if (duplicate(Constant.TABLE_CATEGORY,"server = ?",new String[]{String.valueOf(server)},false)) updateByServer(Constant.TABLE_CATEGORY,cv.editCategory(name,sort,del),server);
        else if (del == 0) insertAndReplace(Constant.TABLE_CATEGORY,cv.addCategory(server,name,sort,del));
    }

    private void country(JSONObject column) throws JSONException {
        // server: id, name, link, img, categories, del
        // mobile: id, server, name, img, categories updated, del
        int server = column.getInt("id");
        int del = column.getInt("del");
        String name = column.getString("name");
        String link = column.getString("link");
        String img = column.getString("img");
        String categories = column.getString("categories");

        if (duplicate(Constant.TABLE_COUNTRY,"server = ?",new String[]{String.valueOf(server)},false)) updateByServer(Constant.TABLE_COUNTRY,cv.editCountry(name,link,img,categories,del),server);
        else if (del == 0) insertAndReplace(Constant.TABLE_COUNTRY,cv.addCountry(server,name,link,img,categories,0));
    }

    public void parse(String from,JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject column = jsonArray.getJSONObject(i);
            if (from.equals(Constant.LINK_COUNTRY)) country(column);
            else if (from.equals(Constant.LINK_CATEGORY)) category(column);
            else if (from.equals(Constant.LINK_MEDIA)) media(column);
        }
    }

}
