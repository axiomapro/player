package com.example.player.mvp.country;

import com.example.player.basic.list.Item;

import java.util.List;

public interface CountryContract {

    interface View {
        void initRecyclerView(List<Item> list);
        void showMessage(String message);
        void next();
    }

    interface Presenter {
        void getList();
        void saveCountry(int id);
        void detach();
    }

}
