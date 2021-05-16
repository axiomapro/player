package com.example.player.mvp.country;

import android.content.Context;
import android.os.Handler;

import com.example.player.basic.backend.Internet;
import com.example.player.basic.list.Item;

import java.util.List;

public class CountryPresenter implements CountryContract.Presenter {

    private CountryContract.View view;
    private final Context context;
    private final CountryModel model;

    public CountryPresenter(CountryContract.View view) {
        this.view = view;
        this.context = (CountryActivity) view;
        this.model = new CountryModel(context);
    }

    @Override
    public void getList() {
        List<Item> list;
        Internet internet = new Internet();
        if (internet.getStatus(context)) {
            model.sendRequest();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.initRecyclerView(model.getList());
                }
            },3000);
        } else {
            list = model.getList();
            if (list.size() > 0) view.initRecyclerView(list);
            else view.showMessage("Включите WiFi");
        }
    }

    @Override
    public void saveCountry(int id) {
        model.saveCountry(context,id);
        view.next();
    }

    @Override
    public void detach() {
        view = null;
    }
}
