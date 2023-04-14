package com.ads.demo.manager;

import android.app.Activity;
import android.util.Log;

import com.ads.demo.App;
import com.ads.demo.AppConst;
import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAd;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitial;
import com.header.app.untext.R;

import java.util.List;

/**
 * 插屏管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdInterstitialManager {
    private static final String TAG = AppConst.TAG_PRE + AdInterstitialManager.class.getSimpleName();

    private Activity mActivity;
    /**
     * 插屏对应的广告对象
     * 每次加载全屏视频广告的时候需要新建一个GMInterstitialAd，否则可能会出现广告填充问题
     */
    private GMInterstitialAd mInterstitialAd;
    /**
     * 插屏加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMInterstitialAdLoadCallback mGMInterstitialAdLoadCallback;

    private String mAdUnitId; //广告位

    /**
     * ------------------------- 以下是必要实现，如果不实现会导致加载广告失败  --------------------------------------
     */

    /**
     * 管理类构造函数
     * @param activity 插屏展示的Activity
     * @param interstitialAdLoadCallback 插屏加载广告回调
     */
    public AdInterstitialManager(Activity activity, GMInterstitialAdLoadCallback interstitialAdLoadCallback) {
        mActivity = activity;
        mGMInterstitialAdLoadCallback = interstitialAdLoadCallback;
    }

    /**
     * 获取插屏广告对象
     */
    public GMInterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    /**
     * 加载插屏广告，如果没有config配置会等到加载完config配置后才去请求广告
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
     * 加载插屏广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param adUnitId  广告位ID
     */
    private void loadInteractionAd(String adUnitId) {
        //Context 必须传activity
        mInterstitialAd = new GMInterstitialAd(mActivity, adUnitId);

        /**
         * 创建全屏广告请求类型参数GMAdSlotInterstitial,具体参数含义参考文档
         */
        GMAdSlotInterstitial adSlotInterstitial = new GMAdSlotInterstitial.Builder()
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setImageAdSize(600, 600)
                .setVolume(0.5f)
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .build();

        mInterstitialAd.loadAd(adSlotInterstitial, mGMInterstitialAdLoadCallback);
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }
        mActivity = null;
        mGMInterstitialAdLoadCallback = null;
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
        if (mInterstitialAd == null) {
            return;
        }
    }

    /**
     * 打印加载失败的adn错误信息
     */
    public void printLoadFailAdnInfo() {
        if (mInterstitialAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "interstitial ad loadinfos: " + mInterstitialAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printShowAdInfo() {
        if (mInterstitialAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = mInterstitialAd.getShowEcpm();
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
