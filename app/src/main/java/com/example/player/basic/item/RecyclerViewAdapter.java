package com.example.player.basic.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.mvp.view.MainActivity;
import com.example.player.R;
import com.example.player.basic.Constant;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private RecyclerViewItem listener;
    private final List<Item> list;
    private String screen;

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
        int item = R.layout.item_audio;
        if (screen.equals(Constant.SCREEN_COUNTRY)) item = R.layout.item_country;
        if (screen.equals(Constant.SCREEN_CLOCK)) item = R.layout.item_clock;
        View v = LayoutInflater.from(parent.getContext()).inflate(item, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = list.get(position);
        holder.tvName.setText(item.getName());

        if (screen.equals("dialog-audio") || screen.equals(Constant.SCREEN_AUDIO) || screen.equals(Constant.SCREEN_FAVOURITE) || screen.equals(Constant.SCREEN_OWN)) {
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName,tvDate;
        private ImageView ivIcon, ivFavourite, ivStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemName);
            tvDate = itemView.findViewById(R.id.itemDate);
            ivIcon = itemView.findViewById(R.id.itemIcon);
            ivFavourite = itemView.findViewById(R.id.itemFavourite);
            ivStatus = itemView.findViewById(R.id.itemStatus);
        }
    }

}
