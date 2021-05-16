package com.example.player.mvp.audio;

import com.example.player.basic.list.Item;

import java.util.List;

public class AudioPresenter implements AudioContract.Presenter {

    private AudioContract.View view;
    private final AudioModel model;

    public AudioPresenter(AudioContract.View view) {
        this.view = view;
        this.model = new AudioModel(((AudioFragment) view).getContext());
    }

    @Override
    public List<Item> getList(int cat) {
        return model.getList(cat);
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
