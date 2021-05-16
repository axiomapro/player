package com.example.player.basic.sqlite;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.config.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Parameter {

    public String getTableByScreen() {
        String result = null;
        if (Constant.screen.equals(Config.screen().country())) result = Config.table().country();
        if (Constant.screen.equals(Config.screen().audio())) result = Config.table().media();
        if (Constant.screen.equals(Config.screen().favourite())) result = Config.table().media();
        if (Constant.screen.equals(Config.screen().clock())) result = Config.table().clock();
        if (Constant.screen.equals(Config.screen().own())) result = Config.table().media();
        return result;
    }

    public String getDatetime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

}
