package com.example.player.mvp.contracct;

import com.example.player.basic.item.Item;

import java.util.List;

public interface OwnContract {

    interface View {
        void showMessage(String message);
        void updateItem(int position,boolean status);
        void removeItem(int position);
        void addItem(Item item);
    }

    interface Presenter {
        List<Item> getList();
        void toggleFavourite(int id,int position);
        void showDeleteDialog(int id,int position);
        void showAddDialog();
        void detach();
    }

}
