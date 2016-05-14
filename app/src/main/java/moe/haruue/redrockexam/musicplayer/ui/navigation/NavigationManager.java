package moe.haruue.redrockexam.musicplayer.ui.navigation;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.caoyue.imageloader.ImageConfig;
import cn.com.caoyue.imageloader.ImageLoader;
import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.storage.CurrentPlay;
import moe.haruue.redrockexam.musicplayer.ui.activity.MainActivity;
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
    }

    public class Listener implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_local_music:
                    if (!context.getClass().getName().equals(MainActivity.class.getName())) {
                        MainActivity.start(context);
                    }
                    break;
                case R.id.nav_hot:
                    break;
                case R.id.nav_search:
                    break;
                case R.id.nav_setting:
                    break;
                case R.id.nav_exit:
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
                    break;
                case R.id.navigation_header_container_button_previous:
                    break;
                case R.id.navigation_header_container_button_play:
                    break;
                case R.id.navigation_header_container_button_pause:
                    break;
                case R.id.navigation_header_container_button_next:
                    break;
            }

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
        songTitleTextView = StandardUtils.$(navigationView, R.id.navigation_header_container_song_title);
        songAlbumPictureImageView = StandardUtils.$(navigationView, R.id.navigation_header_container_song_album_picture);
        songAlbumPictureImageView.setOnClickListener(listener);
        previousButton = StandardUtils.$(navigationView, R.id.navigation_header_container_button_previous);
        previousButton.setOnClickListener(listener);
        playButton = StandardUtils.$(navigationView, R.id.navigation_header_container_button_play);
        playButton.setOnClickListener(listener);
        pauseButton = StandardUtils.$(navigationView, R.id.navigation_header_container_button_pause);
        pauseButton.setOnClickListener(listener);
        nextButton = StandardUtils.$(navigationView, R.id.navigation_header_container_button_next);
        nextButton.setOnClickListener(listener);
        refreshHeader();
    }

    public void refreshHeader() {
        if (CurrentPlay.instance.data != null) {
            songTitleTextView.setText(CurrentPlay.instance.data.songName);
            ImageLoader.getInstance().loadImage(CurrentPlay.instance.data.albumPicSmall, songAlbumPictureImageView, new ImageConfig().setDrawableOnLoading(R.drawable.ic_music_note_white_24dp).setDrawableOnFailure(R.drawable.ic_music_note_white_24dp));
            setPlayButtonStatus(CurrentPlay.instance.isPlaying);
        } else {
            songTitleTextView.setText(R.string.app_name);
            songAlbumPictureImageView.setImageBitmap(StandardUtils.getDrawableResourceAsBitmap(R.drawable.ic_music_note_white_24dp));
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
