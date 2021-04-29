package com.example.player.mvp.presenter;

import com.example.player.basic.Dialog;
import com.example.player.basic.item.Item;
import com.example.player.mvp.view.AudioFragment;
import com.example.player.mvp.view.MainActivity;
import com.example.player.mvp.contracct.AudioContract;
import com.example.player.mvp.model.AudioModel;

import java.util.List;

public class AudioPresenter implements AudioContract.Presenter {

    private AudioContract.View view;
    private final AudioModel model;
    private final Dialog dialog;

    public AudioPresenter(AudioContract.View view) {
        this.view = view;
        this.model = new AudioModel(((AudioFragment) view).getContext());
        this.dialog = new Dialog(((AudioFragment) view).getContext());
    }

    @Override
    public List<Item> getList() {
        return model.getList();
    }

    @Override
    public void toggleFavourite(int id,int position) {
        view.updateItem(position,model.toggleFavourite(id));
    }

    @Override
    public void showDeleteDialog(int id,int position) {
        dialog.delete(new Dialog.Delete() {
            @Override
            public void yes() {
                model.delete(id);
                view.removeItem(position);
            }
        });
    }

    @Override
    public void detach() {
        view = null;
    }
}
