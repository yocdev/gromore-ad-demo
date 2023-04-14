package com.ads.demo.custom.toutiao;

import android.content.Context;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomInitConfig;
import com.bytedance.msdk.api.v2.ad.custom.init.GMCustomAdapterConfiguration;

import java.util.Map;

/**
 * toutiao 自定义初始化类
 */
public class TouTiaoCustomerConfig extends GMCustomAdapterConfiguration {

    private static final String TAG = AppConst.TAG_PRE + TouTiaoCustomerConfig.class.getSimpleName();

    /**
     * 站内初始化
     *
     * @param context
     * @param gmCustomConfig
     * @param localExtra
     */
    @Override
    public void initializeADN(Context context, GMCustomInitConfig gmCustomConfig, Map<String, Object> localExtra) {

    }

    /**
     * 站内广告版本号
     *
     * @return
     */
    @Override
    public String getNetworkSdkVersion() {
        return "1.0";
    }

    /**
     * 自定义adpter版本号
     *
     * @return
     */
    @Override
    public String getAdapterSdkVersion() {
        return "1.0.0";
    }

    /**
     * 站内广告token信息
     *
     * @param context
     * @param extra
     * @return
     */
    @Override
    public String getBiddingToken(Context context, Map<String, Object> extra) {
        return "";
    }

}
