package com.example.player.mvp.contracct;

public interface MaterialContract {

    interface View {
        void audio();
    }

    interface Presenter {
        void audio();
        void detach();
    }

}
