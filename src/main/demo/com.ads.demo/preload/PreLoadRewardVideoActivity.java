
package com.ads.demo.preload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.manager.preload.PreLoadRewardManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.msdk.api.TToast;
import com.header.app.untext.R;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadRewardVideoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE;

    private TextView mTvAdUnitHorizontalId; //横版激励视频id
    private Button mBtShowHorizontalReward;//展示横版激励视频

    private String mAdUnitHorizontalId = "945700410"; //横屏广告位id
    private PreLoadRewardManager mPreLoadRewardManager;

    //是否展示过了
    private boolean mIsShow;
    //是否load失败
    private boolean mIsLoadFail;
    private GMRewardedAdListener mGMRewardedAdListener;
    private GMRewardedAdListener mGMRewardedPlayAgainListener;

    @SuppressLint("CutPasteId")
    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload_reward_video);
        GMMediationAdSdk.requestPermissionIfNecessary(this);
        mTvAdUnitHorizontalId = findViewById(R.id.tv_ad_unit_horizontal_id);
        mBtShowHorizontalReward = findViewById(R.id.bt_show_horizontal_reward);

        mTvAdUnitHorizontalId.setText("横版激励视频广告位:" + mAdUnitHorizontalId);
        initListener();
        initAdLoader();
    }

    @Override
    public void initListener() {
        mBtShowHorizontalReward.setOnClickListener(this);
        mGMRewardedAdListener = new GMRewardedAdListener() {

            public void onRewardedAdShow() {
                mIsShow = true;
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardedAdShow！");
                Log.d(TAG, "onRewardedAdShow");

            }

            @Override
            public void onRewardedAdShowFail(AdError adError) {
                if (adError == null) {
                    return;
                }
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardedAdShowFail！ errCode: " + adError.code + ", errMsg: " + adError.message);
                Log.d(TAG, "onRewardedAdShowFail, errCode: " + adError.code + ", errMsg: " + adError.message);
            }

            @Override
            public void onRewardClick() {
                Log.d(TAG, "onRewardClick");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardClick！");

            }

            public void onRewardedAdClosed() {
                Log.d(TAG, "onRewardedAdClosed");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardedAdClosed！");

            }

            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onVideoComplete！");

            }

            public void onVideoError() {
                Log.d(TAG, "onVideoError");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onVideoError！");

            }

            public void onRewardVerify(RewardItem rewardItem) {
                Log.d(TAG, "onRewardVerify");
                TToast.show(PreLoadRewardVideoActivity.this, "onRewardVerify！");
            }

            @Override
            public void onSkippedVideo() {

            }

        };

        //穿山甲再看一次监听
        mGMRewardedPlayAgainListener = new GMRewardedAdListener() {

            public void onRewardedAdShow() {
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardedAdShow！");
                Log.d(TAG, "onRewardedAdShow---play again");

            }

            @Override
            public void onRewardedAdShowFail(AdError adError) {
                if (adError == null) {
                    return;
                }
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardedAdShowFail！ errCode: " + adError.code + ", errMsg: " + adError.message);
                Log.d(TAG, "onRewardedAdShowFail---play again, errCode: " + adError.code + ", errMsg: " + adError.message);
            }

            @Override
            public void onRewardClick() {
                Log.d(TAG, "onRewardClick---play again");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardClick！");

            }

            public void onRewardedAdClosed() {
                Log.d(TAG, "onRewardedAdClosed---play again");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onRewardedAdClosed！");

            }

            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete---play again");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onVideoComplete！");

            }

            public void onVideoError() {
                Log.d(TAG, "onVideoError---play again");
                TToast.show(PreLoadRewardVideoActivity.this, "激励onVideoError！");
            }

            public void onRewardVerify(RewardItem rewardItem) {
                Log.d(TAG, "onRewardVerify---play again");
                TToast.show(PreLoadRewardVideoActivity.this, "onRewardVerify！");
            }

            @Override
            public void onSkippedVideo() {

            }
        };
    }

    @Override
    public void initAdLoader() {
        mPreLoadRewardManager = new PreLoadRewardManager(this, mAdUnitHorizontalId,GMAdConstant.HORIZONTAL,new GMRewardedAdLoadCallback() {
            @Override
            public void onRewardVideoLoadFail(AdError adError) {
               mIsLoadFail = true;
                Log.e(TAG, "load RewardVideo ad error : " + adError.code + ", " + adError.message);
            }

            @Override
            public void onRewardVideoAdLoad() {
                Log.e(TAG, "load RewardVideo ad success !");
            }

            @Override
            public void onRewardVideoCached() {
                Log.d(TAG, "onRewardVideoCached....缓存成功");
                TToast.show(PreLoadRewardVideoActivity.this, "激励视频素材缓存成功！");
            }
        });
    }

    private void showRewardAd() {
        if (mPreLoadRewardManager != null) {
            if (mPreLoadRewardManager.isReady()) {
                mPreLoadRewardManager.show(this,mGMRewardedAdListener,mGMRewardedPlayAgainListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreLoadRewardManager != null) {
            mPreLoadRewardManager.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_show_horizontal_reward: //展示横版激励视频
                if (mIsLoadFail) {
                    TToast.show(PreLoadRewardVideoActivity.this, "预缓存失败，请退出页面重新进入");
                } else if (mIsShow) {
                    TToast.show(PreLoadRewardVideoActivity.this, "已经展示过了，请退出页面重新进入");
                }  else {
                    showRewardAd();
                }
                break;
        }
    }
}
