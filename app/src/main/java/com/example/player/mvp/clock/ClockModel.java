package com.example.player.mvp.clock;

import android.content.Context;
import android.database.Cursor;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.list.Item;
import com.example.player.basic.sqlite.CV;
import com.example.player.basic.sqlite.Model;
import com.example.player.mvp.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ClockModel extends Model {

    private final CV cv;

    public interface Model {
        void onSuccess(int id);
        void onDuplicate();
    }

    public ClockModel(Context context) {
        super(context);
        cv = new CV();
    }

    public void updateStatus(String datetime) {
        updateByWhere(Constant.TABLE_CLOCK,cv.status(0),"date <= '"+datetime+"' and status = 1");
    }

    public List<Item> getList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = getWithArgs(Constant.TABLE_CLOCK,"id,name,date,status","del = ? order by date desc",new String[]{String.valueOf(0)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            list.add(new Item(id,name,date.substring(0,date.length()-3), status == 1));
        }
        cursor.close();
        return list;
    }

    public List<Item> getAudioList() {
        List<Item> list = new ArrayList<>();
        Cursor cursor = getWithArgs(Constant.TABLE_MEDIA, "id,type,name,description,url,favourite", "country = ? and type = 1 and del = 0", new String[]{String.valueOf(MainActivity.country)});
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

    public void delete(int id) {
        updateById(Constant.TABLE_CLOCK,cv.delete(),id);
    }

    public void add(String name,int media,String date, Model listener) {
        if (duplicate(Constant.TABLE_CLOCK,"date = ?",new String[]{date},true)) {
            listener.onDuplicate();
        } else {
            int id = insertAndReplace(Constant.TABLE_CLOCK,cv.addClock(name,media,date));
            listener.onSuccess(id);
        }
    }

}
