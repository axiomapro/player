package com.example.player.mvp.contracct;

public interface MainContract {

    interface View {
        void initAudioFragment();
        void parseConfig(String json);
        void showMessage(String message);
    }

    interface Presenter {
        void restoreDatabase();
        void sendRequest();
        void parseConfig();
        void exportDatabase();
        void detach();
        void browser(String url,String message);
        void share(String title,String text);
        void showAboutDialog(String message,String email);
    }

}
