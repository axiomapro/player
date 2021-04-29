package com.example.player.mvp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;

import com.example.player.basic.Constant;
import com.example.player.basic.Dialog;
import com.example.player.basic.Param;
import com.example.player.basic.item.Item;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;
import com.example.player.mvp.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavouriteModel extends Model {

    private final CV cv;

    public FavouriteModel(Context context) {
        super(context);
        cv = new CV();
    }

    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = getWithArgs(Constant.TABLE_MEDIA,"id,type,name,url","country = ? and type = 1 and favourite is not null and del = 0",new String[]{String.valueOf(MainActivity.country)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            list.add(new Item(id,type,name,url, true));
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

}
