package com.dl7.player;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

import com.dl7.playerview.media.PlayerView;

public class IjkPlayerActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";

    Toolbar mToolbar;
    private PlayerView mPlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPlayerView = (PlayerView) findViewById(R.id.player_view);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Video Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPlayerView.initVideoPlayer(VIDEO_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPlayerView.configurationChanged(newConfig);
        Log.w("TTAG", "onConfigurationChanged " + newConfig.orientation);
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
        super.onBackPressed();
    }
}
