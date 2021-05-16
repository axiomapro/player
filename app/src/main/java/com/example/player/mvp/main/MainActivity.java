package com.example.player.mvp.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Dialog;
import com.example.player.basic.backend.Param;
import com.example.player.basic.backend.Permission;
import com.example.player.basic.list.RecyclerViewAdapter;
import com.example.player.mvp.audio.AudioFragment;
import com.example.player.mvp.clock.ClockFragment;
import com.example.player.mvp.country.CountryActivity;
import com.example.player.mvp.favourite.FavouriteFragment;
import com.example.player.mvp.material.MaterialFragment;
import com.example.player.mvp.own.OwnFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, MainContract.View, ClockFragment.useActivity , OwnFragment.useActivity {

    private MainContract.Presenter presenter;
    public static MainActivity activity;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigation;
    private CoordinatorLayout coordinator;
    private Param param;
    private FragmentManager manager;
    private MenuItem itemAdd, itemSorting, itemReset;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerViewSorting;
    public static final String LOG = "playerLog";
    public static String screen = Constant.SCREEN_AUDIO;
    private String textAbout, textEmail, textShareLink, textRateLink, textOther, textBonus;
    public static int country = 1;
    public static int visual, nativeAdCnt;
    private boolean isVisibleSorting;
    private int sortingActivePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        initClasses();
        initRecyclerViewMenu();
        initRecyclerViewSorting();
        presenter.restoreDatabase();
        setListener();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        coordinator = findViewById(R.id.coordinator);
    }

    private void initClasses() {
        activity = MainActivity.this;
        param = new Param(this);
        country = param.getInt(Constant.PARAM_COUNTRY);
        presenter = new MainPresenter(this);
        manager = getSupportFragmentManager();
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_view_open,R.string.navigation_view_close);
    }

    private void initRecyclerViewMenu() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
        adapter = new RecyclerViewAdapter("menu",presenter.getListMenu());
        adapter.setClickListener(new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                String title = presenter.getListMenu().get(position).getName();
                itemSorting.setVisible(false);
                if (title.equals(Constant.SCREEN_AUDIO) || title.equals(Constant.SCREEN_FAVOURITE) || title.equals(Constant.SCREEN_CLOCK) || title.equals(Constant.SCREEN_OWN)) {
                    transition(title);
                }
                else if (title.equals("Add clock")) {
                    screen = Constant.SCREEN_CLOCK;
                    ClockFragment fragment = (ClockFragment) manager.findFragmentByTag(Constant.SCREEN_CLOCK);
                    if (fragment != null) fragment.add();
                    else manager.beginTransaction().replace(R.id.container,ClockFragment.newInstance(true),Constant.SCREEN_CLOCK).commit();
                    itemAdd.setVisible(true);
                }
                else if (title.equals("Add own")) {
                    screen = Constant.SCREEN_OWN;
                    OwnFragment fragment = (OwnFragment) manager.findFragmentByTag(Constant.SCREEN_OWN);
                    if (fragment != null) fragment.add();
                    else manager.beginTransaction().replace(R.id.container,OwnFragment.newInstance(true),Constant.SCREEN_OWN).commit();
                    itemAdd.setVisible(true);
                }
                else if (title.equals("Change")) {
                    param.setInt(Constant.PARAM_COUNTRY,0);
                    param.setInt(Constant.PARAM_SORTING,0);
                    startActivity(new Intent(getApplicationContext(), CountryActivity.class));
                    finish();
                }
                else if (title.equals("About")) {
                    presenter.showAboutDialog((textAbout == null || textAbout.equals(""))?"This is default ABOUT text":textAbout,textEmail);
                }
                else if (title.equals("Share")) {
                    presenter.share("Поделиться приложением",textShareLink);
                }
                else if (title.equals("Rate")) {
                    presenter.browser(textRateLink.replace("https://play.google.com/store/apps/","market://"),"Отсутствует Google play");
                }
                else if (title.equals("Other apps")) {
                    presenter.browser(textOther.replace("https://play.google.com/store/apps/","market://"),"Отсутствует Google play");
                }
                else if (title.equals("Bonus apps")) {
                    presenter.browser(textBonus.replace("https://play.google.com/store/apps/","market://"),"Отсутствует Google play");
                }
                drawerLayout.closeDrawers();
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

    private void initRecyclerViewSorting() {
        recyclerViewSorting = findViewById(R.id.recyclerViewSorting);
        RecyclerViewAdapter adapterSorting = new RecyclerViewAdapter("menu",presenter.getListSorting());
        adapterSorting.setClickListener(new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                drawerLayout.closeDrawers();
                ((AudioFragment) manager.findFragmentByTag(Constant.SCREEN_AUDIO)).sorting(adapterSorting.getItem(position).getId());
                adapterSorting.getItem(sortingActivePosition).setActive(false);
                adapterSorting.getItem(position).setActive(true);
                adapterSorting.notifyDataSetChanged();
                sortingActivePosition = position;
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onFavourite(int position) {

            }
        });
        recyclerViewSorting.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSorting.setAdapter(adapterSorting);
    }

    public MaterialFragment getMaterialFragment() {
        MaterialFragment fragment = null;
        if (screen.equals(Constant.SCREEN_MATERIAL)) fragment = (MaterialFragment) manager.findFragmentByTag(screen);
        return fragment;
    }

    private void setListener() {
        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,recyclerViewSorting);
        drawerToggle.syncState();
        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigation.setSelectedItemId(R.id.item_audio);
        manager.addOnBackStackChangedListener(this);
    }

    @Override
    public void initAudioFragment() {
        manager.beginTransaction().replace(R.id.container,new AudioFragment(),Constant.SCREEN_AUDIO).commit();
    }

    @Override
    public void parseConfig(String config) {
        Log.d(MainActivity.LOG,"Config: \n"+config);

        try {
            JSONObject json = new JSONObject(config);
            JSONObject jsonObject = json.getJSONObject("Config");
            boolean isClockTabVisible = jsonObject.getBoolean("is_clock_tab_visible");
            boolean isOwnTabVisible = jsonObject.getBoolean("is_own_tab_visible");
            boolean isChangeVisible = jsonObject.getBoolean("is_change_visible");
            textAbout = jsonObject.getString("text_about");
            textEmail = jsonObject.getString("text_email");
            textShareLink = jsonObject.getString("text_sharelink");
            textRateLink = jsonObject.getString("text_ratelink");
            textOther = jsonObject.getString("text_other");
            textBonus = jsonObject.getString("text_bonus");
            visual = Integer.parseInt(jsonObject.getString("vizual_effect"));
            nativeAdCnt = Integer.parseInt(jsonObject.getString("native_ad_cnt"));
            String showMode = jsonObject.getString("show_mode");
            String appodealAppKey = jsonObject.getString("YOUR_APPODEAL_APP_KEY");
            String playBanner1 = jsonObject.getString("play-banner1");
            String playBanner2 = jsonObject.getString("play-banner2");
            String playBanner3 = jsonObject.getString("play-banner3");
            String playBanner4 = jsonObject.getString("play-banner4");
            String mainAd = jsonObject.getString("MainAd");
            String returnAd = jsonObject.getString("ReturnAd");
            int returnMinute = jsonObject.getInt("ReturnMinute");
            String categoryButton = jsonObject.getString("CategoryButton");
            String clockBadge = jsonObject.getString("ClockBadge");
            String MainBanner = jsonObject.getString("MainBanner");
            String MainNative = jsonObject.getString("MainNative");
            String FavBanner = jsonObject.getString("FavBanner");
            String FavNative = jsonObject.getString("FavNative");
            String ClockBanner = jsonObject.getString("ClockBanner");
            String ClockNative = jsonObject.getString("ClockNative");
            String OwnBanner = jsonObject.getString("OwnBanner");
            String OwnNative = jsonObject.getString("OwnNative");
            String CntryBaner = jsonObject.getString("CntryBaner");
            String CntryAd = jsonObject.getString("CntryAd");

            int deleted = 0;
            if (!isClockTabVisible) {
                bottomNavigation.getMenu().findItem(R.id.item_clock).setVisible(false);
                adapter.remove(2);
                adapter.remove(3);
                adapter.remove(4);
                deleted = 3;
            }
            if (!isOwnTabVisible) {
                bottomNavigation.getMenu().findItem(R.id.item_own).setVisible(false);
                if (!isClockTabVisible) {
                    deleted = 6;
                    adapter.remove(2);
                    adapter.remove(3);
                    adapter.remove(4);
                } else {
                    deleted = 3;
                    adapter.remove(5);
                    adapter.remove(6);
                    adapter.remove(7);
                }
            }

            if (!isChangeVisible) adapter.getItem(9 - deleted).setVisible(false);
            if (textShareLink.equals("")) adapter.getItem(11 - deleted).setVisible(false);
            if (textRateLink.equals("")) adapter.getItem(12 - deleted).setVisible(false);
            if (textOther.equals("")) adapter.getItem(13 - deleted).setVisible(false);
            if (textBonus.equals("")) adapter.getItem(14 - deleted).setVisible(false);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG,"Ошибка в парсинге config.json");
        }
    }

    @Override
    public void showMessage(String message,String from) {
        Snackbar snackbar = Snackbar.make(coordinator,message,Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("receiver")) {
                    manager.beginTransaction().replace(R.id.container,new AudioFragment(),Constant.SCREEN_AUDIO).commit();
                }
                else snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void setSortingActivePosition(int position) {
        sortingActivePosition = position;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transition(item.getTitle().toString());
            return true;
        }
    };

    private void transition(String screen) {
        String tag = "";
        Fragment fragment = null;
        boolean sorting = false;
        boolean add = false;
        int item = 0;
        switch (screen) {
            case Constant.SCREEN_AUDIO:
                item = R.id.item_audio;
                tag = getString(R.string.menu_item_audio);
                if (isVisibleSorting) sorting = true;
                fragment = new AudioFragment();
                break;
            case Constant.SCREEN_FAVOURITE:
                item = R.id.item_favourite;
                tag = getString(R.string.menu_item_favourite);
                fragment = new FavouriteFragment();
                break;
            case Constant.SCREEN_CLOCK:
                item = R.id.item_clock;
                tag = getString(R.string.menu_item_clock);
                add = true;
                fragment = new ClockFragment();
                break;
            case Constant.SCREEN_OWN:
                item = R.id.item_own;
                tag = getString(R.string.menu_item_own);
                add = true;
                fragment = new OwnFragment();
                break;
        }

        MainActivity.screen = tag;
        if (itemAdd != null) itemAdd.setVisible(add);
        if (itemSorting != null) itemSorting.setVisible(sorting);
        bottomNavigation.getMenu().findItem(item).setChecked(true);
        manager.beginTransaction().replace(R.id.container,fragment,tag).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.restoreDatabase();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Подтвердите разрешение для проверки резервной копии", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.restoreDatabase();
                        }
                    }, 3000);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option,menu);
        itemAdd = menu.findItem(R.id.item_add);
        itemSorting = menu.findItem(R.id.item_sorting);
        itemReset = menu.findItem(R.id.item_reset);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isVisibleSorting = presenter.isVisibleSortingIcon();
                if (isVisibleSorting) itemSorting.setVisible(true);
            }
        },param.getBoolean(Constant.PARAM_CHECK_IMPORT_DB)?100:4000);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                if (screen.equals(Constant.SCREEN_CLOCK)) ((ClockFragment) manager.findFragmentByTag(screen)).add();
                if (screen.equals(Constant.SCREEN_OWN)) ((OwnFragment) manager.findFragmentByTag(screen)).add();
                break;
            case R.id.item_sorting:
                drawerLayout.openDrawer(recyclerViewSorting);
                break;
            case R.id.item_reset:
                getMaterialFragment().resetPlayer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
        int total = manager.getBackStackEntryCount();
        if (total > 0) {
            itemAdd.setVisible(false);
            itemReset.setVisible(true);
            if (isVisibleSorting) itemSorting.setVisible(false);
            screen = Constant.SCREEN_MATERIAL;
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            bottomNavigation.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.popBackStack();
                }
            });
        } else {
            screen = manager.getFragments().get(0).getTag();
            itemReset.setVisible(false);
            if (screen.equals(Constant.SCREEN_OWN) || screen.equals(Constant.SCREEN_CLOCK)) itemAdd.setVisible(true);
            if (screen.equals(Constant.SCREEN_AUDIO) && isVisibleSorting) itemSorting.setVisible(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.exportDatabase();
        presenter.detach();
        activity = null;
        screen = null;
    }
}