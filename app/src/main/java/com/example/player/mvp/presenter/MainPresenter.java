package com.example.player.mvp.presenter;

import android.content.Context;

import com.example.player.mvp.view.MainActivity;
import com.example.player.mvp.contracct.MainContract;
import com.example.player.mvp.model.MainModel;

import org.json.JSONException;
import org.json.JSONObject;

public class MainPresenter implements MainContract.Presenter, MainModel.ModelListener {

    private MainContract.View view;
    private final Context context;
    private final MainModel model;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.context = (MainActivity) view;
        this.model = new MainModel(context,this);
    }

    @Override
    public void restoreDatabase() {
        model.restoreDatabase();
    }

    @Override
    public void sendRequest() {
        model.sendRequest();
    }

    @Override
    public void parseConfig() {
        view.initAudioFragment();
        view.parseConfig(model.getConfigFile());
    }

    @Override
    public void exportDatabase() {
        model.exportDatabase();
    }

    @Override
    public void detach() {
        view = null;
    }
}
