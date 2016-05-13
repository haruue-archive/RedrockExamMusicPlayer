package moe.haruue.redrockexam.musicplayer.ui.activity;

import android.os.Bundle;

import moe.haruue.redrockexam.musicplayer.R;
import moe.haruue.redrockexam.ui.recyclerview.HaruueRecyclerView;
import moe.haruue.redrockexam.util.abstracts.HaruueActivity;

public class MainActivity extends HaruueActivity {

    HaruueRecyclerView playListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
