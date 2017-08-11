package com.xz.map.app;

import android.app.ActivityManager;
import android.content.Context;
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
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //隐藏标题栏
//        getSupportActionBar().hide();
        //开启全屏模式才用这个，类似于小书阅读器
//        getWindow().
//                setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AppActivityManager.getScreenManager().pushActivity(this);
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
        AppActivityManager.getScreenManager().popAllActivityExceptOne(
                getClass());
        finish();
    }

    /**
     * param context                上下 * param isBackground是否开开启后台运 * 返回值：void
     * param //isBackground是否开开启后台运 * 返回void
     * 方法说明:退出应用程 * 方法名称:AppExit
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context, Boolean isBackground) {
        try {
            finishBase();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 注意，如果您有后台程序运行，请不要支持此句子
            if (!isBackground) {
                System.exit(0);
            }
        }
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
        AppActivityManager.getScreenManager().popActivity(this);
    }

    /**
     * param savedInstanceState
     * 方法说明:初始化界 * 方法名称:onPreOnCreate
     * 返回void
     */
    public abstract void onXCoderCreate(Bundle savedInstanceState);

    /**
     * 方法说明:手动释放内存
     * 方法名称:releaseMemory
     * 返回void
     */
    public abstract Object closeActivity();



}
