package com.ads.demo.preload;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.manager.preload.PreLoadInterstitialFullManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdListener;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;
import com.bytedance.msdk.api.TToast;
import com.header.app.untext.R;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadInterstitialFullActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE;
    private Context mContext;

    private TextView mTvAdUnitId; //插全屏广告位id
    private Button mBtShowInterFull;//展示插全屏广告

    private static final String mAdUnitId = "947440736"; //插全屏广告位
    private PreLoadInterstitialFullManager mPreLoadInterstitialFullManager;

    //是否展示过了
    private boolean mIsShow;
    //是否load失败
    private boolean mIsLoadFail;
    private GMInterstitialFullAdListener mGMInterstitialFullAdListener;


    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload_intersitial_full_ad);
        mContext = this.getBaseContext();
        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        mBtShowInterFull = findViewById(R.id.bt_show_inter_full);

        mTvAdUnitId.setText("插全屏广告位:" + mAdUnitId);
        initListener();
        initAdLoader();
    }

    @Override
    public void initListener() {
        mBtShowInterFull.setOnClickListener(this);
        mGMInterstitialFullAdListener = new GMInterstitialFullAdListener() {
            @Override
            public void onInterstitialFullShow() {
                mIsShow = true;
                Toast.makeText(mContext, "插全屏广告show", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onInterstitialFullShow");
            }

            @Override
            public void onInterstitialFullShowFail(@NonNull AdError adError) {
                Toast.makeText(mContext, "插全屏广告展示失败", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onInterstitialFullShowFail");

            }

            @Override
            public void onInterstitialFullClick() {
                Toast.makeText(mContext, "插全屏广告click", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onInterstitialFullClick");
            }

            @Override
            public void onInterstitialFullClosed() {
                Toast.makeText(mContext, "插全屏广告close", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onInterstitialFullClosed");
            }

            @Override
            public void onVideoComplete() {
                TToast.show(mContext, "插全屏播放完成");
                Log.d(TAG, "onVideoComplete");
            }

            @Override
            public void onVideoError() {
                TToast.show(mContext, "插全屏播放出错");
                Log.d(TAG, "onVideoError");
            }

            @Override
            public void onSkippedVideo() {
                TToast.show(mContext, "插全屏跳过");
                Log.d(TAG, "onSkippedVideo");
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(mContext, "插全屏广告onAdOpened", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(mContext, "插全屏广告onAdLeftApplication", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onRewardVerify(@NonNull RewardItem rewardItem) {
                Log.d(TAG, "onRewardVerify");
                TToast.show(PreLoadInterstitialFullActivity.this, "onRewardVerify！");
            }
        };
    }

    @Override
    public void initAdLoader() {
        mPreLoadInterstitialFullManager = new PreLoadInterstitialFullManager(this,mAdUnitId, new GMInterstitialFullAdLoadCallback() {
            @Override
            public void onInterstitialFullLoadFail(@NonNull AdError adError) {
                mIsLoadFail = true;
                Log.e(TAG, "load interaction ad error : " + adError.code + ", " + adError.message);
            }

            @Override
            public void onInterstitialFullAdLoad() {
                Log.e(TAG, "load interaction ad success ! ");
                TToast.show(mContext, "插全屏加载成功！");
            }

            @Override
            public void onInterstitialFullCached() {
                Log.d(TAG, "onFullVideoCached....缓存成功！");
                TToast.show(mContext, "插全屏缓存成功！");
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_show_inter_full:
                if (mIsLoadFail) {
                    TToast.show(PreLoadInterstitialFullActivity.this, "预缓存失败，请退出页面重新进入");
                } else if (mIsShow) {
                    TToast.show(PreLoadInterstitialFullActivity.this, "已经展示过了，请退出页面重新进入");
                }  else {
                    showInterFullAd();
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreLoadInterstitialFullManager != null) {
            mPreLoadInterstitialFullManager.destroy();
        }
    }

    private void showInterFullAd() {
        if (mPreLoadInterstitialFullManager != null) {
            if (mPreLoadInterstitialFullManager.isReady()) {
                mPreLoadInterstitialFullManager.show(this,mGMInterstitialFullAdListener);
            }
        }
    }
}
