package com.ads.demo.manager.preload;

import android.app.Activity;

import com.ads.demo.manager.AdRewardManager;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadRewardManager {
    private AdRewardManager mAdRewardManager;

    public PreLoadRewardManager(Activity activity, String adUnitId,int orientation, GMRewardedAdLoadCallback rewardedAdLoadCallback) {
        mAdRewardManager = new AdRewardManager(activity,rewardedAdLoadCallback);
        mAdRewardManager.loadAdWithCallback(adUnitId,orientation);
    }

    public void show(Activity activity, GMRewardedAdListener adRewardedListener,GMRewardedAdListener rewardPlayAgainListener) {
        if(mAdRewardManager != null && mAdRewardManager.getGMRewardAd() != null){
            mAdRewardManager.getGMRewardAd().setRewardAdListener(adRewardedListener);
            mAdRewardManager.getGMRewardAd().setRewardPlayAgainListener(rewardPlayAgainListener);
            mAdRewardManager.getGMRewardAd().showRewardAd(activity);
        }
    }

    public boolean isReady(){
        return mAdRewardManager.getGMRewardAd() != null
                && mAdRewardManager.getGMRewardAd().isReady();
    }

    public void destroy(){
        mAdRewardManager.destroy();
    }
}
