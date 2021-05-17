package com.example.player.basic.sqlite;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Parameter {

    public String getDatetime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

}
