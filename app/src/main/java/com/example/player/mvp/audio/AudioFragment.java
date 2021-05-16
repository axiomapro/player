package com.example.player.mvp.audio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.player.R;
import com.example.player.basic.backend.Param;
import com.example.player.basic.backend.Rview;
import com.example.player.basic.config.Config;
import com.example.player.basic.list.RecyclerViewAdapter;
import com.example.player.mvp.material.MaterialFragment;

public class AudioFragment extends Fragment implements AudioContract.View, RecyclerViewAdapter.RecyclerViewItem {

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
        rView.setList(presenter.getList(param.getInt(Config.param().sorting())));
        rView.setRecyclerView(v.findViewById(R.id.recyclerView));
        rView.init(Config.recyclerView().audio(), this);
        return v;
    }

    public void sorting(int cat) {
        param.setInt(Config.param().sorting(),cat);
        rView.clear();
        rView.setList(presenter.getList(cat));
        rView.refresh(Config.screen().audio(),this);
    }

    @Override
    public void updateItem(int position, boolean status) {
        rView.updateItem(position,status);
    }

    @Override
    public void onItemClick(int position) {
        getFragmentManager().beginTransaction().replace(R.id.container, MaterialFragment.newInstance(rView.getItem(position).getId(),rView.getItem(position).getName()),Config.screen().material()).addToBackStack(Config.screen().material()).commit();
    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onFavourite(int position) {
        presenter.toggleFavourite(rView.getItem(position).getId(),position);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detach();
    }
}
