package moe.haruue.redrockexam.musicplayer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.caoyue.imageloader.ImageLoader;
import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.database.helper.MusicDatabaseHelper;
import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlayList;
import moe.haruue.redrockexam.musicplayer.ui.adapter.SongItemAdapter;
import moe.haruue.redrockexam.musicplayer.ui.navigation.NavigationManager;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayService;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayServiceConnection;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayerController;
import moe.haruue.redrockexam.ui.recyclerview.HaruueAdapter;
import moe.haruue.redrockexam.ui.recyclerview.HaruueRecyclerView;
import moe.haruue.redrockexam.ui.widget.CircleImageView;
import moe.haruue.redrockexam.util.ActivityManager;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.ThreadUtils;
import moe.haruue.redrockexam.util.abstracts.HaruueActivity;
import moe.haruue.redrockexam.util.permission.RequestPermissionListener;

public class MainActivity extends HaruueActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavigationManager navigationManager;
    HaruueRecyclerView playListView;
    SongItemAdapter adapter;
    SeekBar seekBar;
    CircleImageView albumImageView;
    TextView titleView;
    TextView singerView;
    Listener listener = new Listener();
    ImageView buttonNext;
    Handler handler;

    /**
     * 用于生成 requestCode 的序列数字，每使用一次都要 +1 （使用时直接 {@code serialNumber++）
     */
    int serialNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(getMainLooper());
        // Service
        MusicPlayService.start(this);
        MusicPlayService.bind(this);
        // Single Task
        try {
            ActivityManager.finishPreviousActivity();
        } catch (Exception e) {
            StandardUtils.printStack(e);
        }
        initView();
        initData();
        MusicPlayerController.addToCurrentPlayMusicListeners(listener);
    }

    private void initView() {
        initToolbar();
        initDrawer();
        // Initialize List
        playListView = $(R.id.list_local_play_list);
        playListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongItemAdapter(this);
        adapter.setAutoNotify(true);
        adapter.setOnItemClickListener(listener);
        adapter.setOnItemLongClickListener(listener);
        playListView.setAdapter(adapter);
        playListView.setOnRefreshListener(listener);
//        playListView.refresh();
        listener.onRefresh();
        // Initialize Current
        seekBar = $(R.id.current_song_info_progress);
        albumImageView = $(R.id.current_song_info_album_image);
        titleView = $(R.id.current_song_info_title);
        singerView = $(R.id.current_song_info_singer);
        buttonNext = $(R.id.button_next_music);
        buttonNext.setOnClickListener(listener);
        seekBar.setOnSeekBarChangeListener(listener);
        refreshCurrentState();
    }

    private void initToolbar() {
        toolbar = $(R.id.toolbar_in_activity);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    private void initDrawer() {
        drawerLayout = $(R.id.dl_main_drawer);
        navigationView = $(R.id.nv_main_navigation);
        navigationManager = new NavigationManager(this, drawerLayout, navigationView, R.id.nav_local_music);
        navigationManager.initialize();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initData() {
        listener.onRefresh();
    }

    private void refreshCurrentState() {
        if (CurrentPlay.instance.data != null) {
            ImageLoader.getInstance().loadImage(CurrentPlay.instance.data.albumPicSmall, albumImageView);
            titleView.setText(CurrentPlay.instance.data.songName);
            singerView.setText(CurrentPlay.instance.data.singerName);
            seekBar.setMax(MusicPlayServiceConnection.getMediaPlayer().getDuration());
            ThreadUtils.runOnNewThread(this, new Runnable() {
                @Override
                public void run() {
                    if (MusicPlayServiceConnection.getMediaPlayer() != null) {
                        try {
                            seekBar.setProgress(MusicPlayServiceConnection.getMediaPlayer().getCurrentPosition());
                        } catch (Exception e) {
                            StandardUtils.printStack(e);
                        }
                        handler.postDelayed(this, 1000);
                    }
                }
            });
        } else {
            albumImageView.setImageResource(R.drawable.default_album);
            titleView.setText(getResources().getString(R.string.app_name));
            singerView.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.navigationManager = null;
        MusicPlayService.unbind(this);
    }

    class Listener implements SongItemAdapter.OnMoreInfoOptionButtonClickListener, HaruueAdapter.OnItemClickListener<SongModel>, SwipeRefreshLayout.OnRefreshListener, MusicDatabaseHelper.MusicDatabaseHelperListener, RequestPermissionListener, MusicPlayerController.OnCurrentPlayMusicChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, HaruueAdapter.OnItemLongClickListener<SongModel> {

        /**
         * 正数为增加，负数为删除
         */
        HashMap<Integer, SongModel> songModelWaitToChange = new HashMap<>(0);

        @Override
        public void onMoreInfoOptionButtonClickListener(View itemView, int position, SongModel model) {

        }

        @Override
        public void onItemClick(int position, View view, SongModel model) {
            MusicPlayerController.play(model);
        }

        @Override
        public void onRefresh() {
            MusicDatabaseHelper.queryAll(this, 0);
        }

        @Override
        public void onMusicDatabaseQueryResult(ArrayList<SongModel> songModels, int requestCode) {
            adapter.clear();
            if (songModels.isEmpty()) {
                playListView.showEmpty();
            } else {
                adapter.addAll(songModels);
            }
            playListView.getSwipeRefreshLayout().setRefreshing(false);
            CurrentPlayList.instance.playList = songModels;
            playListView.getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        public void onMusicDatabaseSqlExecComplete(int requestCode) {
            if (requestCode < 0) {
                adapter.remove(songModelWaitToChange.get(requestCode));
                CurrentPlayList.instance.playList.remove(songModelWaitToChange.get(requestCode));
            } else {
                adapter.add(songModelWaitToChange.get(requestCode));
                CurrentPlayList.instance.playList.add(songModelWaitToChange.get(requestCode));
            }
            songModelWaitToChange.remove(requestCode);
            playListView.getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        public void onMusicDatabaseOperateFailure(Throwable t, int requestCode) {
            StandardUtils.toast(R.string.operate_failure);
            songModelWaitToChange.remove(requestCode);
            playListView.showError();
            playListView.getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        public void onPermissionGranted(String[] permissions) {

        }

        @Override
        public void onPermissionDenied(String[] permissions) {
            StandardUtils.toast(R.string.permission_denied_exception);
            ActivityManager.finishAllActivity();
        }

        @Override
        public void onCurrentPlayMusicChange() {
            refreshCurrentState();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_next_music:
                    MusicPlayerController.next();
                    break;
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (MusicPlayServiceConnection.getMediaPlayer() != null && fromUser) {
                MusicPlayerController.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public boolean onItemLongClick(int position, View view, SongModel model) {
            return true;
        }
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
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }
}
