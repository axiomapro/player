package com.example.player.mvp.contracct;

import com.example.player.basic.item.Item;

import java.util.List;

public interface AudioContract {

    interface View {
        void updateItem(int position,boolean status);
        void removeItem(int position);
    }

    interface Presenter {
        List<Item> getList();
        void toggleFavourite(int id,int position);
        void showDeleteDialog(int id,int position);
        void detach();
    }

}
