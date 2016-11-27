package com.dl7.playerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.bumptech.glide.Glide;
import com.dl7.player.media.IjkPlayerView;

public class IjkFullscreenActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://cn-hbcd-cu-v-02.acgvideo.com/vg4/f/f2/11505291-1-hd.mp4?expires=1480257000&ssig=sYWHwAnfgU_ZcU7dNSvM9Q&oi=1866712164&rate=3100000";
    private static final String IMAGE_URL = "http://i0.hdslb.com/bfs/archive/014d7bb085edbf014ac179d382841b921a94cc76.jpg_320x200.jpg";
    IjkPlayerView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayerView = new IjkPlayerView(this);
        setContentView(mPlayerView);
        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .alwaysFullScreen()
                .enableOrientation()
                .enableDanmaku()
                .setDanmakuSource(getResources().openRawResource(R.raw.audi_r8))
                .setTitle("(1080P)全球仅一辆，《最终幻想15》 限定版奥迪R8 -----“路西斯之星”")
                .setVideoSource(null, null, null, null, VIDEO_URL)
                .start();
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
