package moe.haruue.redrockexam.musicplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.musicplayer.data.database.helper.MusicDatabaseHelper;
import moe.haruue.redrockexam.musicplayer.data.model.SongModel;
import moe.haruue.redrockexam.musicplayer.data.model.TopModel;
import moe.haruue.redrockexam.musicplayer.data.network.helper.TopMusicApiHelper;
import moe.haruue.redrockexam.musicplayer.ui.adapter.SongItemAdapter;
import moe.haruue.redrockexam.musicplayer.ui.service.MusicPlayerController;
import moe.haruue.redrockexam.ui.recyclerview.HaruueAdapter;
import moe.haruue.redrockexam.ui.recyclerview.HaruueRecyclerView;
import moe.haruue.redrockexam.util.StandardUtils;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
public class TopMusicFragment extends Fragment {

    HaruueRecyclerView topMusicView;
    SongItemAdapter adapter;
    Listener listener = new Listener();
    int topId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_music, container, false);
        topMusicView = StandardUtils.$(view, R.id.recycler_view_top_music);
        initView();
        initData();
        return view;
    }

    public TopMusicFragment setTopId(int topId) {
        this.topId = topId;
        return this;
    }

    private void initView() {
        topMusicView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SongItemAdapter(getActivity());
        adapter.setAutoNotify(true);
        adapter.setOnItemClickListener(listener);
        topMusicView.setAdapter(adapter);
        topMusicView.setOnRefreshListener(listener);
        topMusicView.refresh();
    }

    private void initData() {

    }

    class Listener implements SwipeRefreshLayout.OnRefreshListener, TopMusicApiHelper.TopMusicApiHelperListener, HaruueAdapter.OnItemClickListener<SongModel>,MusicDatabaseHelper.MusicDatabaseHelperListener {

        @Override
        public void onRefresh() {
            TopMusicApiHelper.getTopMusicList(TopMusicFragment.this, topId, this);
        }

        @Override
        public void onGetTopMusicSuccess(ArrayList<TopModel.ResBody.PageBean.SongItem> songList) {
            adapter.clear();
            adapter.addAll(SongModel.getSongModelListFromTopSongItemList(songList));
            topMusicView.getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        public void onGetTopMusicFailure(int code, String error) {
            StandardUtils.toast(error);
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
}
