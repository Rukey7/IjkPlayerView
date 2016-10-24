package com.dl7.ijkplayersample.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dl7.ijkplayersample.R;

import butterknife.BindView;

/**
 * Created by long on 2016/10/24.
 */
public class PlayerView extends FrameLayout {
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
    private Context mContext;


    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _initView(context);
    }

    private void _initView(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_player_view, this);

    }


}
