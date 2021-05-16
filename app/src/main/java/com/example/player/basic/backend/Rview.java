package com.example.player.basic.backend;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.list.Item;
import com.example.player.basic.list.RecyclerViewAdapter;

import java.util.List;

public class Rview {

    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<Item> list;

    public void setList(List<Item> list) {
        this.list = list;
    }

    public void init(String screen,View v, RecyclerViewAdapter.RecyclerViewItem listener) {
        recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(screen,list);
        adapter.setClickListener(listener);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void clear() {
        list.clear();
        adapter.notifyDataSetChanged();
    }

    public void refresh(String screen, RecyclerViewAdapter.RecyclerViewItem listener) {
        adapter = new RecyclerViewAdapter(screen,list);
        adapter.setClickListener(listener);
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
