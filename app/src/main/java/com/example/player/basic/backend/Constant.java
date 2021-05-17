package com.example.player.basic.backend;

import android.util.Log;

import com.example.player.basic.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class Constant {

    public static String screen;
    public static String textAbout;
    public static String textEmail;
    public static String textShareLink;
    public static String textRateLink;
    public static String textOther;
    public static String textBonus;
    public static String showMode;
    public static String appodealAppKey;
    public static String playBanner1;
    public static String playBanner2;
    public static String playBanner3;
    public static String playBanner4;
    public static String mainAd;
    public static String returnAd;
    public static String categoryButton;
    public static String clockBadge;
    public static String MainBanner;
    public static String MainNative;
    public static String FavBanner;
    public static String FavNative;
    public static String ClockBanner;
    public static String ClockNative;
    public static String OwnBanner;
    public static String OwnNative;
    public static String CntryBaner;
    public static String CntryAd;
    public static int country = 1;
    public static int visual;
    public static int nativeAdCnt;
    public static int returnMinute;

    public interface VisibleItems {
        void hideGroup(int group);
        void hideItem(String name);
    }

    public static void parse(String config, VisibleItems listener) {
        Log.d(Config.log().basic(),"Config: \n"+config);
        try {
            JSONObject json = new JSONObject(config);
            JSONObject jsonObject = json.getJSONObject("Config");
            boolean isClockTabVisible = jsonObject.getBoolean("is_clock_tab_visible");
            boolean isOwnTabVisible = jsonObject.getBoolean("is_own_tab_visible");
            boolean isChangeVisible = jsonObject.getBoolean("is_change_visible");
            textAbout = jsonObject.getString("text_about");
            textEmail = jsonObject.getString("text_email");
            textShareLink = jsonObject.getString("text_sharelink");
            textRateLink = jsonObject.getString("text_ratelink");
            textOther = jsonObject.getString("text_other");
            textBonus = jsonObject.getString("text_bonus");
            visual = Integer.parseInt(jsonObject.getString("vizual_effect"));
            nativeAdCnt = Integer.parseInt(jsonObject.getString("native_ad_cnt"));
            showMode = jsonObject.getString("show_mode");
            appodealAppKey = jsonObject.getString("YOUR_APPODEAL_APP_KEY");
            playBanner1 = jsonObject.getString("play-banner1");
            playBanner2 = jsonObject.getString("play-banner2");
            playBanner3 = jsonObject.getString("play-banner3");
            playBanner4 = jsonObject.getString("play-banner4");
            mainAd = jsonObject.getString("MainAd");
            returnAd = jsonObject.getString("ReturnAd");
            returnMinute = jsonObject.getInt("ReturnMinute");
            categoryButton = jsonObject.getString("CategoryButton");
            clockBadge = jsonObject.getString("ClockBadge");
            MainBanner = jsonObject.getString("MainBanner");
            MainNative = jsonObject.getString("MainNative");
            FavBanner = jsonObject.getString("FavBanner");
            FavNative = jsonObject.getString("FavNative");
            ClockBanner = jsonObject.getString("ClockBanner");
            ClockNative = jsonObject.getString("ClockNative");
            OwnBanner = jsonObject.getString("OwnBanner");
            OwnNative = jsonObject.getString("OwnNative");
            CntryBaner = jsonObject.getString("CntryBaner");
            CntryAd = jsonObject.getString("CntryAd");

            if (!isClockTabVisible) listener.hideGroup(1);
            if (!isOwnTabVisible) listener.hideGroup(2);
            if (!isChangeVisible) listener.hideGroup(3);
            if (textShareLink.equals("")) listener.hideItem("Share");
            if (textRateLink.equals("")) listener.hideItem("Rate");
            if (textOther.equals("")) listener.hideItem("Other apps");
            if (textBonus.equals("")) listener.hideItem("Bonus apps");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(Config.log().basic(),"Ошибка в парсинге config.json");
        }
    }

}
