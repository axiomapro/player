package com.example.player.mvp.presenter;

import com.example.player.basic.Dialog;
import com.example.player.basic.item.Item;
import com.example.player.mvp.view.FavouriteFragment;
import com.example.player.mvp.view.MainActivity;
import com.example.player.mvp.contracct.FavouriteContract;
import com.example.player.mvp.model.AudioModel;
import com.example.player.mvp.model.FavouriteModel;

import java.util.List;

public class FavouritePresenter implements FavouriteContract.Presenter {

    private FavouriteContract.View view;
    private final FavouriteModel model;
    private final Dialog dialog;

    public FavouritePresenter(FavouriteContract.View view) {
        this.view = view;
        this.model = new FavouriteModel(((FavouriteFragment) view).getContext());
        this.dialog = new Dialog(((FavouriteFragment) view).getContext());
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
