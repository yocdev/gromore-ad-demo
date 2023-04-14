package com.ads.demo.manager.preload;

import android.app.Activity;

import com.ads.demo.manager.AdInterstitialFullManager;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdListener;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadInterstitialFullManager {
    private AdInterstitialFullManager mAdInterstitialFullManager;

    public PreLoadInterstitialFullManager(Activity activity, String adUnitId, GMInterstitialFullAdLoadCallback interstitialFullAdLoadCallback) {
        mAdInterstitialFullManager = new AdInterstitialFullManager(activity,interstitialFullAdLoadCallback);
        mAdInterstitialFullManager.loadAdWithCallback(adUnitId);
    }

    public void show(Activity activity, GMInterstitialFullAdListener adInterstitialListener) {
        if(mAdInterstitialFullManager != null && mAdInterstitialFullManager.getGMInterstitialFullAd() != null){
            mAdInterstitialFullManager.getGMInterstitialFullAd().setAdInterstitialFullListener(adInterstitialListener);
            mAdInterstitialFullManager.getGMInterstitialFullAd().showAd(activity);
        }
    }

    public boolean isReady(){
        return mAdInterstitialFullManager.getGMInterstitialFullAd() != null
                && mAdInterstitialFullManager.getGMInterstitialFullAd().isReady();
    }

    public void destroy(){
        mAdInterstitialFullManager.destroy();
    }
}
