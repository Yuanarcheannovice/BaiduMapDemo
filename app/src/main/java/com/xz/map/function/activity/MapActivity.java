package com.xz.map.function.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.xcoder.lib.annotation.ContentView;
import com.xcoder.lib.annotation.Injection;
import com.xcoder.lib.annotation.event.OnClick;
import com.xz.map.app.BaseActivity;
import com.xz.map.R;
import com.xz.map.function.controller.MapService;
import com.xz.map.util.AppStaticVariable;


/**
 * Created by xz on 2017/8/8 0008.
 * 关于地图的activity
 */
@ContentView(R.layout.activity_map)
public class MapActivity extends BaseActivity {

    @Injection
    MapService mSer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onXCoderCreate(Bundle savedInstanceState) {
        mSer.init(this);
    }

    @OnClick({R.id.am_submit, R.id.am_location, R.id.am_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.am_submit:
                //提交位置

                break;
            case R.id.am_location:
                //重新定位用户位置
                mSer.initUserLocation();
                break;
            case R.id.am_search:
                //搜索按钮
                openActivityResult(MapSearchActivity.class, null, AppStaticVariable.MAP_SEARCH_CODE);
                break;
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
                mSer.searchStr(address, lon, lat);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mSer.mCom.mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mSer.mCom.mMapView.onPause();
    }

    @Override
    public Object closeActivity() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mSer.onExit();
        mSer.mCom.mMapView.onDestroy();
        return "MapActivity";
    }
}
