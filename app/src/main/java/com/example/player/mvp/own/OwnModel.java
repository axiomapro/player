package com.example.player.mvp.own;

import android.content.Context;
import android.database.Cursor;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.list.Item;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;
import com.example.player.mvp.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OwnModel extends Model {

    private final CV cv;

    public interface Model {
        void onSuccess(int id);
        void onDuplicate();
    }

    public OwnModel(Context context) {
        super(context);
        cv = new CV();
    }

    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = getWithArgs(Constant.TABLE_MEDIA,"id,type,name,description,url,favourite","country = ? and uid = 1 and type = 1 and del = 0 order by date desc",new String[]{String.valueOf(MainActivity.country)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String desc = cursor.getString(cursor.getColumnIndex("description"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            String favourite = cursor.getString(cursor.getColumnIndex("favourite"));
            list.add(new Item(id,type,name,desc,url, favourite != null));
        }
        cursor.close();
        return list;
    }

    public void delete(int id) {
        updateById(Constant.TABLE_MEDIA,cv.delete(),id);
    }

    public boolean toggleFavourite(int id) {
        boolean status = false;
        Cursor cursor = getWithArgs(Constant.TABLE_MEDIA,"favourite","id = ? and del = 0",new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String favourite = cursor.getString(cursor.getColumnIndex("favourite"));
            if (favourite != null) favourite = null;
            else {
                status = true;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                favourite = formatter.format(date);
            }

            updateById(Constant.TABLE_MEDIA,cv.favourite(favourite),id);
        }
        cursor.close();
        return status;
    }

    public void add(String name, String url,Model listener) {
        if (duplicate(Constant.TABLE_MEDIA,"url = ?",new String[]{url},true)) {
            listener.onDuplicate();
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String datetime = formatter.format(date);
            int id = insertAndReplace(Constant.TABLE_MEDIA,cv.addMedia(0,1,MainActivity.country,1,0,name,null,url,datetime,0));
            listener.onSuccess(id);
        }
    }
}
