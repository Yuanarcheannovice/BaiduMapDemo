package com.xz.map.function.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.archeanx.lib.adapter.XRvPureDataAdapter;
import com.archeanx.lib.adapter.xutil.XRvViewHolder;
import com.baidu.mapapi.search.core.PoiInfo;
import com.xz.map.R;
import java.util.List;



/**
 * Created by xz on 2017/8/9 0009.
 * 地图 地址列表 适配器
 * @author xz
 */

public class MapAdapter extends XRvPureDataAdapter<PoiInfo> {
    private int mIndexTag=0;
    private PoiInfo mUserPoiInfo;

    public int getmIndexTag() {
        return mIndexTag;
    }

    public void setmUserPoiInfo(PoiInfo userPoiInfo) {
        this.mUserPoiInfo = userPoiInfo;
    }

    public void setmIndexTag(int mIndexTag) {
        this.mIndexTag = mIndexTag;
        notifyDataSetChanged();
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_map;
    }

    @Override
    public void onBindViewHolder(XRvViewHolder holder, int position) {
        TextView bigTv=holder.getView(R.id.im_bigtv);

        TextView minTv=holder.getView(R.id.im_migtv);

        PoiInfo poiInfo=mDatas.get(position);

        bigTv.setText(poiInfo.name);

        minTv.setText(poiInfo.address);

        if(mIndexTag==position){
            bigTv.setTextColor(ContextCompat.getColor(bigTv.getContext(),R.color.app_sub_color));
            minTv.setTextColor(ContextCompat.getColor(minTv.getContext(),R.color.app_sub_color));
        }else{
            bigTv.setTextColor(ContextCompat.getColor(bigTv.getContext(),R.color.app_txt_black));
            minTv.setTextColor(ContextCompat.getColor(minTv.getContext(),R.color.app_txt_gray_light));
        }
    }

    /**
     * 重写此方法，每次更新数据后，item为第一个
     * @param datas     数据
     * @param isRefresh 是否刷新
     */
    @Override
    public void setDatas(@NonNull List<PoiInfo> datas, boolean isRefresh) {
        if(mUserPoiInfo!=null&&datas!=null) {
            datas.add(0, mUserPoiInfo);
        }
        super.setDatas(datas, isRefresh);
        mIndexTag=0;
    }



}
