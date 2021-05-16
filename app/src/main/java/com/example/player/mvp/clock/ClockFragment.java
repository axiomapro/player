package com.example.player.mvp.clock;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Rview;
import com.example.player.basic.list.Item;
import com.example.player.basic.list.RecyclerViewAdapter;

public class ClockFragment extends Fragment implements ClockContract.View {

    /*
        - native ad
    */

    private useActivity listener;
    private ClockContract.Presenter presenter;
    private Rview rView;

    public interface useActivity {
        void showMessage(String message,String from);
    }

    public static ClockFragment newInstance(boolean clickMenu) {
        ClockFragment fragment = new ClockFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("clickMenu",clickMenu);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clock,container,false);
        getActivity().setTitle("Clock");
        presenter = new ClockPresenter(this);
        presenter.updateStatusClock();
        rView = new Rview();
        rView.setList(presenter.getList());
        rView.init(Constant.SCREEN_CLOCK,v, new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onLongClick(int position) {
                presenter.showDeleteDialog(rView.getItem(position).getId(),position);
            }

            @Override
            public void onFavourite(int position) {
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
        // listener.showMessage(message);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof useActivity) listener = (useActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        presenter.detach();
    }
}
