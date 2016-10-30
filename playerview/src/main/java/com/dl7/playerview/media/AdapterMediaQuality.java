package com.dl7.playerview.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dl7.playerview.R;

import java.util.List;

/**
 * Created by Rukey7 on 2016/10/29.
 */

public class AdapterMediaQuality extends BaseListAdapter<MediaQualityInfo> {

    private OnItemSelectListener mSelectListener;

    public AdapterMediaQuality(Context context, List<MediaQualityInfo> datas) {
        super(context, datas);
    }

    public AdapterMediaQuality(Context context) {
        super(context);
    }

    @Override
    public View getView(final int i, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_media_quality, parent, false);
        }
        TextView qualityDesc = (TextView) view.findViewById(R.id.tv_media_quality);
        qualityDesc.setText(mDatas.get(i).getDesc());
        qualityDesc.setSelected(mDatas.get(i).isSelect());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDatas.get(i).isSelect()) {
                    _cleanSelected();
                    mDatas.get(i).setSelect(true);
                    if (mSelectListener != null) {
                        mSelectListener.onSelect(mDatas.get(i).getIndex());
                    }
                    notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    private void _cleanSelected() {
        for (MediaQualityInfo info : mDatas) {
            if (info.isSelect()) {
                info.setSelect(false);
            }
        }
    }

    public interface OnItemSelectListener {
        void onSelect(int index);
    }

    public void setSelectListener(OnItemSelectListener selectListener) {
        mSelectListener = selectListener;
    }
}
