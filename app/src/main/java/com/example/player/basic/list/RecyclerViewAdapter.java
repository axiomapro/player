package com.example.player.basic.list;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.mvp.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private RecyclerViewItem listener;
    private final List<Item> list;
    private String screen;
    private int MENU_ITEM = 0;
    private int MENU_ITEM_SPACE = 1;

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
            if (screen.equals(Constant.SCREEN_COUNTRY)) item = R.layout.item_country;
            else if (screen.equals(Constant.SCREEN_CLOCK)) item = R.layout.item_clock;
            else if (screen.equals("menu")) item = R.layout.item_menu;
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

            if (screen.equals("dialog-audio") || screen.equals(Constant.SCREEN_AUDIO) || screen.equals(Constant.SCREEN_FAVOURITE) || screen.equals(Constant.SCREEN_OWN)) {
                holder.tvDesc.setText(item.getDesc());
                if (screen.equals("dialog-audio")) holder.ivFavourite.setVisibility(View.GONE);
                holder.ivIcon.setImageResource(R.drawable.ic_track);
            }

            if (screen.equals(Constant.SCREEN_AUDIO) || screen.equals(Constant.SCREEN_FAVOURITE) || screen.equals(Constant.SCREEN_OWN)) {
                if (item.isFavourite()) holder.ivFavourite.setImageResource(R.drawable.ic_star_active);
                else holder.ivFavourite.setImageResource(R.drawable.ic_star);

                holder.ivFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFavourite(position);
                    }
                });
            }

            if (screen.equals(Constant.SCREEN_CLOCK)) {
                holder.tvDate.setText(item.getDate());
                if (item.isActive()) holder.ivStatus.setImageResource(R.drawable.ic_notice_active);
                else holder.ivStatus.setImageResource(R.drawable.ic_notice);
            }

            if (screen.equals(Constant.SCREEN_COUNTRY)) {
                Picasso.get().load(Constant.WEBSITE+"/"+item.getImg()).noPlaceholder().into(holder.ivImg);
            }

            if (screen.equals("menu")) {
                if (item.getIcon() != 0) holder.ivIcon.setImageResource(item.getIcon());
                else holder.ivIcon.setVisibility(View.GONE);
                if (item.isActive()) holder.tvName.setTextColor(Color.parseColor("#DA4C42"));
                else holder.tvName.setTextColor(Color.parseColor("#222222"));
            }

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

    public Item getItem(int position) {
        return list.get(position);
    }

    public void remove(int position) {
        list.remove(position);
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
