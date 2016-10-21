package com.dl7.ijkplayersample;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dl7.ijkplayersample.utils.WindowUtils;
import com.dl7.ijkplayersample.widget.media.IjkVideoView;
import com.dl7.ijkplayersample.widget.media.PlayStateParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.GestureDetector.OnGestureListener;
import static android.view.GestureDetector.SimpleOnGestureListener;
import static android.widget.SeekBar.OnSeekBarChangeListener;
import static com.dl7.ijkplayersample.utils.StringUtils.generateTime;
import static com.dl7.ijkplayersample.utils.StringUtils.getFormatSize;

public class PlayerActivity extends AppCompatActivity {

    private static final String PIC_URL = "http://vimg1.ws.126.net/image/snapshot/2016/10/R/A/VC2O4D0RA.jpg";
    private static final String VIDEO_URL = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    private static final int MAX_VIDEO_SEEK = 1000;
    private static final int DEFAULT_HIDE_TIMEOUT = 3000;
    private static final int MSG_UPDATE_SEEK = 10086;

    @BindView(R.id.video_view)
    IjkVideoView mVideoView;
    @BindView(R.id.iv_thumb)
    ImageView mIvThumb;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.ll_loading)
    LinearLayout mLlLoading;
    @BindView(R.id.tv_volume)
    TextView mTvVolume;
    @BindView(R.id.tv_brightness)
    TextView mTvBrightness;
    @BindView(R.id.tv_fast_forward)
    TextView mTvFastForward;
    @BindView(R.id.tv_fast_rewind)
    TextView mTvFastRewind;
    @BindView(R.id.fl_touch_layout)
    FrameLayout mFlTouchLayout;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.fullscreen_top_bar)
    LinearLayout mFullscreenTopBar;
    @BindView(R.id.iv_back_window)
    ImageView mIvBackWindow;
    @BindView(R.id.window_top_bar)
    FrameLayout mWindowTopBar;
    @BindView(R.id.iv_play)
    ImageView mIvPlay;
    @BindView(R.id.tv_cur_time)
    TextView mTvCurTime;
    @BindView(R.id.player_seek)
    SeekBar mPlayerSeek;
    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.iv_fullscreen)
    ImageView mIvFullscreen;
    @BindView(R.id.ll_bottom_bar)
    LinearLayout mLlBottomBar;
    @BindView(R.id.fl_video_box)
    FrameLayout mFlVideoBox;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_SEEK) {
                final int pos = _setProgress();
                if (!mIsSeeking && mIsShowBar && mVideoView.isPlaying()) {
                    // 这里会重复发送MSG，已达到实时更新 Seek 的效果
                    msg = obtainMessage(MSG_UPDATE_SEEK);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                }
            }
        }
    };
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private boolean isForbidTouch;
    private boolean mIsShowBar = true;
    private boolean mIsFullscreen;
    private boolean mIsSeeking;
    private long mTargetPosition = -1;
    private int mCurVolume = -1;
    private int mInitHeight;
    private int mWidthPixels;
    private int mScreenUiVisibilily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Video Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _initMediaPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.destroy();
    }

    private void _initMediaPlayer() {
        //
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        // 声音
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 亮度
        try {
            int e = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float progress = 1.0F * (float) e / 255.0F;
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = progress;
            getWindow().setAttributes(layout);
        } catch (Settings.SettingNotFoundException var7) {
            var7.printStackTrace();
        }
        // 进度
        mPlayerSeek.setMax(MAX_VIDEO_SEEK);
        mPlayerSeek.setOnSeekBarChangeListener(mSeekListener);
//        hideAllView();
        // 图片
        Glide.with(this).load(PIC_URL).into(mIvThumb);
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
                    mTvSpeed.setText(getFormatSize(extra));
                }
                switchStatus(what);
                return true;
            }
        });
//        mVideoView.start();
        //
        final GestureDetector gestureDetector = new GestureDetector(this, mPlayerGestureListener);
        mFlVideoBox.setClickable(true);
        mFlVideoBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mHandler.removeCallbacks(mHideBarRunnable);
                }
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                    _endGesture();
                }
                return false;
            }
        });
        mInitHeight = mFlVideoBox.getLayoutParams().height;
        mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        Log.e("TTAG", mWidthPixels + " - " + mInitHeight);
        // 启动
        mVideoView.setVideoPath(VIDEO_URL);
        mVideoView.seekTo(0);
        //
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    private void hideAllView() {
        mIvThumb.setVisibility(View.GONE);
        mLlLoading.setVisibility(View.GONE);
        mFlTouchLayout.setVisibility(View.GONE);
        mFullscreenTopBar.setVisibility(View.GONE);
        mWindowTopBar.setVisibility(View.GONE);
        mLlBottomBar.setVisibility(View.GONE);
    }

    private void toggleControlBar() {
        mIsShowBar = !mIsShowBar;
        mLlBottomBar.setVisibility(mIsShowBar ? View.VISIBLE : View.GONE);
        if (mIsFullscreen) {
            mFullscreenTopBar.setVisibility(mIsShowBar ? View.VISIBLE : View.GONE);
            mWindowTopBar.setVisibility(View.GONE);
        } else {
            mWindowTopBar.setVisibility(mIsShowBar ? View.VISIBLE : View.GONE);
            mFullscreenTopBar.setVisibility(View.GONE);
        }
        if (mIsShowBar) {
            mHandler.postDelayed(mHideBarRunnable, DEFAULT_HIDE_TIMEOUT);
            mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        }
    }

    private void _showControlBar(int timeout) {
        if (!mIsShowBar) {
            _setProgress();
            mIvPlay.requestFocus();
            mIsShowBar = true;
        }
        mLlBottomBar.setVisibility(View.VISIBLE);
        if (mIsFullscreen) {
            mFullscreenTopBar.setVisibility(View.VISIBLE);
            mWindowTopBar.setVisibility(View.GONE);
        } else {
            mWindowTopBar.setVisibility(View.VISIBLE);
            mFullscreenTopBar.setVisibility(View.GONE);
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        mHandler.removeCallbacks(mHideBarRunnable);
        if (timeout != 0) {
            mHandler.postDelayed(mHideBarRunnable, timeout);
        }
    }

    private void _endGesture() {
        if (mTargetPosition >= 0 && mTargetPosition != mVideoView.getCurrentPosition()) {
            mVideoView.seekTo((int) mTargetPosition);
            mPlayerSeek.setProgress((int) (mTargetPosition * MAX_VIDEO_SEEK / mVideoView.getDuration()));
            mTargetPosition = -1;
        }
        _hideTouchView();
    }

    private void switchStatus(int status) {
        Log.e("PlayerActivity", "" + status);
        switch (status) {
            case PlayStateParams.STATE_PREPARING:
            case PlayStateParams.MEDIA_INFO_BUFFERING_START:
//                hideAllView();
                mLlLoading.setVisibility(View.VISIBLE);
                break;

            case PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START:
            case PlayStateParams.STATE_PLAYING:
            case PlayStateParams.STATE_PREPARED:
            case PlayStateParams.MEDIA_INFO_BUFFERING_END:
            case PlayStateParams.STATE_PAUSED:
//                hideAllView();
                mLlLoading.setVisibility(View.GONE);
                mIvThumb.setVisibility(View.GONE);
                break;
        }
    }

    private void _togglePlayStatus() {
        if (mVideoView.isPlaying()) {
            mIvPlay.setSelected(false);
            mVideoView.pause();
        } else {
            mIvPlay.setSelected(true);
            mVideoView.start();
            mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        }
    }

    private void _refreshHideRunnable() {
        mHandler.removeCallbacks(mHideBarRunnable);
        mHandler.postDelayed(mHideBarRunnable, DEFAULT_HIDE_TIMEOUT);
    }

    private int _setProgress() {
        if (mVideoView == null || mIsSeeking) {
            return 0;
        }
        int position = mVideoView.getCurrentPosition();
        int duration = mVideoView.getDuration();
        if (duration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / duration;
            mPlayerSeek.setProgress((int) pos);
        }
        int percent = mVideoView.getBufferPercentage();
        mPlayerSeek.setSecondaryProgress(percent * 10);

        mTvEndTime.setText(generateTime(duration));
        mTvCurTime.setText(generateTime(position));

        return position;
    }

    private void _setFastForward(String time) {
        if (mFlTouchLayout.getVisibility() == View.GONE) {
            mFlTouchLayout.setVisibility(View.VISIBLE);
        }
        if (mTvFastForward.getVisibility() == View.GONE) {
            mTvFastForward.setVisibility(View.VISIBLE);
            mTvFastRewind.setVisibility(View.GONE);
        }
        mTvFastForward.setText(time);
    }

    private void _setFastRewind(String time) {
        if (mFlTouchLayout.getVisibility() == View.GONE) {
            mFlTouchLayout.setVisibility(View.VISIBLE);
        }
        if (mTvFastRewind.getVisibility() == View.GONE) {
            mTvFastRewind.setVisibility(View.VISIBLE);
            mTvFastForward.setVisibility(View.GONE);
        }
        mTvFastRewind.setText(time);
    }

    private void _hideTouchView() {
        if (mFlTouchLayout.getVisibility() == View.VISIBLE) {
            mTvFastForward.setVisibility(View.GONE);
            mTvFastRewind.setVisibility(View.GONE);
            mTvVolume.setVisibility(View.GONE);
            mTvBrightness.setVisibility(View.GONE);
            mFlTouchLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 全屏切换
     */
    public void toggleFullScreen() {
        if (WindowUtils.getScreenOrientation(this) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            _handleActionBar(false);
            _changeHeight(false);
            mIvFullscreen.setSelected(false);
            mWindowTopBar.setVisibility(View.VISIBLE);
            mFullscreenTopBar.setVisibility(View.GONE);
            mIsFullscreen = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            _handleActionBar(true);
            _changeHeight(true);
            mIvFullscreen.setSelected(true);
            mWindowTopBar.setVisibility(View.GONE);
            mFullscreenTopBar.setVisibility(View.VISIBLE);
            mIsFullscreen = true;
        }
    }


    private void _handleActionBar(boolean isFullscreen) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (isFullscreen) {
                supportActionBar.hide();
            } else {
                supportActionBar.show();
            }
        }
    }

    private void _changeHeight(boolean isFullscreen) {
        ViewGroup.LayoutParams layoutParams = mFlVideoBox.getLayoutParams();
        if (isFullscreen) {
            layoutParams.height = mWidthPixels;
        } else {
            layoutParams.height = mInitHeight;
        }
        mFlVideoBox.setLayoutParams(layoutParams);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Build.VERSION.SDK_INT >= 19) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                View decorView = getWindow().getDecorView();
                mScreenUiVisibilily = decorView.getSystemUiVisibility();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(mScreenUiVisibilily);
            }
        }
    }

    /**
     * 快进或者快退滑动改变进度
     *
     * @param percent
     */
    private void onProgressSlide(float percent) {
        int position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration / 2);
        long delta = (long) (deltaMax * percent);
        mTargetPosition = delta + position;
        if (mTargetPosition > duration) {
            mTargetPosition = duration;
        } else if (mTargetPosition <= 0) {
            mTargetPosition = 0;
        }
        if (mTargetPosition > position) {
            _setFastForward(generateTime(mTargetPosition));
        } else {
            _setFastRewind(generateTime(mTargetPosition));
        }
    }

    private void _setVolumeSlide(int volume) {
        if (mFlTouchLayout.getVisibility() == View.GONE) {
            mFlTouchLayout.setVisibility(View.VISIBLE);
        }
        if (mTvVolume.getVisibility() == View.GONE) {
            mTvVolume.setVisibility(View.VISIBLE);
        }
        mTvVolume.setText((volume * 100 / mMaxVolume) + "%");
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mCurVolume == -1) {
            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurVolume < 0)
                mCurVolume = 0;
        }
        int index = (int) (percent * mMaxVolume) + mCurVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        _setVolumeSlide(index);
    }

    @OnClick({R.id.iv_back, R.id.iv_back_window, R.id.iv_play, R.id.iv_fullscreen})
    public void onClick(View view) {
        _refreshHideRunnable();
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.iv_back_window:
                break;
            case R.id.iv_play:
                _togglePlayStatus();
                break;
            case R.id.iv_fullscreen:
                toggleFullScreen();
                break;
        }
    }

    /**************************************
     * Runnable
     **********************************************/

    private Runnable mHideBarRunnable = new Runnable() {
        @Override
        public void run() {
            hideAllView();
            mIsShowBar = false;
        }
    };

    /**************************************
     * Listener
     **********************************************/

    private OnGestureListener mPlayerGestureListener = new SimpleOnGestureListener() {

        /**
         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
         */
        private boolean isDownTouch;
        /**
         * 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
         */
        private boolean isVolume;
        /**
         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
         */
        private boolean isLandscape;

        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    isVolume = mOldX > getResources().getDisplayMetrics().widthPixels * 0.5f;
                    isDownTouch = false;
                }

                if (isLandscape) {
                    onProgressSlide(-deltaX / mVideoView.getWidth());
//                    if (!isLive) {
//                        /**进度设置*/
//                        onProgressSlide(-deltaX / videoView.getWidth());
//                    }
                } else {
                    float percent = deltaY / mVideoView.getHeight();
                    if (isVolume) {
                        /**声音设置*/
                        onVolumeSlide(percent);
                    } else {
                        /**亮度设置*/
//                        onBrightnessSlide(percent);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.w("PlayerActivity", "onSingleTapConfirmed");
            if (!isForbidTouch) {
                toggleControlBar();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isForbidTouch) {
                _togglePlayStatus();
            }
            return true;
        }
    };

    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        private long curPosition;

        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mIsSeeking = true;
            _showControlBar(3600000);
            mHandler.removeMessages(MSG_UPDATE_SEEK);
            curPosition = mVideoView.getCurrentPosition();
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
            if (!fromUser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }
            long duration = mVideoView.getDuration();
            mTargetPosition = (duration * progress) / 1000L;
            if (mTargetPosition > curPosition) {
                _setFastForward(generateTime(mTargetPosition));
            } else {
                _setFastRewind(generateTime(mTargetPosition));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            _hideTouchView();
            mIsSeeking = false;
            mVideoView.seekTo((int) mTargetPosition);
            mTargetPosition = -1;
            _setProgress();
//            updatePausePlay();
            _showControlBar(DEFAULT_HIDE_TIMEOUT);
        }
    };
}
