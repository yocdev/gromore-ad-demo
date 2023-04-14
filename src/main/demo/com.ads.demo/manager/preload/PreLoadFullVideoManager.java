package com.ads.demo.manager.preload;

import android.app.Activity;

import com.ads.demo.manager.AdFullVideoManager;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdLoadCallback;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadFullVideoManager {
    private AdFullVideoManager mAdFullVideoManager;

    public PreLoadFullVideoManager(Activity activity,String adUnitId,int orientation, GMFullVideoAdLoadCallback fullVideoAdLoadCallback) {
        mAdFullVideoManager = new AdFullVideoManager(activity,fullVideoAdLoadCallback);
        mAdFullVideoManager.loadAdWithCallback(adUnitId,orientation);
    }

    public void show(Activity activity) {
        if(mAdFullVideoManager != null && mAdFullVideoManager.getFullVideoAd() != null){
            mAdFullVideoManager.getFullVideoAd().showFullAd(activity);
        }
    }

    public boolean isReady(){
        return mAdFullVideoManager.getFullVideoAd() != null && mAdFullVideoManager.getFullVideoAd().isReady();
    }

    public void destroy(){
        mAdFullVideoManager.destroy();
    }
}
