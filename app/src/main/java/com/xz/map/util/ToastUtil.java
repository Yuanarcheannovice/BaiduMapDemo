package com.xz.map.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.xz.map.App;

public class ToastUtil {

    public static void showToast(String string) {
        Toast.makeText(App.mContext, string, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String string) {
        Toast.makeText(App.mContext, string, Toast.LENGTH_LONG).show();
    }

    /**
     * 暂无更多数据吐司
     */
    public static void noData() {
        Toast.makeText(App.mContext, "暂无更多数据", Toast.LENGTH_SHORT).show();
    }

    /**
     * 无网络吐司
     *
     * @param
     */
    public static void noNet() {
        Toast.makeText(App.mContext, "网络繁忙,请检查网络!", Toast.LENGTH_SHORT).show();
    }

    public static void noServiceData() {
        Toast.makeText(App.mContext, "获取失败,服务器繁忙!", Toast.LENGTH_SHORT).show();
    }


    public static void doException(Exception exc) {
        exc.getLocalizedMessage();
        if (TextUtils.equals(exc.toString(), "java.net.SocketTimeoutException")) {
            showToast("服务器连接超时，请稍后再试");
        } else {
            noNet();
        }
    }

}
