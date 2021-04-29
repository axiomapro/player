package com.example.player.mvp.contracct;

import com.example.player.basic.item.Item;

import java.util.List;

public interface ClockContract {

    interface View {
        void showMessage(String message);
        void removeItem(int position);
        void addItem(Item item);
    }

    interface Presenter {
        List<Item> getList();
        void updateStatusClock();
        void showDeleteDialog(int id,int position);
        void showAddDialog();
        void detach();
    }

}
