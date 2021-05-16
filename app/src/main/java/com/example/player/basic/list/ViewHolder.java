package com.example.player.basic.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;

class ViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout llClick;
    public TextView tvName,tvDesc,tvDate;
    public ImageView ivIcon, ivFavourite, ivStatus, ivImg;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        llClick = itemView.findViewById(R.id.itemClick);
        tvName = itemView.findViewById(R.id.itemName);
        tvDesc = itemView.findViewById(R.id.itemDesc);
        tvDate = itemView.findViewById(R.id.itemDate);
        ivIcon = itemView.findViewById(R.id.itemIcon);
        ivImg = itemView.findViewById(R.id.itemImg);
        ivFavourite = itemView.findViewById(R.id.itemFavourite);
        ivStatus = itemView.findViewById(R.id.itemStatus);
    }
}
