package com.example.player.basic.sqlite;

import android.content.ContentValues;
import android.content.Context;

public class CV {

    private ContentValues cv;

    public CV() {
        cv = new ContentValues();
    }

    public ContentValues country(String updated) {
        cv.clear();
        cv.put("updated",updated);
        return cv;
    }

    public ContentValues addCountry(int server, String name, String link, String img, String categories,int del) {
        cv.clear();
        cv.put("server",server);
        cv.put("name",name);
        cv.put("link",link);
        cv.put("img",img);
        cv.put("categories",categories);
        cv.put("del",del);
        return cv;
    }

    public ContentValues editCountry(String name, String link, String img, String categories, int del) {
        cv.clear();
        cv.put("name",name);
        cv.put("link",link);
        cv.put("img",img);
        cv.put("categories",categories);
        cv.put("del",del);
        return cv;
    }

    public ContentValues editCategory(String name, int sort, int del) {
        cv.clear();
        cv.put("name",name);
        cv.put("sort",sort);
        cv.put("del",del);
        return cv;
    }

    public ContentValues addCategory(int server, String name, int sort, int del) {
        cv.clear();
        cv.put("server",server);
        cv.put("name",name);
        cv.put("sort",sort);
        cv.put("del",del);
        return cv;
    }

    public ContentValues addMedia(int server, int uid, int country, int type, int cat, String name, String description, String url, String date, int del) {
        cv.clear();
        cv.put("server",server);
        cv.put("uid",uid);
        cv.put("country",country);
        cv.put("type",type);
        cv.put("cat",cat);
        cv.put("name",name);
        cv.put("description",description);
        cv.put("url",url);
        cv.put("date",date);
        cv.put("favourite",(String) null);
        cv.put("del",del);
        return cv;
    }

    public ContentValues editMedia(int country, int type, int cat, String name, String description, String url, String date, int favourite) {
        cv.clear();
        cv.put("country",country);
        cv.put("type",type);
        cv.put("cat",cat);
        cv.put("name",name);
        cv.put("description",description);
        cv.put("url",url);
        cv.put("date",date);
        cv.put("favourite",favourite);
        return cv;
    }

    public ContentValues addClock(String name,int media,String date) {
        cv.clear();
        cv.put("name",name);
        cv.put("media",media);
        cv.put("date",date);
        cv.put("status",1);
        cv.put("del",0);
        return cv;
    }

    public ContentValues favourite(String favourite) {
        cv.clear();
        cv.put("favourite",favourite);
        return cv;
    }

    public ContentValues delete() {
        cv.clear();
        cv.put("del",1);
        return cv;
    }

    public ContentValues status(int status) {
        cv.clear();
        cv.put("status",status);
        return cv;
    }
}
