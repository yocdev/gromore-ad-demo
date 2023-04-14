package com.ads.demo.custom.toutiao;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.draw.GMCustomDrawAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdAppInfo;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;

import java.util.List;

/**
 * Created by zhy on date
 * Usage:
 * Doc:
 */
public class TouTiaoDrawAd extends GMCustomDrawAd {
    /**
     * ps 广告展示时需调用callDrawAdShow();回传GroMore
     * ps 广告点击时需调用callDrawAdClick();回传GroMore
     */


    private static final String TAG = AppConst.TAG_PRE + TouTiaoDrawAd.class.getSimpleName();


    public TouTiaoDrawAd() {
        GMNativeAdAppInfo nativeAdAppInfo = new GMNativeAdAppInfo();
        setNativeAdAppInfo(nativeAdAppInfo); //设置安全合规五要素
        this.setExpressAd(false); //自渲染广告
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }


    /**
     * 注册事件，计费点
     *
     * @param context
     * @param container
     * @param clickViews
     * @param creativeViews
     * @param viewBinder
     */
    private void registerView(Context context, @NonNull ViewGroup container, List<View> clickViews, List<View> creativeViews, GMViewBinder viewBinder) {

    }


    @Override
    public void registerViewForInteraction(@NonNull Activity activity, @NonNull ViewGroup container, List<View> clickViews, List<View> creativeViews, GMViewBinder viewBinder) {
        super.registerViewForInteraction(activity, container, clickViews, creativeViews, viewBinder);
        registerView(activity, container, clickViews, creativeViews, viewBinder);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    /**
     * 是否准备好
     *
     * @return
     */
    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        return GMAdConstant.AdIsReadyStatus.AD_IS_READY;
    }

}

