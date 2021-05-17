package com.example.player.basic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Model {

    protected DatabaseHelper databaseHelper;
    protected Parameter parameter;

    public Model(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
        parameter = new Parameter();
    }

    public int insertAndReplace(String table, ContentValues cv) {
        int result;
        Cursor cursor = databaseHelper.getDb().rawQuery("select id from "+table+" where del = 1",null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("id"));
            databaseHelper.update(table,cv,"id = "+result);
        } else {
            result = databaseHelper.insert(table,cv);
        }
        cursor.close();
        return result;
    }

    public int count(String table,String where,boolean del) {
        String sortLang = "";
        String sortDel = "";
        if (del) sortDel = " and del = 0";
        return databaseHelper.count("select id from "+table+" where "+where+sortLang+sortDel);
    }

    public Cursor getBySql(String sql) {
        return databaseHelper.getDb().rawQuery(sql,null);
    }

    public Cursor get(String table, String column, String where) {
        return databaseHelper.getDb().rawQuery("select "+column+" from "+table+" where "+where,null);
    }

    public Cursor getWithArgs(String table, String column, String where,String[] args) {
        return databaseHelper.getDb().rawQuery("select "+column+" from "+table+" where "+where,args);
    }

    protected boolean duplicate(String table,String where,String[] args,boolean del) {
        boolean result = true;
        String sortDel = "";
        if (del) sortDel = " and del = 0";
        Cursor cursor = databaseHelper.getDb().rawQuery("select count(1) as total from "+table+" where "+where+sortDel,args);
        cursor.moveToFirst();
        int total = cursor.getInt(cursor.getColumnIndex("total"));
        if (total == 0) result = false;
        cursor.close();
        return result;
    }

    public void updateByWhere(String table,ContentValues cv,String where) {
        databaseHelper.update(table,cv,where);
    }

    public void updateById(String table,ContentValues cv,int id) {
        databaseHelper.update(table,cv,"id = "+id);
    }

    public void updateByServer(String table,ContentValues cv,int server) {
        databaseHelper.update(table,cv,"server = "+server);
    }

    public void updateByImage(String table,ContentValues cv,int image) {
        databaseHelper.update(table,cv,"image = "+image);
    }
}
