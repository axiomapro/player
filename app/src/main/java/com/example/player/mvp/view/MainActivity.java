package com.example.player.mvp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.player.R;
import com.example.player.basic.Constant;
import com.example.player.basic.Param;
import com.example.player.basic.Permission;
import com.example.player.mvp.contracct.MainContract;
import com.example.player.mvp.presenter.MainPresenter;
import com.example.player.mvp.view.AudioFragment;
import com.example.player.mvp.view.ClockFragment;
import com.example.player.mvp.view.FavouriteFragment;
import com.example.player.mvp.view.OwnFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener, MainContract.View, ClockFragment.useActivity , OwnFragment.useActivity {

    private MainContract.Presenter presenter;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigation;
    private NavigationView navigationView;
    private CoordinatorLayout coordinator;
    private FragmentManager manager;
    private MenuItem itemAdd;
    public static MainActivity activity;
    public static final String LOG = "playerLog";
    public static String screen = Constant.SCREEN_AUDIO;
    public static int country = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        initClasses();
        setListener();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        coordinator = findViewById(R.id.coordinator);
    }

    private void initClasses() {
        activity = MainActivity.this;
        Param param = new Param(this);
        country = param.getInt(Constant.PARAM_COUNTRY);
        presenter = new MainPresenter(this);
        manager = getSupportFragmentManager();
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_view_open,R.string.navigation_view_close);
        presenter.restoreDatabase();
    }

    private void setListener() {
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigation.setSelectedItemId(R.id.item_audio);
        manager.addOnBackStackChangedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void initAudioFragment() {
        manager.beginTransaction().add(R.id.container,new AudioFragment(),Constant.SCREEN_AUDIO).commit();
    }

    @Override
    public void parseConfig(String config) {
        try {
            JSONObject json = new JSONObject(config);
            JSONObject jsonObject = json.getJSONObject("Config");
            boolean isClockTabVisible = jsonObject.getBoolean("is_clock_tab_visible");
            boolean isOwnTabVisible = jsonObject.getBoolean("is_own_tab_visible");
            boolean isChangeVisible = jsonObject.getBoolean("is_change_visible");
            String textShareLink = jsonObject.getString("text_sharelink");
            String textRateLink = jsonObject.getString("text_ratelink");
            String textOther = jsonObject.getString("text_other");
            String textBonus = jsonObject.getString("text_bonus");

            if (!isClockTabVisible) {
                navigationView.getMenu().findItem(R.id.item_clock).setVisible(false);
                bottomNavigation.getMenu().findItem(R.id.item_clock).setVisible(false);
            }
            if (!isOwnTabVisible) {
                navigationView.getMenu().findItem(R.id.item_own).setVisible(false);
                bottomNavigation.getMenu().findItem(R.id.item_own).setVisible(false);
            }
            if (!isChangeVisible) navigationView.getMenu().findItem(R.id.item_change).setVisible(false);
            if (textShareLink.equals("")) navigationView.getMenu().findItem(R.id.item_share).setVisible(false);
            if (textRateLink.equals("")) navigationView.getMenu().findItem(R.id.item_rate).setVisible(false);
            if (textOther.equals("")) navigationView.getMenu().findItem(R.id.item_other_apps).setVisible(false);
            if (textBonus.equals("")) navigationView.getMenu().findItem(R.id.item_bonus_apps).setVisible(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(coordinator,message,Snackbar.LENGTH_SHORT).show();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transition(item.getTitle().toString());
            return true;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();

        if (title.equals(Constant.SCREEN_AUDIO) || title.equals(Constant.SCREEN_FAVOURITE) || title.equals(Constant.SCREEN_CLOCK) || title.equals(Constant.SCREEN_OWN)) {
            transition(title);
        }
        else if (title.equals("Change")) {

        }
        else if (title.equals("About")) {

        }
        else if (title.equals("Share")) {

        }
        else if (title.equals("Rate") || title.equals("Other apps") || title.equals("Bonus apps")) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void transition(String screen) {
        String title = "";
        Fragment fragment = null;
        boolean isVisibleAdd = false;
        int item = 0;
        switch (screen) {
            case Constant.SCREEN_AUDIO:
                item = R.id.item_audio;
                title = getString(R.string.menu_item_audio);
                fragment = new AudioFragment();
                break;
            case Constant.SCREEN_FAVOURITE:
                item = R.id.item_favourite;
                title = getString(R.string.menu_item_favourite);
                fragment = new FavouriteFragment();
                break;
            case Constant.SCREEN_CLOCK:
                isVisibleAdd = true;
                item = R.id.item_clock;
                title = getString(R.string.menu_item_clock);
                fragment = new ClockFragment();
                break;
            case Constant.SCREEN_OWN:
                isVisibleAdd = true;
                item = R.id.item_own;
                title = getString(R.string.menu_item_own);
                fragment = new OwnFragment();
                break;
        }

        MainActivity.screen = title;
        if (itemAdd != null) itemAdd.setVisible(isVisibleAdd);
        bottomNavigation.getMenu().findItem(item).setChecked(true);
        getSupportActionBar().setTitle(title);
        manager.beginTransaction().replace(R.id.container,fragment,title).commit();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_add) {
            if (screen.equals(Constant.SCREEN_CLOCK)) ((ClockFragment) manager.findFragmentByTag(screen)).add();
            if (screen.equals(Constant.SCREEN_OWN)) ((OwnFragment) manager.findFragmentByTag(screen)).add();
        }
        return true;
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
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            bottomNavigation.setVisibility(View.GONE);
        } else {
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
    }
}