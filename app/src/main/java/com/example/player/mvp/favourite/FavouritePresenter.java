package com.example.player.mvp.favourite;

import com.example.player.basic.list.Item;

import java.util.List;

public class FavouritePresenter implements FavouriteContract.Presenter {

    private FavouriteContract.View view;
    private final FavouriteModel model;

    public FavouritePresenter(FavouriteContract.View view) {
        this.view = view;
        this.model = new FavouriteModel(((FavouriteFragment) view).getContext());
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
    public void detach() {
        view = null;
    }
}
