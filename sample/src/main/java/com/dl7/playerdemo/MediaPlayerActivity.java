package com.dl7.playerdemo;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MediaPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        View.OnClickListener {

    /**
     * 用来显示Media数据
     */
    private SurfaceView surfaceView;
    /**
     * surfaceView播放控制
     */
    private SurfaceHolder surfaceHolder;
    /**
     * 播放控制条
     */
    private SeekBar seekBar;
    /**
     * 暂停播放按钮
     */
    private Button playButton;
    /**
     * 重新播放按钮
     */
    private Button replayButton;
    /**
     * 截图按钮
     */
    private Button screenShotButton;
    /**
     * 改变视频大小button
     */
    private Button videoSizeButton;
    /**
     * 加载进度显示条
     */
    private ProgressBar progressBar;
    /**
     * 播放视频
     */
    private MediaPlayer mediaPlayer;
    /**
     * 记录当前播放的位置
     */
    private int playPosition = -1;
    /**
     * seekBar是否自动拖动
     */
    private boolean seekBarAutoFlag = false;
    /**
     * 视频时间显示
     */
    private TextView vedioTiemTextView;
    /**
     * 播放总时间
     */
    private String videoTimeString;
    private long videoTimeLong;
    /**
     * 播放路径
     */
    private String pathString;
    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        initViews();
    }

    /**
     * 初始化视图
     */
    public void initViews() {
        String path = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 存在获取外部文件路径
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            // 不存在获取内部存储
            path = Environment.getDataDirectory().getPath();
        }
        pathString = path + "/MyData/my.mp4";
        // 初始化控件
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        playButton = (Button) findViewById(R.id.button_play);
        replayButton = (Button) findViewById(R.id.button_replay);
        vedioTiemTextView = (TextView) findViewById(R.id.textView_showTime);
        screenShotButton = (Button) findViewById(R.id.button_screenShot);
        videoSizeButton = (Button) findViewById(R.id.button_videoSize);
        // 设置surfaceHolder
        surfaceHolder = surfaceView.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置surface回调
        surfaceHolder.addCallback(new SurfaceCallback());

    }


    /**
     * 播放视频
     */
    public void playVideo() {
        // 初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        // 重置mediaPaly,建议在初始化mediaplay立即调用。
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
//        Uri uri = Uri.parse("http://183.61.13.14/vmind.qqvideo.tc.qq.com/x0200hkt1cg.p202.1.mp4");
        try {
             mediaPlayer.setDataSource(this, Uri.parse("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"));
            // mediaPlayer.setDataSource(SurfaceViewTestActivity.this, uri);
//            mediaPlayer.setDataSource(pathString);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载视频错误！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 判断播放位置
        if (Constants.playPosition >= 0) {

            if (null != mediaPlayer) {
                seekBarAutoFlag = true;
                mediaPlayer.seekTo(Constants.playPosition);
                mediaPlayer.start();
            } else {
                playVideo();
            }

        }
    }

    /**
     * 页面处于暂停状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                Constants.playPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                seekBarAutoFlag = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 发生屏幕旋转时调用
     */
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            // 保存播放位置
            Constants.playPosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * 屏幕旋转完成时调用
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 屏幕销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 由于MediaPlay非常占用资源，所以建议屏幕当前activity销毁时，则直接销毁
        try {
            if (null != mediaPlayer) {
                // 提前标志为false,防止在视频停止时，线程仍在运行。
                seekBarAutoFlag = false;
                // 如果正在播放，则停止。
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                Constants.playPosition = -1;
                // 释放mediaPlayer
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 转换播放时间
     *
     * @param milliseconds 传入毫秒值
     * @return 返回 hh:mm:ss或mm:ss格式的数据
     */
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 保存视频截图.该方法只能支持本地视频文件
     *
     * @param time 视频当前位置
     */
    public void savaScreenShot(long time) {
        // 标记是否保存成功
        boolean isSave = false;
        // 获取文件路径
        String path = null;
        // 文件名称
        String fileName = null;
        if (time >= 0) {
            try {
//                Uri uri = Uri.parse("http://183.61.13.14/vmind.qqvideo.tc.qq.com/x0200hkt1cg.p202.1.mp4");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(pathString);
                // 获取视频的播放总时长单位为毫秒
                String timeString = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                // 转换格式为微秒
                long timelong = Long.parseLong(timeString) * 1000;
                // 计算当前视频截取的位置,索引点以秒为单位
                long index = (time * timelong) / mediaPlayer.getDuration();
                // 获取当前视频指定位置的截图,时间参数的单位是微秒,做了*1000处理
                // 第二个参数为指定位置，意思接近的位置截图
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(time * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                // 释放资源
                mediaMetadataRetriever.release();
                // 判断外部设备SD卡是否存在
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 存在获取外部文件路径
                    path = Environment.getExternalStorageDirectory().getPath();
                } else {
                    // 不存在获取内部存储
                    path = Environment.getDataDirectory().getPath();
                }
                // 设置文件名称 ，以事件毫秒为名称
                fileName = Calendar.getInstance().getTimeInMillis() + ".jpg";
                // 设置保存文件
                File file = new File(path + "/MyData/" + fileName);

                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                isSave = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 保存成功以后，展示图片
            if (isSave) {
                Toast.makeText(this, "isSave", Toast.LENGTH_SHORT).show();
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setPadding(20, 20, 20, 20);
                imageView.setImageBitmap(BitmapFactory.decodeFile(path + "/MyData/" + fileName));
                new AlertDialog.Builder(this).setView(imageView).show();
            }
        }

    }

    /**
     * 改变视频的显示大小，全屏，窗口，内容
     */
    public void changeVideoSize() {
        // 改变视频大小
        String videoSizeString = videoSizeButton.getText().toString();
        // 获取视频的宽度和高度
        int width = mediaPlayer.getVideoWidth();
        int height = mediaPlayer.getVideoHeight();
        // 如果按钮文字为窗口则设置为窗口模式
        if ("窗口".equals(videoSizeString)) {
            // 如果为全屏模式则改为适应内容的，前提是视频宽高小于屏幕宽高，如果大于宽高 我们要做缩放
            // 如果视频的宽高度有一方不满足我们就要进行缩放. 如果视频的大小都满足就直接设置并居中显示。
            if (width > screenWidth || height > screenHeight) {
                // 计算出宽高的倍数
                float vWidth = (float) width / (float) screenWidth;
                float vHeight = (float) height / (float) screenHeight;
                // 获取最大的倍数值，按大数值进行缩放
                float max = Math.max(vWidth, vHeight);
                // 计算出缩放大小,取接近的正值
                width = (int) Math.ceil((float) width / max);
                height = (int) Math.ceil((float) height / max);
            }
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("全屏");
        } else if ("全屏".equals(videoSizeString)) {
            // 设置全屏
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth,
                    screenHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("窗口");
        }
    }

    /**
     * seekBar拖动监听类
     *
     * @author shenxiaolei
     */
    @SuppressWarnings("unused")
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // 设置当前播放时间
                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * 视频加载完毕监听
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // 当视频加载完毕以后，隐藏加载进度条
        progressBar.setVisibility(View.GONE);
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        if (Constants.playPosition >= 0) {
            mediaPlayer.seekTo(Constants.playPosition);
            Constants.playPosition = -1;
            // surfaceHolder.unlockCanvasAndPost(Constants.getCanvas());
        }
        seekBarAutoFlag = true;
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setMax(mediaPlayer.getDuration());
        // 设置播放时间
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);
        // 设置拖动监听事件
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        // 设置按钮监听事件
        // 重新播放
        replayButton.setOnClickListener(this);
        // 暂停和播放
        playButton.setOnClickListener(this);
        // 截图按钮
        screenShotButton.setOnClickListener(this);
        // 视频大小
        videoSizeButton.setOnClickListener(this);
        // 播放视频
        mediaPlayer.start();
        // 设置SurfaceView显示媒体数据
        mediaPlayer.setDisplay(surfaceHolder);
        // 开启线程 刷新进度条
        new Thread(runnable).start();
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

    }

    /**
     * 按钮点击事件监听
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // 重新播放
        if (v == replayButton) {
            // mediaPlayer不空，则直接跳转
            if (null != mediaPlayer) {
                // MediaPlayer和进度条都跳转到开始位置
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                // 如果不处于播放状态，则开始播放
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else {
                // 为空则重新设置mediaPlayer
                playVideo();
            }

        }
        // 播放、暂停按钮
        if (v == playButton) {
            if (null != mediaPlayer) {
                // 正在播放
                if (mediaPlayer.isPlaying()) {
                    Constants.playPosition = mediaPlayer.getCurrentPosition();
                    // seekBarAutoFlag = false;
                    mediaPlayer.pause();
                    playButton.setText("播放");
                } else {
                    if (Constants.playPosition >= 0) {
                        // seekBarAutoFlag = true;
                        mediaPlayer.seekTo(Constants.playPosition);
                        mediaPlayer.start();
                        playButton.setText("暂停");
                        Constants.playPosition = -1;
                    }
                }

            }
        }
        // 视频截图
        if (v == screenShotButton) {
            if (null != mediaPlayer) {
                // 视频正在播放，
                if (mediaPlayer.isPlaying()) {
                    // 获取播放位置
                    Constants.playPosition = mediaPlayer.getCurrentPosition();
                    // 暂停播放
                    mediaPlayer.pause();
                    //
                    playButton.setText("播放");
                }
                // 视频截图
                savaScreenShot(Constants.playPosition);
            } else {
                Toast.makeText(this, "视频暂未播放！", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == videoSizeButton) {
            // 调用改变大小的方法
            changeVideoSize();
        }
    }

    /**
     * 播放完毕监听
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 设置seeKbar跳转到最后位置
        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        // 设置播放标记为false
        seekBarAutoFlag = false;
    }

    /**
     * 视频缓存大小监听,当视频播放以后 在started状态会调用
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO Auto-generated method stub
        // percent 表示缓存加载进度，0为没开始，100表示加载完成，在加载完成以后也会一直调用该方法
        Log.e("text", "onBufferingUpdate-->" + percent);
        // 可以根据大小的变化来
    }

    /**
     * 错误监听
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    /**
     * 滑动条变化线程
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                while (seekBarAutoFlag) {
                    if (null != mediaPlayer
                            && mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * SurfaceView的callBack
     * @author LONG
     *
     */
    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // Surface大小发生变化
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // surfaceView被创建,设置播放资源
            playVideo();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // surfaceView销毁,同时销毁mediaPlayer
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }
}
