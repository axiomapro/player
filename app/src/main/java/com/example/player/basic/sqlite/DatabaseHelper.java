package com.example.player.basic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static SQLiteDatabase db;
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static boolean isOpenDb;

    // Singleton
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context);
        return instance;
    }

    // Конструктор класса
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= 28) db.disableWriteAheadLogging();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table `country`(id integer primary key autoincrement, server integer, name text, link text, img text, categories text, updated text, del integer);");
        db.execSQL("create table `category`(id integer primary key autoincrement, server integer, name text, sort integer, del integer);");
        db.execSQL("create table `media`(id integer primary key autoincrement, server integer, uid integer, country integer, type integer, cat integer, name text, url text, date text, favourite text, del integer);");
        db.execSQL("create table `clock`(id integer primary key autoincrement, name text, media integer, date text, status integer, del integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Посчитать записи
    public int count(String sql) {
        openDb();
        Cursor cursor = db.rawQuery(sql,null);
        int total = cursor.getCount();
        cursor.close();
        return total;
    }

    // Обновить запись в таблице
    public void update(String table, ContentValues cv, String where) {
        openDb();
        db.update(table, cv, where, null);
    }

    // Добавить запись в таблицу
    public int insert(String table,ContentValues cv) {
        openDb();
        long id = db.insert(table,null,cv);
        return Integer.parseInt(String.valueOf(id));
    }

    public SQLiteDatabase getDb() { // upgrade
        openDb();
        return db;
    }

    // Открыть соединение с базой данных
    public void openDb() {
        if (!isOpenDb) {
            db = instance.getWritableDatabase();
            isOpenDb = true;
        }
    }

    public boolean isOpenDb() {
        return isOpenDb;
    }
}
