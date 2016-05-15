package moe.haruue.redrockexam.musicplayer.ui.navigation;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.caoyue.imageloader.ImageLoader;
import cn.com.caoyue.imageloader.ImageLoaderListener;
import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentBitmap;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.musicplayer.ui.activity.MainActivity;
import moe.haruue.redrockexam.musicplayer.ui.activity.SearchActivity;
import moe.haruue.redrockexam.musicplayer.ui.activity.SongActivity;
import moe.haruue.redrockexam.musicplayer.ui.activity.TopMusicActivity;
import moe.haruue.redrockexam.musicplayer.ui.service.DownloadService;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayService;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayServiceConnection;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayerController;
import moe.haruue.redrockexam.ui.widget.CircleImageView;
import moe.haruue.redrockexam.util.ActivityManager;
import moe.haruue.redrockexam.util.StandardUtils;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class NavigationManager {

    Context context;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Listener listener = new Listener();


    public NavigationManager(Context context, DrawerLayout drawerLayout, NavigationView navigationView, @IdRes int defaultCheckedItem) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        navigationView.setCheckedItem(defaultCheckedItem);
    }

    public void initialize() {
        initHeader();
        navigationView.setNavigationItemSelectedListener(listener);
        MusicPlayerController.addToCurrentPlayMusicListeners(listener);
    }

    public class Listener implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, MusicPlayerController.OnCurrentPlayMusicChangeListener, ImageLoaderListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_local_music:
                    if (!context.getClass().getName().equals(MainActivity.class.getName())) {
                        MainActivity.start(context);
                    }
                    break;
                case R.id.navigation_header_container_song_album_picture:
                    if (!context.getClass().getName().equals(SongActivity.class.getName())) {
                        SongActivity.start(context);
                    }
                case R.id.nav_current_play:
                    if (!context.getClass().getName().equals(SongActivity.class.getName())) {
                        SongActivity.start(context);
                    }
                    break;
                case R.id.nav_hot:
                    if (!context.getClass().getName().equals(TopMusicActivity.class.getName())) {
                        TopMusicActivity.start(context);
                    }
                    break;
                case R.id.nav_search:
                    if (!context.getClass().getName().equals(SearchActivity.class.getName())) {
                        SearchActivity.start(context);
                    }
                    break;
                case R.id.nav_exit:
                    MusicPlayServiceConnection.getMediaPlayer().stop();
                    MusicPlayService.stop(context);
                    try {
                        MusicPlayServiceConnection.getInstance().getBinder().cancelNotification();
                    } catch (Exception e) {
                        StandardUtils.printStack(e);
                    }
                    DownloadService.stop(context);
                    ActivityManager.exitApplication();
                    break;

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigation_header_container_song_album_picture:
                    SongActivity.start(context);
                    break;
                case R.id.navigation_header_container_button_previous:
                    MusicPlayerController.previous();
                    break;
                case R.id.navigation_header_container_button_play:
                    MusicPlayerController.play();
                    break;
                case R.id.navigation_header_container_button_pause:
                    MusicPlayerController.pause();
                    break;
                case R.id.navigation_header_container_button_next:
                    MusicPlayerController.next();
                    break;
            }

        }

        @Override
        public void onCurrentPlayMusicChange() {
            refreshHeader();
        }

        @Override
        public void onImageLoadSuccess(String url) {
            songAlbumPictureImageView.setDrawingCacheEnabled(true);
            CurrentBitmap.bitmap = ((BitmapDrawable) songAlbumPictureImageView.getDrawable()).getBitmap();
            songAlbumPictureImageView.setDrawingCacheEnabled(false);
        }

        @Override
        public void onImageLoadFailure(String url, Throwable t) {

        }

        @Override
        public void onImageLoadCancel(String url) {

        }
    }

    // View in Header
    TextView songTitleTextView;
    CircleImageView songAlbumPictureImageView;
    ImageView previousButton;
    ImageView playButton;
    ImageView pauseButton;
    ImageView nextButton;

    private void initHeader() {
        songTitleTextView = StandardUtils.$(navigationView.getHeaderView(0), R.id.navigation_header_container_song_title);
        songAlbumPictureImageView = StandardUtils.$(navigationView.getHeaderView(0), R.id.navigation_header_container_song_album_picture);
        songAlbumPictureImageView.setOnClickListener(listener);
        previousButton = StandardUtils.$(navigationView.getHeaderView(0), R.id.navigation_header_container_button_previous);
        previousButton.setOnClickListener(listener);
        playButton = StandardUtils.$(navigationView.getHeaderView(0), R.id.navigation_header_container_button_play);
        playButton.setOnClickListener(listener);
        pauseButton = StandardUtils.$(navigationView.getHeaderView(0), R.id.navigation_header_container_button_pause);
        pauseButton.setOnClickListener(listener);
        nextButton = StandardUtils.$(navigationView.getHeaderView(0), R.id.navigation_header_container_button_next);
        nextButton.setOnClickListener(listener);
        refreshHeader();
    }

    public void refreshHeader() {
        if (CurrentPlay.instance.data != null) {
            songTitleTextView.setText(CurrentPlay.instance.data.songName);
            ImageLoader.getInstance().loadImage(CurrentPlay.instance.data.albumPicSmall, songAlbumPictureImageView, listener);
            setPlayButtonStatus(MusicPlayServiceConnection.getMediaPlayer().isPlaying());
        } else {
            songTitleTextView.setText(R.string.app_name);
            ImageLoader.getInstance().loadImage("", songAlbumPictureImageView);
            setPlayButtonStatus(false);
        }
    }

    public void setPlayButtonStatus(boolean isPlaying) {
        if (isPlaying) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
    }

}
