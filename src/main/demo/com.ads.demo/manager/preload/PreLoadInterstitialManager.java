package com.ads.demo.manager.preload;

import android.app.Activity;

import com.ads.demo.manager.AdInterstitialManager;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdListener;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadInterstitialManager {
    private AdInterstitialManager mAdInterstitialManager;

    public PreLoadInterstitialManager(Activity activity, String adUnitId,  GMInterstitialAdLoadCallback interstitialAdLoadCallback) {
        mAdInterstitialManager = new AdInterstitialManager(activity,interstitialAdLoadCallback);
        mAdInterstitialManager.loadAdWithCallback(adUnitId);
    }

    public void show(Activity activity, GMInterstitialAdListener adInterstitialListener) {
        if(mAdInterstitialManager != null && mAdInterstitialManager.getInterstitialAd() != null){
            mAdInterstitialManager.getInterstitialAd().setAdInterstitialListener(adInterstitialListener);
            mAdInterstitialManager.getInterstitialAd().showAd(activity);
        }
    }

    public boolean isReady(){
        return mAdInterstitialManager.getInterstitialAd() != null
                && mAdInterstitialManager.getInterstitialAd().isReady();
    }

    public void destroy(){
        mAdInterstitialManager.destroy();
    }
}
