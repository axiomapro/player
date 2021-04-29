package com.example.player.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.player.basic.Dialog;
import com.example.player.mvp.view.MainActivity;
import com.example.player.mvp.contracct.MainContract;
import com.example.player.mvp.model.MainModel;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public void browser(String url,String message) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            view.showMessage(message);
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
}
