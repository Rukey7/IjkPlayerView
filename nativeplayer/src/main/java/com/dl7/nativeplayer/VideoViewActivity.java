package com.dl7.nativeplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoViewActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, View.OnTouchListener{

    private static final String VIDEO_FILE = "/MyData/my.mp4";
    private static final int FULL_SCREEN = 1;
    private static final int WINDOW_SCREEN = 2;
    private static final String VIDEO_POSITION = "videoPosition";
    /**
     * View播放
     */
    private VideoView videoView;

    /**
     * 加载预览进度条
     */
    private ProgressBar progressBar;

    /**
     * 设置view播放控制条
     */
    private MediaController mediaController;

    /**
     * 标记当视频暂停时播放位置
     */
    private int intPositionWhenPause = -1;

    /**
     * 设置窗口模式下的videoview的宽度
     */
    private int videoWidth;

    /**
     * 设置窗口模式下videoview的高度
     */
    private int videoHeight;

    private boolean mIsFullScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_view);
        if(savedInstanceState != null) {
            // 获取播放索引
            intPositionWhenPause = savedInstanceState.getInt(VIDEO_POSITION);
        }

        initVideoView();
    }

    /**
     * 初始化videoview播放
     */
    public void initVideoView() {
        // 初始化进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // 初始化VideoView
        videoView = (VideoView) findViewById(R.id.videoView);
        // 初始化videoview控制器
        mediaController = new MediaController(this);
        // 设置videoview的控制器
        videoView.setMediaController(mediaController);
        // 设置显示控制条
        mediaController.show(0);
        // 设置播放完成以后监听
        videoView.setOnCompletionListener(this);
        // 设置发生错误监听，如果不设置videoview会向用户提示发生错误
        videoView.setOnErrorListener(this);
        // 设置在视频文件在加载完毕以后的回调函数
        videoView.setOnPreparedListener(this);
        // 设置videoView的点击监听
        videoView.setOnTouchListener(this);
        // 设置网络视频路径
        // http://123.150.52.227/0725695b00000000-1415769042-1960016430/data5/vkplx.video.qq.com/flv/74/164/a0015193bxf.p203.1.mp4
        // http://120.41.1.158/vkplx.video.qq.com/flv/74/164/a0015193bxf.p203.1.mp4
        // http://vkplx.video.qq.com/flv/74/164/a0015193bxf.p203.1.mp4
        // http://221.204.220.17//data5/cdn_transfer/36/4C/364f19b5662e054057fc9851703a03f3b0f4ea4c.flv
        // http://106.38.249.144/youku/67739CD2F943E7AB77EFF6511/03000810005429C7E703C905CF07DD613756C8-634B-EB1E-9628-F2F38A38C261.mp4
        String sdDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        String videoPath = sdDir + VIDEO_FILE;
//        videoView.setVideoPath(videoPath);
        videoView.setVideoURI(Uri.parse("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"));
        // 设置为全屏模式播放
        setVideoViewLayoutParams(FULL_SCREEN);
        findViewById(R.id.btn_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFullScreen = !mIsFullScreen;
                if (mIsFullScreen) {
                    setVideoViewLayoutParams(FULL_SCREEN);
                } else {
                    setVideoViewLayoutParams(WINDOW_SCREEN);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 启动视频播放
        videoView.start();
        // 设置获取焦点
        videoView.setFocusable(true);

    }

    /**
     * 设置videiview的全屏和窗口模式
     *
     * @param paramsType
     *            标识 1为全屏模式 2为窗口模式
     */
    public void setVideoViewLayoutParams(int paramsType) {

        if (FULL_SCREEN == paramsType) {
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            videoView.setLayoutParams(LayoutParams);
        } else {
            // 动态获取屏幕宽高
            DisplayMetrics DisplayMetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay()
                    .getMetrics(DisplayMetrics);
            videoHeight = DisplayMetrics.heightPixels - 50;
            videoWidth = DisplayMetrics.widthPixels - 50;
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(
                    videoWidth, videoHeight);
            LayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.setLayoutParams(LayoutParams);
        }
    }

    /**
     * 视频播放完成以后调用的回调函数
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(this, "视频播放结束!", Toast.LENGTH_SHORT).show();
    }

    /**
     * 视频播放发生错误时调用的回调函数
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e("text", "发生未知错误");

                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e("text", "媒体服务器死机");
                break;
            default:
                Log.e("text", "onError+" + what);
                break;
        }
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                // io读写错误
                Log.e("text", "文件或网络相关的IO操作错误");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                // 文件格式不支持
                Log.e("text", "比特流编码标准或文件不符合相关规范");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                // 一些操作需要太长时间来完成,通常超过3 - 5秒。
                Log.e("text", "操作超时");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                // 比特流编码标准或文件符合相关规范,但媒体框架不支持该功能
                Log.e("text", "比特流编码标准或文件符合相关规范,但媒体框架不支持该功能");
                break;
            default:
                Log.e("text", "onError+" + extra);
                break;
        }
        // 如果未指定回调函数，或回调函数返回假，VideoView 会通知用户发生了错误。
        return false;
    }

    /**
     * 视频文件加载文成后调用的回调函数
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // 如果文件加载成功,隐藏加载进度条
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 对videoView的触摸监听
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * 页面暂停效果处理
     */
    @Override
    protected void onPause() {
        super.onPause();
        // 如果当前页面暂定则保存当前播放位置，并暂停
        intPositionWhenPause = videoView.getCurrentPosition();
        // 停止回放视频文件
        videoView.stopPlayback();
    }

    /**
     * 页面从暂停中恢复
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 跳转到暂停时保存的位置
        if (intPositionWhenPause >= 0) {
            videoView.seekTo(intPositionWhenPause);
            // 初始播放位置
            intPositionWhenPause = -1;
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy:" + intPositionWhenPause);

        super.onDestroy();
        if (null != videoView) {
            videoView = null;
        }
    }

    /**
     * 屏幕翻转会调用onDestroy()销毁活动，需要保存播放索引参数
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt(VIDEO_POSITION, intPositionWhenPause);
    }
}
