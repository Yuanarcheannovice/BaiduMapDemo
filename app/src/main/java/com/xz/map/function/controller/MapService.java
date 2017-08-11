package com.xz.map.function.controller;


import android.app.ProgressDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.xcoder.lib.annotation.Injection;
import com.xz.map.R;
import com.xz.map.app.MapPositioning;
import com.xz.map.function.activity.MapActivity;
import com.xz.map.function.adapter.MapAdapter;
import com.xz.map.function.model.MapComponter;
import com.xz.map.util.ToastUtil;
import com.xz.map.util.adapter.RvPureAdapter;

import java.util.List;

/**
 * Created by xz on 2017/8/8 0008.
 */

public class MapService {
    @Injection
    public MapComponter mCom;

    private MapActivity mActivity;
    private float mapZoom = 19;//地图放大级别
    private BaiduMap mBaiduMap;
    private MapAdapter mMapAdapter;
    private PoiSearch mPoiSearch;
    private MapPositioning mMapPositioning;
    private GeoCoder mGeoCoder;
    private boolean isRvClick = false;//是否是点击列表导致的移动
    private ProgressDialog mProgressDialog;

    public void init(MapActivity activity) {
        this.mActivity = activity;
        initView();
        initSetting();
        initListener();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mBaiduMap = mCom.mMapView.getMap();
        mCom.mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mCom.mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
        mMapAdapter = new MapAdapter();
        mMapAdapter.setOnItemClickListener(new RvPureAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                isRvClick = true;
                PoiInfo poiInfo = mMapAdapter.getItem(position);
                setNewLatLngZoom(poiInfo.location);
                mMapAdapter.setmIndexTag(position);
            }
        });
        mCom.mRecyclerView.setAdapter(mMapAdapter);
    }

    /**
     * 初始化地图的设置
     */
    private void initSetting() {
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        //是否允许旋转手势
        uiSettings.setRotateGesturesEnabled(false);
        //是否允许指南针
        uiSettings.setCompassEnabled(false);
        //是否允许俯视手势
        uiSettings.setOverlookingGesturesEnabled(false);
        //是否显示缩放控件
        mCom.mMapView.showZoomControls(false);
        //是否显示比例尺
        mCom.mMapView.showScaleControl(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 当不需要定位图层时关闭定位图层
        //mBaiduMap.setMyLocationEnabled(false);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //楼快效果
        mBaiduMap.setBuildingsEnabled(true);
        //设置放大缩小级别
        mBaiduMap.setMaxAndMinZoomLevel(21, 10);
        //热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        //交通图
//        mBaiduMap.setTrafficEnabled(true);//
        //室内地图
//        mBaiduMap.setIndoorEnable(true);
        //设置是否显示室内图标注, 默认显示
//        mBaiduMap.showMapIndoorPoi(true);
    }

    /**
     * 设置xy
     */
    private LatLng setLatLng(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
    }


    /**
     * 设置标记点的放大级别
     */
    private void setNewLatLngZoom(LatLng latLng) {
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, mapZoom));
    }

    /**
     * 定位用户位置用户位置
     */
    public void initUserLocation() {

        mProgressDialog = ProgressDialog.show(mActivity, null, "正在定位,请稍后");
        //开启定位
        mMapPositioning = MapPositioning.getInstance();
        mMapPositioning.setmLocation(new MapPositioning.XLocation() {

            @Override
            public void locSuccess(BDLocation location) {
                mProgressDialog.dismiss();
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        //设置精确度
                        .accuracy(0)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0)
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();

                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_user_mark);
                //保存配置，定位图层显示方式，是否允许显示方向信息，用户自定义定位图标
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
                mBaiduMap.setMyLocationConfiguration(config);
                //移动到屏幕中心
                LatLng latLng = setLatLng(location.getLatitude(), location.getLongitude());
                setNewLatLngZoom(latLng);

                //设置用户地址
                PoiInfo userPoi = new PoiInfo();
                userPoi.location = latLng;

                userPoi.address = location.getAddrStr() + location.getLocationDescribe();
                userPoi.name = "[位置]";
                mMapAdapter.setmUserPoiInfo(userPoi);

                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }

            @Override
            public void locFailure(int errorType, String errorString) {
                mProgressDialog.dismiss();
                ToastUtil.showToast(errorString);
            }
        });
        mMapPositioning.start();
    }


    /**
     * 搜索返回后，需要先搜索
     */
    public void searchStr(String address, double lon, double lat) {
        if (lon > 0 && lat > 0) {
            LatLng latLng = setLatLng(lat, lon);
            //设置搜索地址
            PoiInfo userPoi = new PoiInfo();
            userPoi.location = latLng;
            userPoi.address = address;
            userPoi.name = "[位置]";
            mMapAdapter.setmUserPoiInfo(userPoi);
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            setNewLatLngZoom(latLng);
        }
    }


    /**
     * 检索 创建
     */
    private void createSearch() {
        //兴趣点检索   没有用到
        mPoiSearch = PoiSearch.newInstance();

        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            public void onGetPoiResult(PoiResult result) {
                //获取POI检索结果
                mMapAdapter.setDatas(result.getAllPoi(), true);
            }

            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                //poi 室内检索结果回调
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        /**
         *
         * mPoiSearch.searchInCity((new PoiCitySearchOption())
         .city(“北京”)
         .keyword(“美食”)
         .pageNum(10));
         .pageNum(10));
         *
         */

        //地里编码
        mGeoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener getGeoListener = new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }
                //设置搜索地址
                PoiInfo userPoi = new PoiInfo();
                userPoi.location = result.getLocation();
                userPoi.address = result.getSematicDescription();
                userPoi.name = "[位置]";
                mMapAdapter.setmUserPoiInfo(userPoi);

                //获取反向地理编码结果
                List<PoiInfo> poiList = result.getPoiList();
                mMapAdapter.setDatas(poiList, true);
            }
        };
        mGeoCoder.setOnGetGeoCodeResultListener(getGeoListener);
    }

    /**
     * 地图监听
     */
    private void initListener() {
        //地图加载完成回调
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                createSearch();
                initUserLocation();
                mCom.mLocationButton.setVisibility(View.VISIBLE);
                mCom.mSearchButton.setVisibility(View.VISIBLE);
            }

        });
        //单击事件监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //监听地图状态
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (!isRvClick) {
                    mapStatus.toString();

                    //得到中心点坐标，开始反地理编码
                    LatLng centerLatLng = mapStatus.target;
                    mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(centerLatLng));
                }
            }
        });
        //监听地图的按下事件
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //如果用户触碰了地图，那么把 isRvClick 还原
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isRvClick = false;
                }
            }
        });
    }


    public void onExit() {
        if (mMapPositioning != null)
            mMapPositioning.onExit();



        if (mPoiSearch != null)
            mPoiSearch.destroy();

        if (mGeoCoder != null) {
            mGeoCoder.destroy();
        }
    }
}
