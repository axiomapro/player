package com.example.player.mvp.material;

public class MaterialPresenter implements MaterialContract.Presenter {

    private MaterialContract.View view;

    public MaterialPresenter(MaterialContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        view = null;
    }
}
