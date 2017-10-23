package com.xz.map.function.adapter;


import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.xz.map.R;
import com.xz.xadapter.XRvPureDataAdapter;
import com.xz.xadapter.xutil.XRvViewHolder;

/**
 * Created by xz on 2017/8/9 0009.
 * 地图 地址列表搜索 适配器
 * @author xz
 */

public class MapSearchAdapter extends XRvPureDataAdapter<SuggestionResult.SuggestionInfo> {

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_map;
    }

    @Override
    public void onBindViewHolder(XRvViewHolder holder, int position) {
        TextView bigTv=holder.getView(R.id.im_bigtv);
        SuggestionResult.SuggestionInfo ss=mDatas.get(position);

        bigTv.setText(ss.city+ss.district+ss.key);

//        minTv.setText(poiInfo.address);

    }


}
