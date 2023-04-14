package com.ads.pangle;

import android.content.Context;
import android.util.Log;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomInitConfig;
import com.bytedance.msdk.api.v2.ad.custom.init.GMCustomAdapterConfiguration;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdSdk;

import java.util.Map;

public class PangleCustomerConfig extends GMCustomAdapterConfiguration {

    private static final String TAG = AppConst.TAG_PRE + PangleCustomerConfig.class.getSimpleName();

    @Override
    public void initializeADN(Context context, GMCustomInitConfig gmCustomConfig, Map<String, Object> localExtra) {
        TTAdConfig ttAdConfig = new TTAdConfig.Builder()
                .appId(gmCustomConfig.getAppId())
                .appName("")
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .build();
        TTAdSdk.init(context, ttAdConfig, new TTAdSdk.InitCallback() {
            @Override
            public void success() {
                Log.i(TAG, "穿山甲初始化成功 " + Thread.currentThread().getName());
            }

            @Override
            public void fail(int i, String s) {
                Log.i(TAG, "穿山甲初始化失败 " + Thread.currentThread().getName());
            }
        });

        Log.i(TAG, "CustomerAdapterConfig " + Thread.currentThread().getName());
        callInitSuccess();
    }

    @Override
    public String getNetworkSdkVersion() {
        return "4.0.0.0";
    }

    @Override
    public String getAdapterSdkVersion() {
        return "1.0.0";
    }

}
