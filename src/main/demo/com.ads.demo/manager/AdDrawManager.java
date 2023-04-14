package com.ads.demo.manager;

import android.app.Activity;
import android.util.Log;

import com.ads.demo.App;
import com.ads.demo.AppConst;
import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawAd;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.draw.GMUnifiedDrawAd;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAd;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotDraw;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitial;
import com.header.app.untext.R;

import java.util.List;

/**
 * draw管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdDrawManager {
    private static final String TAG = AppConst.TAG_PRE + AdDrawManager.class.getSimpleName();

    private Activity mActivity;
    /**
     * draw对应的广告对象
     * 每次加载draw广告的时候需要新建一个GMUnifiedDrawAd，否则可能会出现广告填充问题
     */
    private GMUnifiedDrawAd mGMUnifiedDrawAd;
    /**
     * draw广告回调
     * 请在加载广告成功后展示广告
     */
    private GMDrawAdLoadCallback mGMDrawAdLoadCallback;

    private String mAdUnitId; //广告位

    /**
     * ------------------------- 以下是必要实现，如果不实现会导致加载广告失败  --------------------------------------
     */

    /**
     * 管理类构造函数
     * @param activity 插屏展示的Activity
     * @param drawAdLoadCallback 插屏加载广告回调
     */
    public AdDrawManager(Activity activity, GMDrawAdLoadCallback drawAdLoadCallback) {
        mActivity = activity;
        mGMDrawAdLoadCallback = drawAdLoadCallback;
    }

    /**
     * 获取插屏广告对象
     */
    public GMUnifiedDrawAd getGMUnifiedDrawAd() {
        return mGMUnifiedDrawAd;
    }

    /**
     * 加载draw广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param adUnitId  广告位ID
     */
    public void loadAdWithCallback(final String adUnitId) {
        this.mAdUnitId = adUnitId;

        if (GMMediationAdSdk.configLoadSuccess()) {
            loadInteractionAd(adUnitId);
        } else {
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 加载draw广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param adUnitId  广告位ID
     */
    private void loadInteractionAd(String adUnitId) {
        //Context 必须传activity
        mGMUnifiedDrawAd = new GMUnifiedDrawAd(mActivity, adUnitId);

        /**
         * 创建全屏广告请求类型参数GMAdSlotDraw,具体参数含义参考文档
         */
        GMAdSlotDraw adSlotDraw = new GMAdSlotDraw.Builder()
                .setImageAdSize(600, 600)
                .setAdCount(1)
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .build();

        mGMUnifiedDrawAd.loadAd(adSlotDraw, mGMDrawAdLoadCallback);
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mGMUnifiedDrawAd != null) {
            mGMUnifiedDrawAd.destroy();
        }
        mActivity = null;
        mGMDrawAdLoadCallback = null;
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }


    /**
     * config配置回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            loadInteractionAd(mAdUnitId);
        }
    };


    /**
     * ------------------------- 以下是非必要功能请选择性使用  --------------------------------------
     */

    /**
     * 展示广告加载信息
     */
    public void printLoadAdInfo() {
        if (mGMUnifiedDrawAd == null) {
            return;
        }
    }

    /**
     * 打印加载失败的adn错误信息
     */
    public void printLoadFailAdnInfo() {
        if (mGMUnifiedDrawAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "draw ad loadinfos: " + mGMUnifiedDrawAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printShowAdInfo(GMDrawAd drawAd) {
        if (drawAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = drawAd.getShowEcpm();
        if (gmAdEcpmInfo == null) {
            return;
        }
        String s = App.getAppContext().getResources().getString(R.string.show_info,
                gmAdEcpmInfo.getAdNetworkRitId(),
                gmAdEcpmInfo.getAdnName(),
                gmAdEcpmInfo.getPreEcpm());
        Logger.e(TAG, s);
    }

}
