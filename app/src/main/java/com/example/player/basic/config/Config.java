package com.example.player.basic.config;

public class Config {

    private static ScreenConfig screenConfig;
    private static TableConfig tableConfig;
    private static RecyclerViewConfig recyclerViewConfig;
    private static ParamConfig paramConfig;
    private static LogConfig logConfig;
    private static LinkConfig linkConfig;
    private static URLConfig urlConfig;
    private static AdConfig adConfig;

    public static ScreenConfig screen() {
        if (screenConfig == null) screenConfig = new ScreenConfig();
        return screenConfig;
    }

    public static TableConfig table() {
        if (tableConfig == null) tableConfig = new TableConfig();
        return tableConfig;
    }

    public static RecyclerViewConfig recyclerView() {
        if (recyclerViewConfig == null) recyclerViewConfig = new RecyclerViewConfig();
        return recyclerViewConfig;
    }

    public static ParamConfig param() {
        if (paramConfig == null) paramConfig = new ParamConfig();
        return paramConfig;
    }

    public static LogConfig log() {
        if (logConfig == null) logConfig = new LogConfig();
        return logConfig;
    }

    public static LinkConfig link() {
        if (linkConfig == null) linkConfig = new LinkConfig();
        return linkConfig;
    }

    public static URLConfig url() {
        if (urlConfig == null) urlConfig = new URLConfig();
        return urlConfig;
    }

    public static AdConfig ad() {
        if (adConfig == null) adConfig = new AdConfig();
        return adConfig;
    }

}
