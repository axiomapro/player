package com.example.player.mvp.audio;

import com.example.player.basic.list.Item;

import java.util.List;

public interface AudioContract {

    interface View {
        void updateItem(int position,boolean status);
    }

    interface Presenter {
        List<Item> getList(int cat);
        void toggleFavourite(int id,int position);
        void detach();
    }

}
