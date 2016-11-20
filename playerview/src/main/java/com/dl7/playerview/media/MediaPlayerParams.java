package com.dl7.playerview.media;

public class MediaPlayerParams {

    /**================================= 视频裁剪比例 开始 =================================*/
    /**
     * 可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     */
    public static final int fitparent = 0;
    /**
     * 可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     */
    public static final int fillparent = 1;
    /**
     * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     */
    public static final int wrapcontent = 2;
    /**
     * 不剪裁,非等比例拉伸画面填满整个View
     */
    public static final int fitxy = 3;
    /**
     * 不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     */
    public static final int f16_9 = 4;
    /**
     * 不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     */
    public static final int f4_3 = 5;

    /**================================= 播放状态 =================================*/
    // 空闲
    public static final int STATE_IDLE = 330;
    // 错误
    public static final int STATE_ERROR = 331;
    // 加载中
    public static final int STATE_PREPARING = 332;
    // 加载完成
    public static final int STATE_PREPARED = 333;
    // 播放中
    public static final int STATE_PLAYING = 334;
    // 暂停
    public static final int STATE_PAUSED = 335;
    // 结束
    public static final int STATE_COMPLETED = 336;

    /**
     * ============================ 弹幕状态 ============================
     */
}
