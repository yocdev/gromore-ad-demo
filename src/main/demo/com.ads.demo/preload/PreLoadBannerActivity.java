package com.ads.demo.preload;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.manager.preload.PreLoadBannerManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeAdInfo;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeToBannerListener;
import com.bytedance.msdk.api.TToast;
import com.header.app.untext.R;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadBannerActivity extends BaseActivity {
    private static final String TAG = AppConst.TAG_PRE + PreLoadBannerActivity.class.getSimpleName();
    private FrameLayout mBannerContainer;

    private Button mButtonShow;
    private TextView mTvAdUnitId;
    //广告位id
    private String mAdUnitId;
    //是否展示过了
    private boolean mIsShow;
    //是否load失败
    private boolean mIsLoadFail;
    //预加载广告管理类
    private PreLoadBannerManager mPreLoadBannerManager;
    // banner广告事件的监听
    private GMBannerAdListener mAdBannerListener;
    private GMNativeToBannerListener mNativeToBannerListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload_banner);
        mButtonShow = findViewById(R.id.btn_show);
        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        //放置banner的父容器
        mBannerContainer = findViewById(R.id.banner_container);
        mAdUnitId = getResources().getString(R.string.banner_unit_id);
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        initListener();
        initAdLoader();
    }

    @Override
    public void initAdLoader() {
        mPreLoadBannerManager = new PreLoadBannerManager(this, mAdUnitId, new GMBannerAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(AdError adError) {
                mIsLoadFail = true;
                TToast.show(PreLoadBannerActivity.this, "广告加载失败");
                Log.e(TAG, "load banner ad error : " + adError.code + ", " + adError.message);
                mBannerContainer.removeAllViews();
            }

            @Override
            public void onAdLoaded() {
                TToast.show(PreLoadBannerActivity.this, "广告加载成功");
                Log.i(TAG, "banner load success ");
            }
        }, mAdBannerListener, mNativeToBannerListener);
    }

    @Override
    public void initListener() {
        mButtonShow.setOnClickListener(v -> {
            if (mIsLoadFail) {
                TToast.show(PreLoadBannerActivity.this, "预缓存失败，请退出页面重新进入");
            } else if (mIsShow) {
                TToast.show(PreLoadBannerActivity.this, "已经展示过了，请退出页面重新进入");
            } else {
                showBannerAd();
            }
        });
        mAdBannerListener = new GMBannerAdListener() {

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                mIsShow = true;
            }

            @Override
            public void onAdShowFail(AdError adError) {
                Log.d(TAG, "onAdShowFail");
            }
        };
        mNativeToBannerListener = new GMNativeToBannerListener() {
            @Override
            public View getGMBannerViewFromNativeAd(GMNativeAdInfo ad) {
                // 1、根据GMNativeAd提供的素材，创建view
                // 2、调用ad.registerView函数进行注册
                // 3、返回view
                return super.getGMBannerViewFromNativeAd(ad);
            }
        };
    }

    private void showBannerAd() {
        if (mPreLoadBannerManager != null) {
            mBannerContainer.removeAllViews();
            if (mPreLoadBannerManager.getBannerAd() != null) {
                View view = mPreLoadBannerManager.getBannerAd().getBannerView();
                if (view != null) {
                    mBannerContainer.addView(view);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreLoadBannerManager != null) {
            mPreLoadBannerManager.destroy();
        }
    }
}
