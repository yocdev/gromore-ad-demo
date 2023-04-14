package com.ads.demo.preload;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.manager.preload.PreLoadInterstitialManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdListener;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdLoadCallback;
import com.bytedance.msdk.api.TToast;
import com.header.app.untext.R;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadInterstitialActivity extends BaseActivity {
    private static final String TAG = AppConst.TAG_PRE + PreLoadInterstitialActivity.class.getSimpleName();

    private TextView mTvAdUnitId;
    private Button mButtonShow;
    private String mAdUnitId;
    private GMInterstitialAdListener interstitialListener;
    private PreLoadInterstitialManager mPreLoadInterstitialManager;
    //是否展示过了
    private boolean mIsShow;
    //是否load失败
    private boolean mIsLoadFail;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload_intersitial_ad);
        mButtonShow = (Button) findViewById(R.id.btn_show);
        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        mAdUnitId = getResources().getString(R.string.interstitial_unit_id);
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        initListener();
        initAdLoader();
    }

    @Override
    public void initListener() {
        interstitialListener = new GMInterstitialAdListener() {

            @Override
            public void onInterstitialShow() {
                mIsShow = false;
                Log.d(TAG, "onInterstitialShow");
            }

            @Override
            public void onInterstitialShowFail(AdError adError) {
                Log.d(TAG, "onInterstitialShowFail");
            }

            @Override
            public void onInterstitialAdClick() {
                Log.d(TAG, "onInterstitialAdClick");
            }

            @Override
            public void onInterstitialClosed() {
                Log.d(TAG, "onInterstitialClosed");
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
            }
        };
        mButtonShow.setOnClickListener(v -> {
            if (mIsLoadFail) {
                TToast.show(PreLoadInterstitialActivity.this, "预缓存失败，请退出页面重新进入");
            } else if (mIsShow) {
                TToast.show(PreLoadInterstitialActivity.this, "已经展示过了，请退出页面重新进入");
            }  else {
                show();
            }
        });
    }

    private void show() {
        if (mPreLoadInterstitialManager != null) {
            if(mPreLoadInterstitialManager.isReady()){
                mPreLoadInterstitialManager.show(this,interstitialListener);
            }
        }
    }

    @Override
    public void initAdLoader() {
        mPreLoadInterstitialManager = new PreLoadInterstitialManager(this,mAdUnitId, new GMInterstitialAdLoadCallback() {
            @Override
            public void onInterstitialLoadFail(AdError adError) {
                mIsLoadFail = true;
                TToast.show(PreLoadInterstitialActivity.this, "广告加载失败");
                Log.e(TAG, "load interaction ad error : " + adError.code + ", " + adError.message);
            }

            @Override
            public void onInterstitialLoad() {
                TToast.show(PreLoadInterstitialActivity.this, "广告加载成功");
                Log.e(TAG, "load interaction ad success ! ");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreLoadInterstitialManager != null) {
            mPreLoadInterstitialManager.destroy();
        }
    }
}
