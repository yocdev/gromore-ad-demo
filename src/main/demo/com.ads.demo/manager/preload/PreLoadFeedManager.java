package com.ads.demo.manager.preload;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.ads.demo.manager.AdFeedManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;

import java.util.List;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadFeedManager {
    private AdFeedManager mAdFeedManager;
    private List<GMNativeAd> mAds;

    public PreLoadFeedManager(Activity activity, String adUnitId,int adCount,int styleType,GMNativeAdLoadCallback nativeAdLoadCallback) {
        mAdFeedManager = new AdFeedManager(activity, new GMNativeAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull List<GMNativeAd> listAd) {
                mAds = listAd;
                nativeAdLoadCallback.onAdLoaded(listAd);
            }

            @Override
            public void onAdLoadedFail(@NonNull AdError adError) {
                nativeAdLoadCallback.onAdLoadedFail(adError);
            }
        });
        mAdFeedManager.loadAdWithCallback(adUnitId,adCount,styleType);
    }

    public GMNativeAd getGMNativeAd(){
        if(mAds != null && mAds.size() > 0){
            return mAds.get(0);
        }
        return null;
    }

    public void destroy(){
        mAdFeedManager.destroy();
        if(mAds != null){
            mAds.clear();
        }
    }
}
