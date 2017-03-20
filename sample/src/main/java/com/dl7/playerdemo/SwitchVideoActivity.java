package com.dl7.playerdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.bumptech.glide.Glide;
import com.dl7.player.media.IjkPlayerView;
import com.dl7.tag.TagLayout;
import com.dl7.tag.TagView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class SwitchVideoActivity extends AppCompatActivity {

    private static final String IMAGE_URL = "http://vimg3.ws.126.net/image/snapshot/2015/5/J/M/VAPRJCSJM.jpg";

    private final String[] mVideoPath = new String[]{
            "http://flv2.bn.netease.com/videolib3/1505/29/DCNOo7461/SD/DCNOo7461-mobile.mp4",
            "http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4",
            "http://flv2.bn.netease.com/videolib3/1611/28/GbgsL3639/SD/movie_index.m3u8",
    };
    private final String[] mTitle = new String[]{
            "视频1", "视频2", "视频3",
    };

    private IjkPlayerView mPlayerView;
    private TagLayout mTagLayout;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_video);
        mPlayerView = (IjkPlayerView) findViewById(R.id.player_view);
        mTagLayout = (TagLayout) findViewById(R.id.tag_layout);

        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .setTitle(mTitle[mIndex])
                .enableDanmaku()
                .setDanmakuSource(getResources().openRawResource(R.raw.bili))
                .setVideoPath(mVideoPath[mIndex]);

        mPlayerView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                mIndex++;
                if (mIndex != mTitle.length) {
                    mPlayerView.switchVideoPath(mVideoPath[mIndex])
                            .setTitle(mTitle[mIndex])
                            .enableDanmaku(false)
                            .start();
                    mTagLayout.setCheckTag(mIndex);
                }
            }
        });

        mTagLayout.setTags(mTitle);
        mTagLayout.setCheckTag(0);
        mTagLayout.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean isCheck) {
                if (isCheck && mIndex != i) {
                    mIndex = i;
                    mPlayerView.switchVideoPath(mVideoPath[mIndex])
                            .setTitle(mTitle[mIndex])
                            .enableDanmaku()
                            .setDanmakuSource(getResources().openRawResource(R.raw.bili))
                            .start();
                }
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
