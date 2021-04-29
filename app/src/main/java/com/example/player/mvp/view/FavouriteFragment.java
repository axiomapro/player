package com.example.player.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.Constant;
import com.example.player.basic.Rview;
import com.example.player.basic.item.Item;
import com.example.player.basic.item.RecyclerViewAdapter;
import com.example.player.mvp.contracct.FavouriteContract;
import com.example.player.mvp.presenter.FavouritePresenter;

import java.util.List;

public class FavouriteFragment extends Fragment implements FavouriteContract.View {

    /*
        - native ad
    */
    private FavouriteContract.Presenter presenter;
    private Rview rView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite,container,false);
        presenter = new FavouritePresenter(this);
        rView = new Rview();
        rView.setList(presenter.getList());
        rView.init(v, new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                getFragmentManager().beginTransaction().replace(R.id.container,MaterialFragment.newInstance(rView.getItem(position).getUrl()),Constant.SCREEN_MATERIAL).addToBackStack(Constant.SCREEN_MATERIAL).commit();
            }

            @Override
            public void onLongClick(int position) {
                presenter.showDeleteDialog(rView.getItem(position).getId(),position);
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
    public void removeItem(int position) {
        rView.removeItem(position);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detach();
    }
}
