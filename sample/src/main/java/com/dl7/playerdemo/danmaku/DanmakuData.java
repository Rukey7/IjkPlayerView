package com.dl7.playerdemo.danmaku;

import com.dl7.player.danmaku.BaseDanmakuData;

/**
 * Created by long on 2016/12/22.
 */
public class DanmakuData extends BaseDanmakuData {

    @Override
    public String toString() {
        return "DanmakuData{" +
                "content='" + content + '\'' +
                ", time=" + time +
                ", type=" + type +
                ", textSize=" + textSize +
                ", textColor=" + textColor +
                '}';
    }
}
