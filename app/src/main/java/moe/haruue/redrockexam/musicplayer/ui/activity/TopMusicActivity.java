package moe.haruue.redrockexam.musicplayer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.ui.adapter.TopMusicPagerAdapter;
import moe.haruue.redrockexam.musicplayer.ui.navigation.NavigationManager;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayService;
import moe.haruue.redrockexam.util.abstracts.HaruueActivity;

public class TopMusicActivity extends HaruueActivity {


    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavigationManager navigationManager;

    ViewPager viewPager;
    TopMusicPagerAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_music);
        // Service
        MusicPlayService.start(this);
        MusicPlayService.bind(this);
        initView();
    }

    private void initView() {
        initToolbar();
        initDrawer();
        viewPager = $(R.id.view_pager_in_activity);
        adapter = new TopMusicPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = $(R.id.tab_layout_in_activity);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar = $(R.id.toolbar_in_activity);
        toolbar.setTitle(R.string.search);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    private void initDrawer() {
        drawerLayout = $(R.id.dl_main_drawer);
        navigationView = $(R.id.nv_main_navigation);
        navigationManager = new NavigationManager(this, drawerLayout, navigationView, R.id.nav_hot);
        navigationManager.initialize();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayService.unbind(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, TopMusicActivity.class);
        context.startActivity(starter);
    }
}
