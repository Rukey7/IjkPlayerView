package com.dl7.playerview.media;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dl7.playerview.R;
import com.dl7.playerview.utils.WindowUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.GestureDetector.OnGestureListener;
import static android.view.GestureDetector.SimpleOnGestureListener;
import static android.widget.SeekBar.OnSeekBarChangeListener;
import static com.dl7.playerview.utils.StringUtils.generateTime;
import static tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;

/**
 * Created by long on 2016/10/24.
 */
public class PlayerView extends FrameLayout implements View.OnClickListener {

    // 进度条最大值
    private static final int MAX_VIDEO_SEEK = 1000;
    // 默认隐藏控制栏时间
    private static final int DEFAULT_HIDE_TIMEOUT = 3000;
    // 更新进度消息
    private static final int MSG_UPDATE_SEEK = 10086;

    // 原生的IjkPlayer
    private IjkVideoView mVideoView;
    // 视频开始前的缩略图，根据需要外部进行加载
    public ImageView mPlayerThumb;
    // 加载
    private ProgressBar mLoadingView;
    // 音量
    private TextView mTvVolume;
    // 亮度
    private TextView mTvBrightness;
    // 快进
    private TextView mTvFastForward;
    // 触摸信息布局
    private FrameLayout mFlTouchLayout;
    // 全屏下的后退键
    private ImageView mIvBack;
    // 全屏下的标题
    private TextView mTvTitle;
    // 全屏下的TopBar
    private LinearLayout mFullscreenTopBar;
    // 窗口模式的后退键
    private ImageView mIvBackWindow;
    // 窗口模式的TopBar
    private FrameLayout mWindowTopBar;
    // 播放键
    private ImageView mIvPlay;
    // 当前时间
    private TextView mTvCurTime;
    // 进度条
    private SeekBar mPlayerSeek;
    // 结束时间
    private TextView mTvEndTime;
    // 全屏切换按钮
    private ImageView mIvFullscreen;
    // BottomBar
    private LinearLayout mLlBottomBar;
    // 整个视频框架布局
    private FrameLayout mFlVideoBox;
    // 锁屏键
    private ImageView mIvPlayerLock;
    // 关联的Activity
    private AppCompatActivity mAttachActivity;

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
    // 音量控制
    private AudioManager mAudioManager;
    // 手势控制
    private GestureDetector mGestureDetector;
    // 最大音量
    private int mMaxVolume;
    // 锁屏
    private boolean mIsForbidTouch = false;
    // 是否显示控制栏
    private boolean mIsShowBar = true;
    // 是否全屏
    private boolean mIsFullscreen;
    // 是否正在拖拽进度条
    private boolean mIsSeeking;
    // 目标进度
    private long mTargetPosition = -1;
    // 当前进度
    private int mCurPosition = -1;
    // 当前音量
    private int mCurVolume = -1;
    // 当前亮度
    private float mCurBrightness = -1;
    // 初始高度
    private int mInitHeight;
    // 屏幕宽度
    private int mWidthPixels;
    // 屏幕UI可见性
    private int mScreenUiVisibility;
    // 屏幕旋转角度监听
    private OrientationEventListener mOrientationListener;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _initView(context);
    }

    private void _initView(Context context) {
        if (context instanceof AppCompatActivity) {
            mAttachActivity = (AppCompatActivity) context;
        } else {
            throw new IllegalArgumentException("Context must be AppCompatActivity");
        }
        View.inflate(context, R.layout.layout_player_view, this);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mPlayerThumb = (ImageView) findViewById(R.id.iv_thumb);
        mLoadingView = (ProgressBar) findViewById(R.id.pb_loading);
        mTvVolume = (TextView) findViewById(R.id.tv_volume);
        mTvBrightness = (TextView) findViewById(R.id.tv_brightness);
        mTvFastForward = (TextView) findViewById(R.id.tv_fast_forward);
        mFlTouchLayout = (FrameLayout) findViewById(R.id.fl_touch_layout);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mFullscreenTopBar = (LinearLayout) findViewById(R.id.fullscreen_top_bar);
        mIvBackWindow = (ImageView) findViewById(R.id.iv_back_window);
        mWindowTopBar = (FrameLayout) findViewById(R.id.window_top_bar);
        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mTvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        mPlayerSeek = (SeekBar) findViewById(R.id.player_seek);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        mIvFullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
        mLlBottomBar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
        mFlVideoBox = (FrameLayout) findViewById(R.id.fl_video_box);
        mIvPlayerLock = (ImageView) findViewById(R.id.iv_player_lock);

        mIvPlay.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mIvFullscreen.setOnClickListener(this);
        mIvBackWindow.setOnClickListener(this);
        mIvPlayerLock.setOnClickListener(this);
    }

    /**
     * 初始化
     */
    private void _initMediaPlayer() {
        // 加载 IjkMediaPlayer 库
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        // 声音
        mAudioManager = (AudioManager) mAttachActivity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 亮度
        try {
            int e = Settings.System.getInt(mAttachActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float progress = 1.0F * (float) e / 255.0F;
            WindowManager.LayoutParams layout = mAttachActivity.getWindow().getAttributes();
            layout.screenBrightness = progress;
            mAttachActivity.getWindow().setAttributes(layout);
        } catch (Settings.SettingNotFoundException var7) {
            var7.printStackTrace();
        }
        // 进度
        mPlayerSeek.setMax(MAX_VIDEO_SEEK);
        mPlayerSeek.setOnSeekBarChangeListener(mSeekListener);
        // 视频监听
        mVideoView.setOnInfoListener(mInfoListener);
        // 触摸控制
        mGestureDetector = new GestureDetector(mAttachActivity, mPlayerGestureListener);
        mFlVideoBox.setClickable(true);
        mFlVideoBox.setOnTouchListener(mPlayerTouchListener);
        // 屏幕翻转控制
        mOrientationListener = new OrientationEventListener(mAttachActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                _handleOrientation(orientation);
            }
        };
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mInitHeight == 0) {
            mInitHeight = getHeight();
            mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        }
    }

    /**============================ 外部调用接口 ============================*/

    /**
     * Activity.onResume() 里调用
     */
    public void onResume() {
        mVideoView.resume();
        if (!mIsForbidTouch) {
            mOrientationListener.enable();
        }
        if (mCurPosition != -1) {
            // 重进后 seekTo 到指定位置播放时，通常会回退到前几秒，关键帧??
            mVideoView.seekTo(mCurPosition);
            mCurPosition = -1;
        }
    }

    /**
     * Activity.onPause() 里调用
     */
    public void onPause() {
        mCurPosition = mVideoView.getCurrentPosition();
        mVideoView.pause();
        mIvPlay.setSelected(false);
        mOrientationListener.disable();
    }

    /**
     * Activity.onDestroy() 里调用
     */
    public void onDestroy() {
        mVideoView.destroy();
        IjkMediaPlayer.native_profileEnd();
    }

    /**
     * 处理音量键，避免外部按音量键后导航栏和状态栏显示出来退不回去的状态
     *
     * @param keyCode
     * @return
     */
    public boolean handleVolumeKey(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        } else {
            return false;
        }
    }

    public boolean onBackPressed() {
        if (mIsFullscreen) {
            mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * 初始化，必须要先调用
     *
     * @return
     */
    public PlayerView init() {
        _initMediaPlayer();
        return this;
    }

    /**
     * 设置播放资源
     *
     * @param url
     * @return
     */
    public PlayerView setVideoPath(String url) {
        mVideoView.setVideoPath(url);
        mVideoView.seekTo(0);
        return this;
    }

    /**
     * 设置播放资源
     *
     * @param uri
     * @return
     */
    public PlayerView setVideoPath(Uri uri) {
        mVideoView.setVideoURI(uri);
        mVideoView.seekTo(0);
        return this;
    }

    /**
     * 设置标题，全屏的时候可见
     *
     * @param title
     */
    public PlayerView setTitle(String title) {
        mTvTitle.setText(title);
        return this;
    }

    /**
     * 开始播放
     *
     * @return
     */
    public void start() {
        if (!mVideoView.isPlaying()) {
            mIvPlay.setSelected(true);
            mVideoView.start();
            // 更新进度
            mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mVideoView.isPlaying()) {
            mIvPlay.setSelected(false);
            mVideoView.pause();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        mVideoView.stopPlayback();
    }

    public void reset() {
    }


    /**============================ 控制栏处理 ============================*/

    /**
     * SeekBar监听
     */
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
            // 计算目标位置
            mTargetPosition = (duration * progress) / MAX_VIDEO_SEEK;
            int deltaTime = (int) ((mTargetPosition - curPosition) / 1000);
            String desc;
            // 对比当前位置来显示快进或后退
            if (mTargetPosition > curPosition) {
                desc = generateTime(mTargetPosition) + "/" + generateTime(duration) + "\n" + "+" + deltaTime + "秒";
            } else {
                desc = generateTime(mTargetPosition) + "/" + generateTime(duration) + "\n" + deltaTime + "秒";
            }
            _setFastForward(desc);
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            _hideTouchView();
            mIsSeeking = false;
            // 视频跳转
            mVideoView.seekTo((int) mTargetPosition);
            mTargetPosition = -1;
            _setProgress();
            _showControlBar(DEFAULT_HIDE_TIMEOUT);
        }
    };

    /**
     * 隐藏视图Runnable
     */
    private Runnable mHideBarRunnable = new Runnable() {
        @Override
        public void run() {
            _hideAllView(false);
            mIsShowBar = false;
        }
    };

    /**
     * 隐藏除视频外所有视图
     */
    private void _hideAllView(boolean isTouchLock) {
        mPlayerThumb.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mFlTouchLayout.setVisibility(View.GONE);
        mFullscreenTopBar.setVisibility(View.GONE);
        mWindowTopBar.setVisibility(View.GONE);
        mLlBottomBar.setVisibility(View.GONE);
        if (!isTouchLock) {
            mIvPlayerLock.setVisibility(View.GONE);
        }
    }

    /**
     * 设置控制栏显示或隐藏
     *
     * @param isShowBar
     */
    private void _setControlBarVisible(boolean isShowBar) {
        if (mIsForbidTouch) {
            mIvPlayerLock.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
        } else {
            mLlBottomBar.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
            // 全屏切换显示的控制栏不一样
            if (mIsFullscreen) {
                mFullscreenTopBar.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
                mWindowTopBar.setVisibility(View.GONE);
                mIvPlayerLock.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
            } else {
                mWindowTopBar.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
                mFullscreenTopBar.setVisibility(View.GONE);
                mIvPlayerLock.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 开关控制栏，单击界面的时候
     */
    private void _toggleControlBar() {
        mIsShowBar = !mIsShowBar;
        _setControlBarVisible(mIsShowBar);
        if (mIsShowBar) {
            // 发送延迟隐藏控制栏的操作
            mHandler.postDelayed(mHideBarRunnable, DEFAULT_HIDE_TIMEOUT);
            // 发送更新 Seek 消息
            mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        }
    }

    /**
     * 显示控制栏
     *
     * @param timeout 延迟隐藏时间
     */
    private void _showControlBar(int timeout) {
        if (!mIsShowBar) {
            _setProgress();
            mIvPlay.requestFocus();
            mIsShowBar = true;
        }
        _setControlBarVisible(true);
        mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        // 先移除隐藏控制栏 Runnable，如果 timeout=0 则不做延迟隐藏操作
        mHandler.removeCallbacks(mHideBarRunnable);
        if (timeout != 0) {
            mHandler.postDelayed(mHideBarRunnable, timeout);
        }
    }

    /**
     * 切换播放状态，点击播放按钮时
     */
    private void _togglePlayStatus() {
        if (mVideoView.isPlaying()) {
            mIvPlay.setSelected(false);
            mVideoView.pause();
        } else {
            mIvPlay.setSelected(true);
            mVideoView.start();
            // 更新进度
            mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        }
    }

    /**
     * 刷新隐藏控制栏的操作
     */
    private void _refreshHideRunnable() {
        mHandler.removeCallbacks(mHideBarRunnable);
        mHandler.postDelayed(mHideBarRunnable, DEFAULT_HIDE_TIMEOUT);
    }

    /**
     * 切换控制锁
     */
    private void _togglePlayerLock() {
        mIsForbidTouch = !mIsForbidTouch;
        mIvPlayerLock.setSelected(mIsForbidTouch);
        if (mIsForbidTouch) {
            mOrientationListener.disable();
            _hideAllView(true);
        } else {
            mOrientationListener.enable();
            mFullscreenTopBar.setVisibility(View.VISIBLE);
            mLlBottomBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        _refreshHideRunnable();
        int i = v.getId();
        if (i == R.id.iv_back) {
            mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (i == R.id.iv_back_window) {
            mAttachActivity.finish();
        } else if (i == R.id.iv_play) {
            _togglePlayStatus();
        } else if (i == R.id.iv_fullscreen) {
            _toggleFullScreen();
        } else if (i == R.id.iv_player_lock) {
            _togglePlayerLock();
        }
    }

    /**==================== 屏幕翻转/切换处理 ====================*/

    /**
     * 全屏切换，点击全屏按钮
     */
    private void _toggleFullScreen() {
        if (WindowUtils.getScreenOrientation(mAttachActivity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * 设置全屏或窗口模式
     *
     * @param isFullscreen
     */
    private void _setFullScreen(boolean isFullscreen) {
        mIsFullscreen = isFullscreen;
        if (isFullscreen) {
            _handleActionBar(true);
            _changeHeight(true);
            // 改变图标
            mIvFullscreen.setSelected(true);
            mWindowTopBar.setVisibility(View.GONE);
            if (mIsShowBar) {
                mFullscreenTopBar.setVisibility(View.VISIBLE);
                mIvPlayerLock.setVisibility(VISIBLE);
            }
        } else {
            _handleActionBar(false);
            _changeHeight(false);
            mIvFullscreen.setSelected(false);
            if (mIsShowBar) {
                mWindowTopBar.setVisibility(View.VISIBLE);
            }
            mFullscreenTopBar.setVisibility(View.GONE);
            mIvPlayerLock.setVisibility(GONE);
        }
    }

    /**
     * 处理屏幕翻转
     *
     * @param orientation
     */
    private void _handleOrientation(int orientation) {
        Log.i("TTAG", "" + orientation);
        if (mIsFullscreen) {
            // 在全屏状态下，角度偏离竖直方向左右30°内进行竖屏切换
            if (orientation >= 0 && orientation <= 30 || orientation >= 330) {
                // 请求屏幕翻转
                mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            if (orientation >= 60 && orientation <= 120) {
                mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            } else if (orientation >= 240 && orientation <= 300) {
                mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    /**
     * 隐藏/显示 ActionBar
     *
     * @param isFullscreen
     */
    private void _handleActionBar(boolean isFullscreen) {
        ActionBar supportActionBar = mAttachActivity.getSupportActionBar();
        if (supportActionBar != null) {
            if (isFullscreen) {
                supportActionBar.hide();
            } else {
                supportActionBar.show();
            }
        }
    }

    /**
     * 改变视频布局高度
     *
     * @param isFullscreen
     */
    private void _changeHeight(boolean isFullscreen) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (isFullscreen) {
            // 高度扩展为横向全屏
            layoutParams.height = mWidthPixels;
        } else {
            // 还原高度
            layoutParams.height = mInitHeight;
        }
        setLayoutParams(layoutParams);
    }

    /**
     * 屏幕翻转后的处理，在 Activity.configurationChanged() 调用
     * SYSTEM_UI_FLAG_LAYOUT_STABLE：维持一个稳定的布局
     * SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
     * SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)
     * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     * SYSTEM_UI_FLAG_IMMERSIVE：沉浸式，从顶部下滑出现状态栏和导航栏会固定住
     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY：黏性沉浸式，从顶部下滑出现状态栏和导航栏过几秒后会缩回去
     *
     * @param newConfig
     */
    public void configurationChanged(Configuration newConfig) {
        // 沉浸式只能在SDK19以上实现
        if (Build.VERSION.SDK_INT >= 19) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // 获取关联 Activity 的 DecorView
                View decorView = mAttachActivity.getWindow().getDecorView();
                // 保存旧的配置
                mScreenUiVisibility = decorView.getSystemUiVisibility();
                // 沉浸式使用这些Flag
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                _setFullScreen(true);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                View decorView = mAttachActivity.getWindow().getDecorView();
                // 还原
                decorView.setSystemUiVisibility(mScreenUiVisibility);
                _setFullScreen(false);
            }
        }
    }

    /**============================ 触屏操作处理 ============================*/

    /**
     * 手势监听
     */
    private OnGestureListener mPlayerGestureListener = new SimpleOnGestureListener() {
        // 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
        private boolean isDownTouch;
        // 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
        private boolean isVolume;
        // 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
        private boolean isLandscape;

        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mIsForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    // 判断左右或上下滑动
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    // 判断是声音或亮度控制
                    isVolume = mOldX > getResources().getDisplayMetrics().widthPixels * 0.5f;
                    isDownTouch = false;
                }

                if (isLandscape) {
                    _onProgressSlide(-deltaX / mVideoView.getWidth());
//                    if (!isLive) {
//                        /**进度设置*/
//                        _onProgressSlide(-deltaX / videoView.getWidth());
//                    }
                } else {
                    float percent = deltaY / mVideoView.getHeight();
                    if (isVolume) {
                        _onVolumeSlide(percent);
                    } else {
                        _onBrightnessSlide(percent);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            _toggleControlBar();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!mIsForbidTouch) {
                _togglePlayStatus();
            }
            return true;
        }
    };

    /**
     * 触摸监听
     */
    private OnTouchListener mPlayerTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mHandler.removeCallbacks(mHideBarRunnable);
            }
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            }
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                _endGesture();
            }
            return false;
        }
    };

    /**
     * 更新进度条
     *
     * @return
     */
    private int _setProgress() {
        if (mVideoView == null || mIsSeeking) {
            return 0;
        }
        // 视频播放的当前进度
        int position = mVideoView.getCurrentPosition();
        // 视频总的时长
        int duration = mVideoView.getDuration();
        if (duration > 0) {
            // use long to avoid overflow，转换为 Seek 显示的进度值
            long pos = (long) MAX_VIDEO_SEEK * position / duration;
            mPlayerSeek.setProgress((int) pos);
        }
        // 获取缓冲的进度百分比，并显示在 Seek 的次进度
        int percent = mVideoView.getBufferPercentage();
        mPlayerSeek.setSecondaryProgress(percent * 10);
        // 更新播放时间
        mTvEndTime.setText(generateTime(duration));
        mTvCurTime.setText(generateTime(position));
        // 返回当前播放进度
        return position;
    }

    /**
     * 设置快进
     *
     * @param time
     */
    private void _setFastForward(String time) {
        if (mFlTouchLayout.getVisibility() == View.GONE) {
            mFlTouchLayout.setVisibility(View.VISIBLE);
        }
        if (mTvFastForward.getVisibility() == View.GONE) {
            mTvFastForward.setVisibility(View.VISIBLE);
        }
        mTvFastForward.setText(time);
    }

    /**
     * 隐藏触摸视图
     */
    private void _hideTouchView() {
        if (mFlTouchLayout.getVisibility() == View.VISIBLE) {
            mTvFastForward.setVisibility(View.GONE);
            mTvVolume.setVisibility(View.GONE);
            mTvBrightness.setVisibility(View.GONE);
            mFlTouchLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 快进或者快退滑动改变进度，这里处理触摸滑动不是拉动 SeekBar
     *
     * @param percent 拖拽百分比
     */
    private void _onProgressSlide(float percent) {
        int position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        // 单次拖拽最大时间差为100秒或播放时长的1/2
        long deltaMax = Math.min(100 * 1000, duration / 2);
        // 计算滑动时间
        long delta = (long) (deltaMax * percent);
        // 目标位置
        mTargetPosition = delta + position;
        if (mTargetPosition > duration) {
            mTargetPosition = duration;
        } else if (mTargetPosition <= 0) {
            mTargetPosition = 0;
        }
        int deltaTime = (int) ((mTargetPosition - position) / 1000);
        String desc;
        // 对比当前位置来显示快进或后退
        if (mTargetPosition > position) {
            desc = generateTime(mTargetPosition) + "/" + generateTime(duration) + "\n" + "+" + deltaTime + "秒";
        } else {
            desc = generateTime(mTargetPosition) + "/" + generateTime(duration) + "\n" + deltaTime + "秒";
        }
        _setFastForward(desc);
    }

    /**
     * 设置声音控制显示
     *
     * @param volume
     */
    private void _setVolumeInfo(int volume) {
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
    private void _onVolumeSlide(float percent) {
        if (mCurVolume == -1) {
            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurVolume < 0) {
                mCurVolume = 0;
            }
        }
        int index = (int) (percent * mMaxVolume) + mCurVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        _setVolumeInfo(index);
    }


    /**
     * 设置亮度控制显示
     *
     * @param brightness
     */
    private void _setBrightnessInfo(float brightness) {
        if (mFlTouchLayout.getVisibility() == View.GONE) {
            mFlTouchLayout.setVisibility(View.VISIBLE);
        }
        if (mTvBrightness.getVisibility() == View.GONE) {
            mTvBrightness.setVisibility(View.VISIBLE);
        }
        mTvBrightness.setText(Math.ceil(brightness * 100) + "%");
    }

    /**
     * 滑动改变亮度大小
     *
     * @param percent
     */
    private void _onBrightnessSlide(float percent) {
        if (mCurBrightness < 0) {
            mCurBrightness = mAttachActivity.getWindow().getAttributes().screenBrightness;
            if (mCurBrightness < 0.0f) {
                mCurBrightness = 0.5f;
            } else if (mCurBrightness < 0.01f) {
                mCurBrightness = 0.01f;
            }
        }
        WindowManager.LayoutParams attributes = mAttachActivity.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + percent;
        if (attributes.screenBrightness > 1.0f) {
            attributes.screenBrightness = 1.0f;
        } else if (attributes.screenBrightness < 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        _setBrightnessInfo(attributes.screenBrightness);
        mAttachActivity.getWindow().setAttributes(attributes);
    }

    /**
     * 手势结束调用
     */
    private void _endGesture() {
        if (mTargetPosition >= 0 && mTargetPosition != mVideoView.getCurrentPosition()) {
            // 更新视频播放进度
            mVideoView.seekTo((int) mTargetPosition);
            mPlayerSeek.setProgress((int) (mTargetPosition * MAX_VIDEO_SEEK / mVideoView.getDuration()));
            mTargetPosition = -1;
        }
        // 隐藏触摸操作显示图像
        _hideTouchView();
        mCurVolume = -1;
        mCurBrightness = -1;
    }

    /**
     * ============================ 播放状态控制 ============================
     */

    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int status, int extra) {
            switchStatus(status);
            return true;
        }
    };

    /**
     * 视频播放状态处理
     *
     * @param status
     */
    private void switchStatus(int status) {
        Log.e("TTAG", "status " + status);
        switch (status) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                mLoadingView.setVisibility(View.VISIBLE);
                break;

            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                mLoadingView.setVisibility(View.GONE);
                mPlayerThumb.setVisibility(View.GONE);
                // 更新进度
                mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
                break;
        }
    }

    /**============================ Runnable ============================*/

}
