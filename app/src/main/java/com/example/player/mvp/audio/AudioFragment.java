package com.example.player.mvp.audio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Param;
import com.example.player.basic.backend.Rview;
import com.example.player.basic.list.RecyclerViewAdapter;
import com.example.player.mvp.main.MainActivity;
import com.example.player.mvp.material.MaterialFragment;

public class AudioFragment extends Fragment implements AudioContract.View, RecyclerViewAdapter.RecyclerViewItem {

    /*
        - native ad
    */

    private AudioContract.Presenter presenter;
    private Rview rView;
    private Param param;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audio, container, false);
        getActivity().setTitle("Audio");
        param = new Param(getContext());
        presenter = new AudioPresenter(this);
        rView = new Rview();
        rView.setList(presenter.getList(param.getInt(Constant.PARAM_SORTING)));
        rView.init(Constant.SCREEN_AUDIO,v, this);
        return v;
    }

    public void sorting(int cat) {
        param.setInt(Constant.PARAM_SORTING,cat);
        rView.clear();
        rView.setList(presenter.getList(cat));
        rView.refresh(Constant.SCREEN_AUDIO,this);
    }

    @Override
    public void updateItem(int position, boolean status) {
        rView.updateItem(position,status);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detach();
    }

    @Override
    public void onItemClick(int position) {
        getFragmentManager().beginTransaction().replace(R.id.container, MaterialFragment.newInstance(rView.getItem(position).getId(),rView.getItem(position).getName()),Constant.SCREEN_MATERIAL).addToBackStack(Constant.SCREEN_MATERIAL).commit();
    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onFavourite(int position) {
        presenter.toggleFavourite(rView.getItem(position).getId(),position);
    }
}
