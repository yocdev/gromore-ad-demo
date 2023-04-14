package com.ads.demo;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import androidx.multidex.MultiDex;
import android.util.Log;
import android.webkit.WebView;

import com.ads.demo.config.GMAdManagerHolder;
//import com.diggo.sdk.DigGo;
import com.bytedance.msdk.api.UIUtils;
import com.facebook.stetho.Stetho;

/**
 * created by wuzejian on 2020-03-12
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        // 支持多进程，需要设置webview的data目录，不然会崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = UIUtils.getCurrProcessName(this);
            if (!"com.header.app.untext".equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        Stetho.initializeWithDefaults(this);

        //--------------MSDK的初始化 start----------------
        GMAdManagerHolder.init(this);
        //--------------MSDK的初始化 end----------------

        Log.d("App", "App-->onCreate-0<TTAdManagerHolder.init");
        //启用phone 端调试工具。必须在onCreate 之后调用。
//        DigGo.showLens(this);// optional 【可选】
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        // config :传入 Application 对象 注意必须确保只在主进程初始化。
//        DigGo.config(this, true, true)
//                .enableFlipper(true)//必须
//                //可选. 用于自动化启动分析，作为启动结束的标记点。取界面中的ViewGroup的ViewID。注意： id 必须是ViewGroup Id
//                .init();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
