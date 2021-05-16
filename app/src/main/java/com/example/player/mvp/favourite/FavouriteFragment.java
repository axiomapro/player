package com.example.player.mvp.favourite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Rview;
import com.example.player.basic.config.Config;
import com.example.player.basic.list.RecyclerViewAdapter;
import com.example.player.mvp.material.MaterialFragment;

public class FavouriteFragment extends Fragment implements FavouriteContract.View {

    private FavouriteContract.Presenter presenter;
    private Rview rView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite,container,false);
        getActivity().setTitle("Favourite");
        presenter = new FavouritePresenter(this);
        rView = new Rview();
        rView.setList(presenter.getList());
        rView.setRecyclerView(v.findViewById(R.id.recyclerView));
        rView.init(Config.recyclerView().favourite(), new RecyclerViewAdapter.RecyclerViewItem() {
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
        });
        return v;
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
}
