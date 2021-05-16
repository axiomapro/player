package com.example.player.mvp.own;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.player.R;
import com.example.player.basic.backend.Rview;
import com.example.player.basic.config.Config;
import com.example.player.basic.list.Item;
import com.example.player.basic.list.RecyclerViewAdapter;
import com.example.player.mvp.material.MaterialFragment;

public class OwnFragment extends Fragment implements OwnContract.View {

    private OwnContract.Presenter presenter;
    private Rview rView;

    public static OwnFragment newInstance(boolean clickMenu) {
        OwnFragment fragment = new OwnFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("clickMenu",clickMenu);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_own,container,false);
        getActivity().setTitle("Own");
        presenter = new OwnPresenter(this);
        rView = new Rview();
        rView.setList(presenter.getList());
        rView.setRecyclerView(v.findViewById(R.id.recyclerView));
        rView.init(Config.recyclerView().own(), new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                getFragmentManager().beginTransaction().replace(R.id.container, MaterialFragment.newInstance(rView.getItem(position).getId(),rView.getItem(position).getName()),Config.screen().material()).addToBackStack(Config.screen().material()).commit();
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

        if (getArguments() != null && getArguments().getBoolean("clickMenu")) add();
        return v;
    }

    public void add() {
        presenter.showAddDialog();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
    public void addItem(Item item) {
        rView.addItem(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detach();
    }
}
