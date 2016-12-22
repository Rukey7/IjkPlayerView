package com.dl7.playerdemo.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.dl7.playerdemo.danmaku.DanmakuData;

import java.util.ArrayList;
import java.util.List;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

/**
 * Created by long on 2016/12/22.
 */

public class DataHelper {

    public static void afunData2CustomData(Context context) {
        List<AfunData> datas = GsonHelper.convertEntities(AssetsHelper.readData(context, "afun.json"), AfunData.class);
        List<DanmakuData> danmakuDatas = new ArrayList<>();
        for (AfunData data : datas) {
            DanmakuData info = new DanmakuData();
            String[] values = data.getC().split(",");
            if (values.length > 0) {
                int type = Integer.parseInt(values[2]); // 弹幕类型
                if (type == 7)
                    continue;
                long time = (long) (Float.parseFloat(values[0]) * 1000); // 出现时间
                int color = Integer.parseInt(values[1]) | 0xFF000000; // 颜色
                float textSize = Float.parseFloat(values[3]); // 字体大小
                info.type = type;
                info.time = time;
                info.textSize = 100;
                info.textColor = color;
                info.content = data.getM();
                danmakuDatas.add(info);
            }
        }
        Log.w("DataHelper", danmakuDatas.toString());
        Log.e("DataHelper", GsonHelper.object2JsonStr(danmakuDatas));
    }
}
