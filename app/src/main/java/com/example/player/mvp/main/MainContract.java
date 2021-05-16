package com.example.player.mvp.main;

import com.example.player.basic.list.Item;

import java.util.List;

public interface MainContract {

    interface View {
        void initAudioFragment();
        void parseConfig(String json);
        void showMessage(String message,String from);
        void setSortingActivePosition(int position);
    }

    interface Presenter {
        void restoreDatabase();
        void parseConfig(String link);
        void exportDatabase();
        void detach();
        void browser(String url,String message);
        void share(String title,String text);
        void showAboutDialog(String message,String email);
        boolean isVisibleSortingIcon();
        List<Item> getListMenu();
        List<Item> getListSorting();
    }

}
