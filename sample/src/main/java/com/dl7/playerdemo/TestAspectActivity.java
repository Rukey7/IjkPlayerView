package com.dl7.playerdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.dl7.player.media.IjkPlayerView;
import com.dl7.playerdemo.adapter.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TestAspectActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://cn-hbcd-cu-v-02.acgvideo.com/vg5/2/95/11679028-1-hd.mp4?expires=1480265100&ssig=H3cXzQwX0TUJpR3RlP8L2A&oi=1866712258&rate=3100000";
    private static final String IMAGE_URL = "http://i0.hdslb.com/bfs/archive/d7a0316a67fccded122fae5d95f12f2c8e3e07f4.jpg_320x200.jpg";

    private IjkPlayerView mPlayerView;
    private ListView mListView;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_aspect);
        mPlayerView = (IjkPlayerView) findViewById(R.id.player_view);
        mListView = (ListView) findViewById(R.id.lv_list);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            list.add("Some message " + i);
        }
        mAdapter = new ListAdapter(this, list);
        mListView.setAdapter(mAdapter);

        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .setTitle("这是个跑马灯TextView，标题要足够长才会跑。-(゜ -゜)つロ 乾杯~")
                .setVideoPath(VIDEO_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPlayerView.configurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlayerView.handleVolumeKey(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mPlayerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
