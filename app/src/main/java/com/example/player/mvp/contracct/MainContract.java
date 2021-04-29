package com.example.player.mvp.contracct;

public interface MainContract {

    interface View {
        void initAudioFragment();
        void parseConfig(String json);
    }

    interface Presenter {
        void restoreDatabase();
        void sendRequest();
        void parseConfig();
        void exportDatabase();
        void detach();
    }

}
