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
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAd;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotFullVideo;
import com.header.app.untext.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全屏管理类。
 * 只需要复制粘贴到项目中，通过回调处理相应的业务逻辑即可使用完成广告加载&展示
 */
public class AdFullVideoManager {
    private static final String TAG = AppConst.TAG_PRE + AdFullVideoManager.class.getSimpleName();

    /**
     * 全屏对应的广告对象
     * 每次加载全屏视频广告的时候需要新建一个GMFullVideoAd，否则可能会出现广告填充问题
     */
    private GMFullVideoAd mFullVideoAd;
    private Activity mActivity;
    /**
     * 全屏加载广告回调
     * 请在加载广告成功后展示广告
     */
    private GMFullVideoAdLoadCallback mGMFullVideoAdLoadCallback;
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
     * @param activity 全屏展示的Activity
     * @param fullVideoAdLoadCallback 全屏加载广告回调
     */
    public AdFullVideoManager(Activity activity, GMFullVideoAdLoadCallback fullVideoAdLoadCallback) {
        mActivity = activity;
        mGMFullVideoAdLoadCallback = fullVideoAdLoadCallback;
    }
    /**
     * 获取全屏广告对象
     */
    public GMFullVideoAd getFullVideoAd() {
        return mFullVideoAd;
    }

    /**
     * 加载全屏广告，如果没有config配置会等到加载完config配置后才去请求广告
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
     * 加载全屏广告，如果没有config配置会等到加载完config配置后才去请求广告
     * @param adUnitId  广告位ID
     * @param orientation 方向
     */
    private void loadAd(String adUnitId, int orientation) {
        mFullVideoAd = new GMFullVideoAd(mActivity, adUnitId);

        Map<String, String> customData = new HashMap<>();
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, "gdt custom data");//目前仅支持gdt

        /**
         * 创建全屏广告请求类型参数GMAdSlotFullVideo,具体参数含义参考文档
         */
        GMAdSlotFullVideo adSlotFullVideo = new GMAdSlotFullVideo.Builder()
                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                .setUserID("user123")//用户id,必传参数
                .setOrientation(orientation)//必填参数，期望视频的播放方向
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setCustomData(customData) //服务端验证
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .build();
        mFullVideoAd.loadAd(adSlotFullVideo, mGMFullVideoAdLoadCallback);
    }

    /**
     * 在Activity onDestroy中需要调用清理资源
     */
    public void destroy() {
        if (mFullVideoAd != null) {
            mFullVideoAd.destroy();
        }
        mActivity = null;
        mGMFullVideoAdLoadCallback = null;
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
        if (mFullVideoAd == null) {
            return;
        }
    }

    /**
     * 打印加载失败的adn错误信息
     */
    public void printLoadFailAdnInfo() {
        if (mFullVideoAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "fullvideo ad loadinfos: " + mFullVideoAd.getAdLoadInfoList());
    }

    /**
     * 打印已经展示的广告信息
     */
    public void printSHowAdInfo() {
        if (mFullVideoAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = mFullVideoAd.getShowEcpm();
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
