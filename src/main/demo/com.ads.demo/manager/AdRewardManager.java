package com.ads.demo.manager;

import android.app.Activity;
import android.util.Log;

import com.ads.demo.App;
import com.ads.demo.AppConst;
import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.header.app.untext.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 激励管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdRewardManager {
    private static final String TAG = AppConst.TAG_PRE;

    /**
     * 激励对应的广告对象
     * 每次加载全屏视频广告的时候需要新建一个GMRewardAd，否则可能会出现广告填充问题
     */
    private GMRewardAd mGMRewardAd;
    private Activity mActivity;
    /**
     * 激励加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMRewardedAdLoadCallback mGMRewardedAdLoadCallback;
    /**
     * GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
     */
    private int mOrientation; //方向
    private String mAdUnitId; //广告位

    /**
     * ------------------------- 以下是必要实现，如果不实现会导致加载广告失败  --------------------------------------
     */

    /**
     * 管理类构造函数
     * @param activity 激励展示的Activity
     * @param rewardedAdLoadCallback 激励加载广告回调
     */
    public AdRewardManager(Activity activity, GMRewardedAdLoadCallback rewardedAdLoadCallback) {
        mActivity = activity;
        mGMRewardedAdLoadCallback = rewardedAdLoadCallback;
    }

    /**
     * 获取激励广告对象
     */
    public GMRewardAd getGMRewardAd() {
        return mGMRewardAd;
    }

    /**
     * 加载激励广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param adUnitId  广告位ID
     * @param orientation 方向
     */
    public void loadAdWithCallback(final String adUnitId, final int orientation) {
        this.mOrientation = orientation;
        this.mAdUnitId = adUnitId;

        if (GMMediationAdSdk.configLoadSuccess()) {
            loadAd(adUnitId, orientation);
        } else {
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 加载激励广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param adUnitId  广告位ID
     * @param orientation 方向
     */
    private void loadAd(String adUnitId, int orientation) {
        mGMRewardAd = new GMRewardAd(mActivity, adUnitId);

        /**
         * 激励视频服务端验证，GroMore会把设置的字符串透传给相应的ADN
         */
        Map<String, String> customData = new HashMap<>();
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_PANGLE, "pangle media_extra");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, "gdt custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_KS, "ks custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_SIGMOB, "sigmob custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_MINTEGRAL, "mintegral custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_BAIDU, "baidu custom data");
        // 如果开启了gromre服务端激励验证，可以传以下信息，跟adn无关。
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GROMORE_EXTRA, "gromore serverside verify extra data"); // 会透传给媒体的服务器

        /**
         * 创建激励广告请求类型参数GMAdSlotRewardVideo,具体参数含义参考文档
         */
        GMAdSlotRewardVideo adSlotRewardVideo = new GMAdSlotRewardVideo.Builder()
                .setMuted(true)//对所有SDK的激励广告生效，除需要在平台配置的SDK，如穿山甲SDK
                .setVolume(0f)//配合Admob的声音大小设置[0-1]
                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                .setCustomData(customData)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setUseSurfaceView(false) // 是否使用SurfaceView绘制，默认false。目前仅针对百度生效，建议使用false，否则百度会黑屏。
                .setOrientation(orientation)//必填参数，期望视频的播放方向：GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .setScenarioId("msdk-demo-scenarioId")
                .setExtraObject(GMAdConstant.PANGLE_VID, new int[]{1, 2, 3})
                .build();
        mGMRewardAd.loadAd(adSlotRewardVideo, mGMRewardedAdLoadCallback);
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mGMRewardAd != null) {
            mGMRewardAd.destroy();
        }
        mActivity = null;
        mGMRewardedAdLoadCallback = null;
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }

    /**
     * config配置回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            loadAd(mAdUnitId, mOrientation);
        }
    };


    /**
     * ------------------------- 以下是非必要功能请选择性使用  --------------------------------------
     */

    /**
     * 展示广告加载信息
     */
    public void printLoadAdInfo() {
        if (mGMRewardAd == null) {
            return;
        }
    }

    /**
     * 打印加载失败的adn错误信息
     */
    public void printLoadFailAdnInfo() {
        if (mGMRewardAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "reward ad loadinfos: " + mGMRewardAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printSHowAdInfo() {
        if (mGMRewardAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = mGMRewardAd.getShowEcpm();
        if (gmAdEcpmInfo == null) {
            return;
        }
        String s = App.getAppContext().getResources().getString(R.string.show_info,
                gmAdEcpmInfo.getAdNetworkRitId(),
                gmAdEcpmInfo.getAdnName(),
                gmAdEcpmInfo.getPreEcpm());
        Map<String, String> customData = gmAdEcpmInfo.getCustomData();
        Logger.e(TAG, s + ", customData: " + customData);
    }

}
