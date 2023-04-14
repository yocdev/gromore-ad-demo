package com.ads.demo.manager.preload;

import android.app.Activity;

import com.ads.demo.manager.AdBannerManager;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeToBannerListener;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadBannerManager {
    private AdBannerManager mAdBannerManager;

    public PreLoadBannerManager(Activity activity, String adUnit, GMBannerAdLoadCallback bannerAdLoadCallback,
                                GMBannerAdListener adBannerListener, GMNativeToBannerListener nativeToBannerListener) {
        mAdBannerManager = new AdBannerManager(activity,bannerAdLoadCallback, adBannerListener, nativeToBannerListener);
        mAdBannerManager.loadAdWithCallback(adUnit);
    }

    public GMBannerAd getBannerAd() {
        return mAdBannerManager.getBannerAd();
    }

    public void destroy(){
        mAdBannerManager.destroy();
    }
}
