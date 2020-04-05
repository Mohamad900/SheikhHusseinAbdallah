package com.sheikh.hussein.abdallah.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sheikh.hussein.abdallah.R;
import com.sheikh.hussein.abdallah.data.SharedPref;
import com.sheikh.hussein.abdallah.fragment.FragmentFavorite;
import com.sheikh.hussein.abdallah.fragment.FragmentHome;
import com.sheikh.hussein.abdallah.fragment.FragmentPlaylist;
import com.sheikh.hussein.abdallah.fragment.FragmentVideo;
import com.sheikh.hussein.abdallah.room.AppDatabase;
import com.sheikh.hussein.abdallah.room.DAO;
import com.sheikh.hussein.abdallah.utils.BottomNavigationViewHelper;
import com.sheikh.hussein.abdallah.utils.Tools;

public class ActivityMain extends AppCompatActivity {

    private BottomNavigationView navigation;
    private ActionBar actionBar;
    private FragmentHome fragmentHome;
    static boolean active = false;
    private FragmentPlaylist fragmentPlaylist;
    private FragmentVideo fragmentVideo;
    private FragmentFavorite fragmentFavorite;

    private long exitTime = 0;
    private int notification_count = -1;
    private View notif_badge;

    private SharedPref sharedPref;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = new SharedPref(this);
        dao = AppDatabase.getDb(this).getDAO();

        initComponent();
        initToolbar();

        onNavigationMenuItemSelected(R.id.navigation_home, true);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.navigation_menu_home));
        actionBar.setDisplayHomeAsUpEnabled(false);
        Tools.changeOverflowMenuIconColor(toolbar, getResources().getColor(R.color.colorPrimary));
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }


    private void initComponent() {
        navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onNavigationMenuItemSelected(item.getItemId(), false);
                return true;
            }
        });
    }

    public void onNavigationMenuItemSelected(int item_id, boolean selected) {
        Fragment fragment = null;
        String title = "";
        switch (item_id) {
            case R.id.navigation_home:
                if (fragmentHome == null) fragmentHome = new FragmentHome();
                fragment = fragmentHome;
                title = getString(R.string.navigation_menu_home);
                break;
            case R.id.navigation_playlist:
                if (fragmentPlaylist == null) fragmentPlaylist = new FragmentPlaylist();
                fragment = fragmentPlaylist;
                title = getString(R.string.navigation_menu_playlist);
                break;
            case R.id.navigation_video:
                if (fragmentVideo == null) fragmentVideo = new FragmentVideo();
                fragment = fragmentVideo;
                title = getString(R.string.navigation_menu_video);
                break;
            case R.id.navigation_favorite:
                if (fragmentFavorite == null) fragmentFavorite = new FragmentFavorite();
                fragment = fragmentFavorite;
                title = getString(R.string.navigation_menu_favorite);
                break;
        }
        actionBar.setTitle(title);
        if (fragment != null) loadFragment(fragment);
        if (selected) {
            navigation.setSelectedItemId(item_id);
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int new_notif_count = dao.getNotificationUnreadCount();
        if (new_notif_count != notification_count) {
            notification_count = new_notif_count;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorPrimary));

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = MenuItemCompat.getActionView(menuItem);
        notif_badge = actionView.findViewById(R.id.notif_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    private void setupBadge() {
        if (notif_badge == null) return;
        if (notification_count == 0) {
            notif_badge.setVisibility(View.GONE);
        } else {
            notif_badge.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            finish();
        } else if (item_id == R.id.action_search) {
            ActivitySearch.navigate(this, null);
        } else if (item_id == R.id.action_notifications) {
            ActivityNotifications.navigate(this);
        } else if (item_id == R.id.action_settings) {
            ActivitySettings.navigate(this);
        } else if (item_id == R.id.action_rate) {
            Tools.rateAction(this);
        } else if (item_id == R.id.action_about) {
            Tools.showDialogAbout(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
