package com.xz.map.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.xcoder.lib.app.AppActivityManager;
import com.xcoder.lib.injection.ViewUtils;


/**
 * 类名:BaseActivity
 * @author xz
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppActivityManager.getInstance().pushActivity(this);
        ViewUtils.inject(this);
        onXCoderCreate(savedInstanceState);
    }

    /**
     * param pClass
     * 方法说明:启动指定activity
     * 方法名称:openActivity
     * 返回void
     */
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * param pClass
     * param pBundle
     * 方法说明:启动到指定activity，Bundle传递对象（作用个界面之间传递数据）
     * 方法名称:openActivity
     * 返回void
     */
    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * param pAction
     * 方法说明:根据界面name启动到指定界 * 方法名称:openActivity
     * 返回void
     */
    public void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * param pAction
     * param pBundle
     * 方法说明:根据界面name启动到指定界面，Bundle传递对象（作用个界面之间传递数据）
     * 方法名称:openActivity
     * 返回void
     */
    public void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }


    /**
     * param pClass
     * param pBundle
     * param requestCode
     * 方法说明:A-Activity需要在B-Activtiy中执行一些数据操作， 而B-Activity又要将，执行操作数据的结果返回给A-Activity
     * Bundle传递对象（作用个界面之间传递数据）
     * 方法名称:openActivityResult
     * 返回void
     */
    public void openActivityResult(Class<?> pClass, Bundle pBundle,
                                   int requestCode) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivityForResult(intent, requestCode);
    }


    /**
     * 方法说明:退出栈中所有Activity
     * 方法名称:finishBase
     * 返回void
     */
    public void finishBase() {
        AppActivityManager.getInstance().finishAllActivityExceptOne(getClass());
        finish();
    }

    /**
     * 重写方法onDestroy
     * 父类:see android.app.Activity#onDestroy()
     * 方法说明:
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁当前的activity
        closeActivity();
        AppActivityManager.getInstance().finishActivity(this);
    }

    /**
     * param savedInstanceState
     * 方法说明:初始化界 * 方法名称:onPreOnCreate
     * 返回void
     *
     * @param savedInstanceState savedInstanceState
     */
    public abstract void onXCoderCreate(Bundle savedInstanceState);

    /**
     * 方法说明:手动释放内存
     * 方法名称:releaseMemory
     * 返回void
     *
     * @return
     */
    public abstract void closeActivity();


}
