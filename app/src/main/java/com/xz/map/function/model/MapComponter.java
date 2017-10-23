package com.xz.map.function.model;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.baidu.mapapi.map.MapView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xcoder.lib.annotation.ViewInject;
import com.xz.map.R;


/**
 * Created by xz on 2017/8/8 0008.
 * @author xz
 */

public class MapComponter {

    @ViewInject(R.id.am_rv)
    public RecyclerView mRecyclerView;

    @ViewInject(R.id.am_srl)
    public SmartRefreshLayout mSmartRefreshLayout;

    @ViewInject(R.id.am_map)
    public MapView mMapView;

    /**
     * 定位按钮
     */
    @ViewInject(R.id.am_location)
    public ImageView mLocationButton;

    /**
     * 搜索按钮
     */
    @ViewInject(R.id.am_search)
    public ImageView mSearchButton;
}
