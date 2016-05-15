package moe.haruue.redrockexam.musicplayer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.database.helper.MusicDatabaseHelper;
import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.musicplayer.data.model.SongSearchModel;
import moe.haruue.redrockexam.musicplayer.data.network.helper.SongSearchApiHelper;
import moe.haruue.redrockexam.musicplayer.ui.adapter.SongItemAdapter;
import moe.haruue.redrockexam.musicplayer.ui.navigation.NavigationManager;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayService;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayerController;
import moe.haruue.redrockexam.ui.recyclerview.HaruueAdapter;
import moe.haruue.redrockexam.ui.recyclerview.HaruueRecyclerView;
import moe.haruue.redrockexam.util.ActivityManager;
import moe.haruue.redrockexam.util.StandardUtils;
import moe.haruue.redrockexam.util.abstracts.HaruueActivity;

public class SearchActivity extends HaruueActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavigationManager navigationManager;
    HaruueRecyclerView searchResultView;
    SongItemAdapter adapter;
    TextInputEditText keywordEditText;
    ImageView searchButton;
    View moreView;
    Listener listener = new Listener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
    }

    private void initView() {
        initToolbar();
        initDrawer();
        keywordEditText = $(R.id.edit_text_search_keyword);
        searchButton = $(R.id.button_search);
        searchButton.setOnClickListener(listener);
        searchResultView = $(R.id.search_result_view);
        searchResultView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongItemAdapter(this);
        adapter.setAutoNotify(true);
        adapter.setOnItemClickListener(listener);
        searchResultView.setAdapter(adapter);
        moreView = View.inflate(this, R.layout.view_more, null);
        adapter.setMoreView(moreView);
        adapter.setNoMoreView(R.layout.view_no_more);
        adapter.setOnLoadMoreListener(listener);
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
        navigationManager = new NavigationManager(this, drawerLayout, navigationView, R.id.nav_search);
        navigationManager.initialize();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initData() {

    }

    private void doSearch(int page) {
        listener.keyword = keywordEditText.getText().toString();
        SongSearchApiHelper.getSongSearchResult(this, listener.keyword, page, listener);
    }

    class Listener implements View.OnClickListener, SongSearchApiHelper.SongSearchApiHelperListener, HaruueAdapter.OnLoadMoreListener, HaruueAdapter.OnItemClickListener<SongModel>,MusicDatabaseHelper.MusicDatabaseHelperListener {

        int currentPage = 0;
        String keyword;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_search:
                    adapter.clear();
                    currentPage = 0;
                    doSearch(SongSearchApiHelper.FIRST_PAGE);
                    break;
            }
        }

        @Override
        public void onGetSongSearchResultSuccess(SongSearchModel.ResBody.PageBean searchPage) {
            currentPage = searchPage.currentPage;
            adapter.addAll(searchPage.getSongModelList());
            if (currentPage == searchPage.allPages) {
                adapter.getFooterGroup().removeAllViews();
                adapter.showNoMore();
            } else {
                adapter.getFooterGroup().removeAllViews();
                adapter.showMore();
                moreView.findViewById(R.id.button_load_more).setVisibility(View.VISIBLE);
                moreView.findViewById(R.id.view_loading_more).setVisibility(View.GONE);
                adapter.setOnLoadMoreListener(this);
            }
        }

        @Override
        public void onGetSongSearchResultFailure(int code, String error) {
            adapter.getFooterGroup().removeAllViews();
            adapter.showNoMore();
        }

        @Override
        public void onLoadMore(int currentPosition) {
            moreView.findViewById(R.id.button_load_more).setVisibility(View.GONE);
            moreView.findViewById(R.id.view_loading_more).setVisibility(View.VISIBLE);
            moreView.setOnClickListener(null);
            SongSearchApiHelper.getSongSearchResult(SearchActivity.this, keyword, currentPage + 1, this);
        }

        @Override
        public void onItemClick(int position, View view, SongModel model) {
            MusicPlayerController.play(model);
            MusicDatabaseHelper.insert(model, this, 0);
        }

        @Override
        public void onMusicDatabaseQueryResult(ArrayList<SongModel> songModels, int requestCode) {

        }

        @Override
        public void onMusicDatabaseSqlExecComplete(int requestCode) {

        }

        @Override
        public void onMusicDatabaseOperateFailure(Throwable t, int requestCode) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayService.unbind(this);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchActivity.class);
        context.startActivity(starter);
    }
}
