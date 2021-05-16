package com.example.player.basic.list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.config.Config;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private RecyclerViewItem listener;
    private final List<Item> list;
    private String screen;
    private final int MENU_ITEM = 0;
    private final int MENU_ITEM_SPACE = 1;

    public interface RecyclerViewItem {
        void onItemClick(int position);
        void onLongClick(int position);
        void onFavourite(int position);
    }

    public void setClickListener(RecyclerViewItem listener) {
        this.listener = listener;
    }

    public RecyclerViewAdapter(String screen,List<Item> list) {
        this.screen = screen;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int item = 0;
        if (viewType == MENU_ITEM_SPACE) {
            item = R.layout.item_menu_space;
        } else {
            if (screen.equals(Config.recyclerView().country())) item = R.layout.item_country;
            else if (screen.equals(Config.recyclerView().clock())) item = R.layout.item_clock;
            else if (screen.equals(Config.recyclerView().menu())) item = R.layout.item_menu;
            else item = R.layout.item_audio;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(item, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = list.get(position);
        if (getItemViewType(position) == MENU_ITEM_SPACE) {

        } else {
            holder.tvName.setText(item.getName());

            if (screen.equals(Config.recyclerView().dialogAudio()) || screen.equals(Config.recyclerView().audio()) || screen.equals(Config.recyclerView().favourite()) || screen.equals(Config.recyclerView().own())) {
                holder.tvDesc.setText(item.getDesc());
                if (screen.equals(Config.recyclerView().dialogAudio())) holder.ivFavourite.setVisibility(View.GONE);
                holder.ivIcon.setImageResource(R.drawable.ic_track);
            }

            if (screen.equals(Config.recyclerView().audio()) || screen.equals(Config.recyclerView().favourite()) || screen.equals(Config.recyclerView().own())) {
                if (item.isFavourite()) holder.ivFavourite.setImageResource(R.drawable.ic_star_active);
                else holder.ivFavourite.setImageResource(R.drawable.ic_star);

                holder.ivFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFavourite(position);
                    }
                });
            }

            if (screen.equals(Config.recyclerView().clock())) {
                holder.tvDate.setText(item.getDate());
                if (item.isActive()) holder.ivStatus.setImageResource(R.drawable.ic_notice_active);
                else holder.ivStatus.setImageResource(R.drawable.ic_notice);
            }

            if (screen.equals(Config.recyclerView().country())) {
                Picasso.get().load(Config.url().site()+"/"+item.getImg()).noPlaceholder().into(holder.ivImg);
            }

            if (screen.equals("menu")) {
                if (item.getIcon() != 0) holder.ivIcon.setImageResource(item.getIcon());
                else holder.ivIcon.setVisibility(View.GONE);
                if (item.isActive()) holder.tvName.setTextColor(Color.parseColor("#DA4C42"));
                else holder.tvName.setTextColor(Color.parseColor("#222222"));
            }

            if (holder.llClick != null) {
                holder.llClick.setOnClickListener(v -> {
                    listener.onItemClick(position);
                });

                holder.llClick.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onLongClick(position);
                        return false;
                    }
                });
            } else {
                holder.itemView.setOnClickListener(v -> {
                    listener.onItemClick(position);
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onLongClick(position);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        int result = MENU_ITEM;
        if (list.get(position).getName() == null) result = MENU_ITEM_SPACE;
        return result;
    }
}
