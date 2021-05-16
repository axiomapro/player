package com.example.player.mvp.favourite;

import com.example.player.basic.list.Item;

import java.util.List;

public interface FavouriteContract {

    interface View {
        void updateItem(int position,boolean status);
    }

    interface Presenter {
        List<Item> getList();
        void toggleFavourite(int id,int position);
        void detach();
    }

}
