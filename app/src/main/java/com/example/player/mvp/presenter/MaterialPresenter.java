package com.example.player.mvp.presenter;

import com.example.player.mvp.contracct.MaterialContract;

public class MaterialPresenter implements MaterialContract.Presenter {

    private MaterialContract.View view;

    public MaterialPresenter(MaterialContract.View view) {
        this.view = view;
    }

    @Override
    public void audio() {
        /*
            play
                stop
            stop
                play

            play
                service
                notification
                view
            stop
                service
                notification
                view
        */
    }

    @Override
    public void detach() {
        view = null;
    }
}
