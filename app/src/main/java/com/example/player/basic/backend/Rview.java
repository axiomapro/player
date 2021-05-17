package com.example.player.basic.backend;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void init(String screen,RecyclerViewAdapter.RecyclerViewItem listener) {
        adapter = new RecyclerViewAdapter(screen,list);
        adapter.setClickListener(listener);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void clear() {
        list.clear();
        adapter.notifyDataSetChanged();
    }

    public void update() {
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

    public void removeMenuItems(boolean removeByGroup,String name,int group) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (removeByGroup) {
                if (list.get(position).getGroup() == group && group != 0) {
                    list.remove(position);
                    position--;
                } else position++;
            } else {
                if (list.get(position).getName().equals(name)) {
                    list.remove(position);
                    position--;
                } else position++;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public Item getItem(int position) {
        return list.get(position);
    }

}
