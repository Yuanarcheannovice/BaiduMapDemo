package com.xz.map.function.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.archeanx.lib.adapter.XRvPureAdapter;
import com.archeanx.lib.util.ToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xz.map.R;
import com.xz.map.app.MapPositioning;
import com.xz.map.function.adapter.MapAdapter;
import com.xz.map.util.AppStaticVariable;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

/**
 * @author Yuanarcheannovice
 * https://github.com/Yuanarcheannovice/BaiduMapDemo
 * <p>
 * 加载地图完成->判断是否有权限定位->定位成功后，获取定位数据和附近数据
 */
public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    public RecyclerView mRecyclerView;

    public SmartRefreshLayout mSmartRefreshLayout;

    public MapView mMapView;

    /**
     * 定位按钮
     */
    public ImageView mLocationButton;

    /**
     * 搜索按钮
     */
    public ImageView mSearchButton;

    /**
     * 地图放大级别
     */
    private float mapZoom = 19;
    private BaiduMap mBaiduMap;
    private MapAdapter mMapAdapter;
    /**
     * 地里编码
     */
    private GeoCoder mGeoCoder;
    /**
     * 是否是点击列表导致的移动
     */
    private boolean isRvClick = false;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //
        mMapView = findViewById(R.id.am_map);
        mBaiduMap = mMapView.getMap();
        //
        mLocationButton = findViewById(R.id.am_location);
        mSearchButton = findViewById(R.id.am_search);
        TextView submitTv = findViewById(R.id.am_submit);
        //
        mSmartRefreshLayout = findViewById(R.id.am_srl);
        mRecyclerView = findViewById(R.id.am_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mMapAdapter = new MapAdapter();
        //条目点击移动界面
        mMapAdapter.setOnItemClickListener(new XRvPureAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                isRvClick = true;
                PoiInfo poiInfo = mMapAdapter.getItem(position);
                setNewLatLngZoom(poiInfo.location);
                mMapAdapter.setmIndexTag(position);
            }
        });
        mRecyclerView.setAdapter(mMapAdapter);

        mSearchButton.setOnClickListener(this);
        mLocationButton.setOnClickListener(this);
        submitTv.setOnClickListener(this);

        initSetting();
        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.am_submit:
                //提交位置
                doSubmit();
                break;
            case R.id.am_location:
                //重新定位用户位置
                initUserLocation();
                break;
            case R.id.am_search:
                //搜索按钮
                startActivityForResult(new Intent(this, MapSearchActivity.class), AppStaticVariable.MAP_SEARCH_CODE);
                break;
            default:
                break;
        }
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
        mMapView.showZoomControls(false);
        //是否显示比例尺
        mMapView.showScaleControl(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
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
        return new LatLng(lat, lon);
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
        mProgressDialog = ProgressDialog.show(this, null, "正在定位,请稍后");
        //开启定位
        MapPositioning mapPositioning = new MapPositioning(this);
        mapPositioning.setmLocation(new MapPositioning.XbdLocation() {
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
                ToastUtil.show(errorString);
            }
        });
        mapPositioning.start();
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
        PoiSearch poiSearch = PoiSearch.newInstance();

        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                //获取POI检索结果
                mMapAdapter.setDatas(result.getAllPoi(), true);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                //poi 室内检索结果回调
            }
        };
        //mPoiSearch.searchInCity((new PoiCitySearchOption()).city(“北京”).keyword(“美食”).pageNum(10)).pageNum(10));
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        //地里编码
        mGeoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener getGeoListener = new OnGetGeoCoderResultListener() {
            @Override
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
                mRecyclerView.scrollToPosition(0);
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
                mLocationButton.setVisibility(View.VISIBLE);
                mSearchButton.setVisibility(View.VISIBLE);
                requestPermission();
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
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (!isRvClick) {
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

    /**
     * 获取权限，地图不需要权限，但是定位需要，建议在地图加载完成之后，再获取定位
     */
    private void requestPermission() {
        //扫码权限，文件权限
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        initUserLocation();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        ToastUtil.show("没有权限无法使用");
                    }
                }).start();
    }

    /**
     * 提交位置信息
     */
    public void doSubmit() {
        if (mMapAdapter.getDatas().size() > 0) {
            PoiInfo item = mMapAdapter.getItem(mMapAdapter.getmIndexTag());
            ToastUtil.show("经度:" + item.location.longitude + "-纬度:" + item.location.latitude + "-地址:" + item.address);
        } else {
            ToastUtil.show("请选择地址");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppStaticVariable.MAP_SEARCH_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String address = data.getStringExtra(AppStaticVariable.MAP_SEARCH_ADDRESS);
                double lon = data.getDoubleExtra(AppStaticVariable.MAP_SEARCH_LONGITUDE, 0.0);
                double lat = data.getDoubleExtra(AppStaticVariable.MAP_SEARCH_LATITUDE, 0.0);
                searchStr(address, lon, lat);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }


}
