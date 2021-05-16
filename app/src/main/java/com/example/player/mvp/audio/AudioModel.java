package com.example.player.mvp.audio;

import android.content.Context;
import android.database.Cursor;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.config.Config;
import com.example.player.basic.list.Item;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;
import com.example.player.mvp.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioModel extends Model {

    private final CV cv;

    public AudioModel(Context context) {
        super(context);
        cv = new CV();
    }

    public List<Item> getList(int cat) {
        List<Item> list = new ArrayList<>();
        String sorting = "";
        if (cat > 0) sorting = "and cat = "+cat;
        Cursor cursor = getWithArgs(table, "id,type,name,description,url,favourite", "country = ? and type = 1 "+sorting+" and del = 0", new String[]{String.valueOf(Constant.country)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String desc = cursor.getString(cursor.getColumnIndex("description"));
            String url = cursor.getString(cursor.getColumnIndex("url"));
            String favourite = cursor.getString(cursor.getColumnIndex("favourite"));
            list.add(new Item(id, type, name, desc, url, favourite != null));
        }
        cursor.close();
        return list;
    }

    public boolean toggleFavourite(int id) {
        boolean status = false;
        Cursor cursor = getWithArgs(table, "favourite", "id = ? and del = 0", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String favourite = cursor.getString(cursor.getColumnIndex("favourite"));
            if (favourite != null) favourite = null;
            else {
                status = true;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                favourite = formatter.format(date);
            }

            updateById(table, cv.favourite(favourite), id);
        }
        cursor.close();
        return status;
    }

}
