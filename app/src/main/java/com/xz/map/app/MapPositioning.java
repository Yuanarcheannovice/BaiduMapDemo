package com.xz.map.app;

import android.content.Context;
import android.util.Log;

import com.archeanx.lib.util.ToastUtil;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * 定位
 * Created by xz on 2016/10/11 0011.
 *
 * @author xz
 */

public class MapPositioning {

    private static MapPositioning mMapPositioning = null;

    private LocationClient mLocationClient;




    public MapPositioning(Context context) {
        //声明LocationClient类
        mLocationClient = new LocationClient(context);
        initLocation();
        //注册监听函数
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {

            /**
             * 接受位置内部类
             */
            @Override
            public void onReceiveLocation(BDLocation location) {
                mLocationClient.stop();

                //定位成功
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeOffLineLocation) {
                    if (mXLocation != null) {
                        mXLocation.locSuccess(location);
                    }
                }
                //定位失败
                if (location.getLocType() == BDLocation.TypeServerError || location.getLocType() == BDLocation.TypeNetWorkException || location.getLocType() == BDLocation.TypeCriteriaException) {
                    if (mXLocation != null) {
                        mXLocation.locFailure(location.getLocType(), "定位失败,请检查网络");
                    }
                }

                //Receive Location
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());


                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    sb.append("\nspeed : ");
                    // 单位：公里每小时
                    sb.append(location.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    // 单位：米
                    sb.append(location.getAltitude());
                    sb.append("\ndirection : ");
                    // 单位度
                    sb.append(location.getDirection());
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    //运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    ToastUtil.show("定位失败，请重试！");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    ToastUtil.show("定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    ToastUtil.show("定位失败，请检查是否是飞行模式");
                }
                sb.append("\nlocationdescribe : ");
                // 位置语义化信息
                sb.append(location.getLocationDescribe());
//                List<Poi> list = location.getPoiList();// POI数据
                Log.i("BaiduLocationApiDem", sb.toString());
            }


        });
    }

    /**
     * 开始定位
     */
    public MapPositioning start() {
        if (mLocationClient != null) {
            mLocationClient.start();
            mLocationClient.requestLocation();
        }
        return this;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        int span = 1;
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }


    public void onExit() {
        if (mXLocation != null) {
            mXLocation = null;
        }
    }


    private XbdLocation mXLocation;

    public void setmLocation(XbdLocation location) {
        this.mXLocation = location;
    }

    public interface XbdLocation {
        /**
         * 定位成功
         *
         * @param location 位置信息
         */
        void locSuccess(BDLocation location);

        /**
         * 定位错误
         *
         * @param errorType   错误类型
         * @param errorString 错误提示
         */
        void locFailure(int errorType, String errorString);
    }

}
