package com.dl7.playerdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.dl7.player.media.IjkPlayerView;
import com.dl7.player.utils.SDCardUtils;
import com.dl7.playerdemo.adapter.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class TestAspectActivity extends AppCompatActivity {

    private static final String VIDEO_URL_2 = "http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4";

    private static final String VIDEO_URL = "http://flv2.bn.netease.com/videolib3/1505/29/DCNOo7461/SD/DCNOo7461-mobile.mp4";
    private static final String IMAGE_URL = "http://vimg3.ws.126.net/image/snapshot/2015/5/J/M/VAPRJCSJM.jpg";

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

        String path = SDCardUtils.getRootPath() + "/360/2.rmvb";
        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .setTitle("美加州死亡谷石头会走路")
                .setVideoPath(VIDEO_URL);
        mPlayerView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.e("TestAspectActivity", "onCompletion");
                mPlayerView.switchVideo(VIDEO_URL_2)
                        .setTitle("这是个跑马灯TextView，标题要足够长才会跑。-(゜ -゜)つロ 乾杯~")
                        .start();
            }
        });
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
