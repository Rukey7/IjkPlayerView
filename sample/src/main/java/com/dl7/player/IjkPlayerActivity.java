package com.dl7.player;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

import com.dl7.playerview.media.PlayerView;

public class IjkPlayerActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    private static final String VIDEO_URL_HD = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
    private static final String IMAGE_URL = "http://cdn.pcbeta.attachment.inimc.com/data/attachment/forum/201205/15/073132nwznnmjixknqw0wj.jpg";
    Toolbar mToolbar;
    private PlayerView mPlayerView;
    private EditText mEditText;
    private ImageView mIvSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = getLayoutInflater().from(this).inflate(R.layout.activity_ijk_player, null);
        setContentView(rootView);
        /**虚拟按键的隐藏方法*/
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
//                //比较Activity根布局与当前布局的大小
//                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
//                if (heightDiff > 100) {
//                    //大小超过100时，一般为显示虚拟键盘事件
//                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                } else {
//                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
//                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                }
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPlayerView = (PlayerView) findViewById(R.id.player_view);
        mEditText = (EditText) findViewById(R.id.et_danmaku_text);
        mIvSend = (ImageView) findViewById(R.id.iv_send_danmaku);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Video Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
//        mPlayerView.init().setVideoPath(VIDEO_URL).start();
        mPlayerView.init()
                .setVideoSource(null, null, VIDEO_URL, VIDEO_URL_HD, null)
                .enableDanmaku()
//                .setDanmakuSource(getResources().openRawResource(R.raw.comments))
                .setMediaQuality(PlayerView.MEDIA_QUALITY_SUPER);

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
