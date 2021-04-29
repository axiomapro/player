package com.example.player.basic;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.item.Item;
import com.example.player.basic.item.RecyclerViewAdapter;
import com.example.player.mvp.view.MainActivity;

import java.util.List;

public class Rview {

    private RecyclerViewAdapter adapter;
    private List<Item> list;

    public void setList(List<Item> list) {
        this.list = list;
    }

    public void init(View v, RecyclerViewAdapter.RecyclerViewItem listener) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(MainActivity.screen,list);
        adapter.setClickListener(listener);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void addItem(Item item) {
        list.add(0,item);
        adapter.notifyItemInserted(0);
    }

    public void updateItem(int position,boolean status) {
        list.get(position).setFavourite(status);
        adapter.notifyItemChanged(position);
    }

    public void removeItem(int position) {
        list.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,list.size());
    }

    public Item getItem(int position) {
        return list.get(position);
    }

}
