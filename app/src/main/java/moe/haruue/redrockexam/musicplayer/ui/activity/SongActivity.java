package moe.haruue.redrockexam.musicplayer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.com.caoyue.imageloader.ImageLoader;
import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlayList;
import moe.haruue.redrockexam.musicplayer.ui.navigation.NavigationManager;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayService;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayServiceConnection;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayerController;
import moe.haruue.redrockexam.util.ActivityManager;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.ThreadUtils;
import moe.haruue.redrockexam.util.abstracts.HaruueActivity;

public class SongActivity extends HaruueActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavigationManager navigationManager;

    TextView titleView;
    TextView singerView;
    ImageView albumView;
    ImageView playModeButton;
    ImageView previousButton;
    ImageView nextButton;
    ImageView playButton;
    ImageView pauseButton;
    SeekBar seekBar;

    Listener listener = new Listener();
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        handler = new Handler(getMainLooper());
        // Single Task
        try {
            ActivityManager.finishPreviousActivity();
        } catch (Exception e) {
            StandardUtils.printStack(e);
        }
        // Service
        MusicPlayService.start(this);
        MusicPlayService.bind(this);
        initView();
        MusicPlayerController.addToCurrentPlayMusicListeners(listener);
    }

    private void initView() {
        initToolbar();
        initDrawer();
        titleView = $(R.id.song_title);
        singerView = $(R.id.song_singer);
        albumView = $(R.id.song_album_image_big);
        playModeButton = $(R.id.song_button_play_mode);
        playModeButton.setOnClickListener(listener);
        previousButton = $(R.id.song_button_previous);
        previousButton.setOnClickListener(listener);
        nextButton = $(R.id.song_button_next);
        nextButton.setOnClickListener(listener);
        playButton = $(R.id.song_button_play);
        playButton.setOnClickListener(listener);
        pauseButton = $(R.id.song_button_pause);
        pauseButton = $(R.id.song_button_pause);
        seekBar = $(R.id.song_progress);
        seekBar.setOnSeekBarChangeListener(listener);
        refreshState();
    }

    private void refreshState() {
        if (CurrentPlay.instance.data != null) {
            ImageLoader.getInstance().loadImage(CurrentPlay.instance.data.albumPicBig, albumView);
            titleView.setText(CurrentPlay.instance.data.songName);
            singerView.setText(CurrentPlay.instance.data.singerName);
            setPlayButtonState(MusicPlayServiceConnection.getMediaPlayer().isPlaying());
            seekBar.setMax(MusicPlayServiceConnection.getMediaPlayer().getDuration());
            ThreadUtils.runOnNewThread(this, new Runnable() {
                @Override
                public void run() {
                    if (MusicPlayServiceConnection.getMediaPlayer() != null) {
                        seekBar.setProgress(MusicPlayServiceConnection.getMediaPlayer().getCurrentPosition());
                        handler.postDelayed(this, 1000);
                    }
                }
            });
        } else {
            albumView.setImageResource(R.drawable.default_album);
            titleView.setText(getResources().getString(R.string.app_name));
            singerView.setText("");
            setPlayButtonState(false);
        }
        refreshPlayModeButtonState();
    }

    private void setPlayButtonState(boolean isPlaying) {
        if (isPlaying) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
    }

    private void refreshPlayModeButtonState() {
        switch (CurrentPlayList.instance.mode) {
            case CurrentPlayList.PlayMode.LOOP_ONE_SONG:
                playModeButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                break;
            case CurrentPlayList.PlayMode.ORDER_LIST:
                playModeButton.setImageResource(R.drawable.ic_repeat_black_24dp);
                break;
            case CurrentPlayList.PlayMode.RANDOM:
                playModeButton.setImageResource(R.drawable.ic_shuffle_black_24dp);
                break;
        }
    }

    private void initToolbar() {
        toolbar = $(R.id.toolbar_in_activity);
        toolbar.setTitle(R.string.playing);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    private void initDrawer() {
        drawerLayout = $(R.id.dl_main_drawer);
        navigationView = $(R.id.nv_main_navigation);
        navigationManager = new NavigationManager(this, drawerLayout, navigationView, R.id.nav_current_play);
        navigationManager.initialize();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    class Listener implements View.OnClickListener, MusicPlayerController.OnCurrentPlayMusicChangeListener, SeekBar.OnSeekBarChangeListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.song_button_next:
                    MusicPlayerController.next();
                    break;
                case R.id.song_button_pause:
                    MusicPlayerController.pause();
                    break;
                case R.id.song_button_play:
                    MusicPlayerController.play();
                    break;
                case R.id.song_button_play_mode:
                    onChangePlayMode();
                    break;
                case R.id.song_button_previous:
                    MusicPlayerController.previous();
                    break;
            }
        }

        public void onChangePlayMode() {
            if (CurrentPlayList.instance.mode >= 2) {
                CurrentPlayList.instance.mode = 0;
            } else {
                CurrentPlayList.instance.mode++;
            }
            refreshPlayModeButtonState();
        }

        @Override
        public void onCurrentPlayMusicChange() {
            refreshState();
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
        Intent starter = new Intent(context, SongActivity.class);
        context.startActivity(starter);
    }

}
