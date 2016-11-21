package com.dl7.playerdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dl7.player.media.IjkPlayerView;

public class IjkPlayerActivity extends AppCompatActivity {

//    private static final String VIDEO_URL = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
//    private static final String VIDEO_URL_HD = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
    private static final String VIDEO_URL = "http://baobab.wdjcdn.com/14564977406580.mp4";
    private static final String VIDEO_URL_HD = "http://baobab.wdjcdn.com/14564977406580.mp4";
    private static final String IMAGE_URL = "http://i2.hdslb.com/video/46/46f6bb84ffdfedaa1973b5595322178d.jpg";
    Toolbar mToolbar;
    private IjkPlayerView mPlayerView;
    private EditText mEditText;
    private ImageView mIvSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPlayerView = (IjkPlayerView) findViewById(R.id.player_view);
        mEditText = (EditText) findViewById(R.id.et_danmaku_text);
        mIvSend = (ImageView) findViewById(R.id.iv_send_danmaku);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Video Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .enableDanmaku()
                .setSkipTip(1000*60*2)
                .setTitle("这是个跑马灯TextView，标题要足够长才会跑。-(゜ -゜)つロ 乾杯~")
                .setVideoSource(null, null, VIDEO_URL, VIDEO_URL_HD, null)
                .setDanmakuSource(getResources().openRawResource(R.raw.comments))
                .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_SUPER);

        mIvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerView.sendDanmaku(mEditText.getText().toString(), false);
                mEditText.setText("");
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
