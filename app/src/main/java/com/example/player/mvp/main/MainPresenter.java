package com.example.player.mvp.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.player.R;
import com.example.player.basic.backend.ConfigJson;
import com.example.player.basic.backend.Dialog;
import com.example.player.basic.list.Item;
import com.example.player.basic.request.MyRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainContract.Presenter, MainModel.ModelListener {

    private MainContract.View view;
    private final Context context;
    private final MainModel model;
    private final Dialog dialog;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.context = (MainActivity) view;
        this.model = new MainModel(context,this);
        this.dialog = new Dialog(context);
    }

    @Override
    public void restoreDatabase() {
        model.restoreDatabase();
    }

    @Override
    public void parseConfig(String link) {
        view.initAudioFragment();
        File config = new File(context.getExternalFilesDir(null)+"/"+link+".json");
        if (!config.exists()) {
            ConfigJson configJson = new ConfigJson(context);
            configJson.download(new MyRequest.VolleyRequest() {
                @Override
                public void onSuccess(String response) {
                    configJson.update(link,response);
                    view.parseConfig(model.getConfigFile());
                }

                @Override
                public void onError(String message) {

                }
            });
        } else view.parseConfig(model.getConfigFile());
    }

    @Override
    public void exportDatabase() {
        model.exportDatabase();
    }

    @Override
    public void detach() {
        view = null;
    }

    @Override
    public void browser(String url,String message) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            view.showMessage(message,"mainPresenter");
        }
    }

    @Override
    public void share(String title,String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "Поделиться"));
    }

    @Override
    public void showAboutDialog(String message,String email) {
        dialog.about(message,email);
    }

    @Override
    public boolean isVisibleSortingIcon() {
        return model.checkSortingItems();
    }

    @Override
    public List<Item> getListMenu() {
        return model.getListMenu();
    }

    @Override
    public List<Item> getListSorting() {
        List<Item> list = model.getListSorting();
        view.setSortingActivePosition(model.getSortingActivePosition());
        return list;
    }
}
