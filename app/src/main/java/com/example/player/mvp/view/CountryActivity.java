package com.example.player.mvp.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.Constant;
import com.example.player.basic.item.Item;
import com.example.player.basic.item.RecyclerViewAdapter;
import com.example.player.mvp.contracct.CountryContract;
import com.example.player.mvp.presenter.CountryPresenter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CountryActivity extends AppCompatActivity implements CountryContract.View {

    private CountryContract.Presenter presenter;
    private CoordinatorLayout coordinator;
    private Snackbar snackbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Выбор страны");
        coordinator = findViewById(R.id.coordinator);
        MainActivity.screen = Constant.SCREEN_COUNTRY;
        presenter = new CountryPresenter(this);
    }

    @Override
    public void initRecyclerView(List<Item> list) {
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(Constant.SCREEN_COUNTRY,list);
        adapter.setClickListener(new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                presenter.saveCountry(list.get(position).getId());
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onFavourite(int position) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showMessage(String message) {
        snackbar = Snackbar.make(coordinator,message,Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    @Override
    public void next() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    if (snackbar != null && snackbar.isShown()) snackbar.dismiss();
                    if (recyclerView == null) presenter.getList();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    if (recyclerView == null) showMessage("Включите WiFi");
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }
}
