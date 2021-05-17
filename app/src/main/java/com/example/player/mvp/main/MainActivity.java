package com.example.player.mvp.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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
import com.example.player.basic.backend.Rview;
import com.example.player.basic.config.Config;
import com.example.player.basic.list.Item;
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

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, MainContract.View {

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
    private Rview rviewMenu, rviewSorting;
    private RecyclerView recyclerViewMenu, recyclerViewSorting;
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
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        recyclerViewSorting = findViewById(R.id.recyclerViewSorting);
    }

    private void initClasses() {
        Constant.screen = Config.screen().audio();
        activity = MainActivity.this;
        param = new Param(this);
        Constant.country = param.getInt(Config.param().country());
        presenter = new MainPresenter(this);
        manager = getSupportFragmentManager();
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_view_open,R.string.navigation_view_close);
    }

    private void initRecyclerViewMenu() {
        rviewMenu = new Rview();
        rviewMenu.setList(presenter.getListMenu());
        rviewMenu.setRecyclerView(recyclerViewMenu);
        rviewMenu.init(Config.recyclerView().menu(), new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                String title = rviewMenu.getItem(position).getName();
                itemSorting.setVisible(false);
                if (title.equals(Config.screen().audio()) || title.equals(Config.screen().favourite()) || title.equals(Config.screen().clock()) || title.equals(Config.screen().own())) {
                    transition(title);
                }
                else if (title.equals("Add clock")) {
                    Constant.screen = Config.screen().clock();
                    ClockFragment fragment = (ClockFragment) manager.findFragmentByTag(Config.screen().clock());
                    if (fragment != null) fragment.add();
                    else manager.beginTransaction().replace(R.id.container,ClockFragment.newInstance(true),Config.screen().clock()).commit();
                    itemAdd.setVisible(true);
                }
                else if (title.equals("Add own")) {
                    Constant.screen = Config.screen().own();
                    OwnFragment fragment = (OwnFragment) manager.findFragmentByTag(Config.screen().own());
                    if (fragment != null) fragment.add();
                    else manager.beginTransaction().replace(R.id.container,OwnFragment.newInstance(true),Config.screen().own()).commit();
                    itemAdd.setVisible(true);
                }
                else if (title.equals("Change")) {
                    param.setInt(Config.param().country(),0);
                    param.setInt(Config.param().sorting(),0);
                    startActivity(new Intent(getApplicationContext(), CountryActivity.class));
                    finish();
                }
                else if (title.equals("About")) {
                    presenter.showAboutDialog((Constant.textAbout == null || Constant.textAbout.equals(""))?"This is default ABOUT text":Constant.textAbout,Constant.textEmail);
                }
                else if (title.equals("Share")) {
                    presenter.share("Поделиться приложением",Constant.textShareLink);
                }
                else if (title.equals("Rate")) {
                    presenter.browser(Constant.textRateLink.replace("https://play.google.com/store/apps/","market://"),"Отсутствует Google play");
                }
                else if (title.equals("Other apps")) {
                    presenter.browser(Constant.textOther.replace("https://play.google.com/store/apps/","market://"),"Отсутствует Google play");
                }
                else if (title.equals("Bonus apps")) {
                    presenter.browser(Constant.textBonus.replace("https://play.google.com/store/apps/","market://"),"Отсутствует Google play");
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
    }

    private void initRecyclerViewSorting() {
        rviewSorting = new Rview();
        rviewSorting.setList(presenter.getListSorting());
        rviewSorting.setRecyclerView(recyclerViewSorting);
        rviewSorting.init(Config.recyclerView().menu(), new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                drawerLayout.closeDrawers();
                ((AudioFragment) manager.findFragmentByTag(Config.screen().audio())).sorting(rviewSorting.getItem(position).getId());
                rviewSorting.getItem(sortingActivePosition).setActive(false);
                rviewSorting.getItem(position).setActive(true);
                rviewSorting.update();
                sortingActivePosition = position;
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onFavourite(int position) {

            }
        });
    }

    public MaterialFragment getMaterialFragment() {
        MaterialFragment fragment = null;
        if (Constant.screen.equals(Config.screen().material())) fragment = (MaterialFragment) manager.findFragmentByTag(Constant.screen);
        return fragment;
    }

    private void setListener() {
        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,findViewById(R.id.recyclerViewSorting));
        drawerToggle.syncState();
        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigation.getMenu().findItem(R.id.item_audio).setChecked(true);
        manager.addOnBackStackChangedListener(this);

        toolbar.setNavigationOnClickListener(v -> {
            if (Constant.screen.equals(Config.screen().material())) manager.popBackStack();
            else drawerLayout.openDrawer(Gravity.LEFT);
        });
    }

    @Override
    public void initAudioFragment() {
        manager.beginTransaction().replace(R.id.container,new AudioFragment(),Config.screen().audio()).commit();
    }

    @Override
    public void parseConfig(String config) {
        Constant.parse(config, new Constant.VisibleItems() {
            @Override
            public void hideGroup(int group) {
                rviewMenu.removeMenuItems(true,null,group);
            }

            @Override
            public void hideItem(String name) {
                rviewMenu.removeMenuItems(false,name,0);
            }
        });
    }

    @Override
    public void showMessage(String message,String from) {
        Snackbar snackbar = Snackbar.make(coordinator,message,Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", v -> {
            if (from.equals("receiver")) transition(Config.screen().audio());
            else snackbar.dismiss();
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
        if (Config.screen().audio().equals(screen)) {
            item = R.id.item_audio;
            tag = getString(R.string.menu_item_audio);
            if (isVisibleSorting) sorting = true;
            fragment = new AudioFragment();
        } else if (Config.screen().favourite().equals(screen)) {
            item = R.id.item_favourite;
            tag = getString(R.string.menu_item_favourite);
            fragment = new FavouriteFragment();
        } else if (Config.screen().clock().equals(screen)) {
            item = R.id.item_clock;
            tag = getString(R.string.menu_item_clock);
            add = true;
            fragment = new ClockFragment();
        } else if (Config.screen().own().equals(screen)) {
            item = R.id.item_own;
            tag = getString(R.string.menu_item_own);
            add = true;
            fragment = new OwnFragment();
        }

        Constant.screen = tag;
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
        },param.getBoolean(Config.param().checkImportDb())?100:4000);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                if (Constant.screen.equals(Config.screen().clock())) ((ClockFragment) manager.findFragmentByTag(Config.screen().clock())).add();
                if (Constant.screen.equals(Config.screen().own())) ((OwnFragment) manager.findFragmentByTag(Config.screen().own())).add();
                break;
            case R.id.item_sorting:
                drawerLayout.openDrawer(findViewById(R.id.recyclerViewSorting));
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
            Constant.screen = Config.screen().material();
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            bottomNavigation.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Constant.screen = manager.getFragments().get(0).getTag();
            itemReset.setVisible(false);
            if (Constant.screen.equals(Config.screen().own()) || Constant.screen.equals(Config.screen().clock())) itemAdd.setVisible(true);
            if (Constant.screen.equals(Config.screen().audio()) && isVisibleSorting) itemSorting.setVisible(true);
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
        Constant.screen = null;
    }
}