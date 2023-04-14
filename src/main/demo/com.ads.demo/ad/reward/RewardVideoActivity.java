package com.ads.demo.ad.reward;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.header.app.untext.R;

/**
 * 激励视频广告使用示例。
 * <p>
 * 1. 广告加载 ： 参考下面 loadRewardAdWithCallback 方法
 * 2. 广告展示 ： 参考下面 showAd 方法
 * 3. 更多功能请参考接入文档
 */
public class RewardVideoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE;

    private TextView mTvAdUnitId; //激励视频id
    private Button mBtLoadReward; //加载激励视频
    private Button mBtShowReward;//展示激励视频
    private Button mBtLoadShowReward;//加载并展示激励视频
    private RadioGroup radioGroup;

    private String mAdUnitId; //横屏广告位id
    private GMRewardAd mRewardAd;

    private boolean mLoadSuccess; //是否加载成功
    private boolean mIsLoadedAndShow;//广告加载成功并展示
    private GMRewardedAdLoadCallback mGMRewardedAdLoadCallback;
    private GMRewardedAdListener mGMRewardedAdListener;

    private int orientation = GMAdConstant.HORIZONTAL;

    @SuppressLint("CutPasteId")
    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        GMMediationAdSdk.requestPermissionIfNecessary(this);
        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        mBtLoadReward = findViewById(R.id.bt_load_reward);
        mBtShowReward = findViewById(R.id.bt_show_reward);
        mBtLoadShowReward = findViewById(R.id.bt_load_show_reward);
        radioGroup = findViewById(R.id.radio_group);
        mAdUnitId = getResources().getString(R.string.reward_horizontal_unit_id); //横屏广告位id
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        initListener();
        initAdLoader();
    }

    @Override
    public void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_horizontal) {
                    orientation = GMAdConstant.HORIZONTAL;
                    mAdUnitId = getResources().getString(R.string.reward_horizontal_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                } else if (checkedId == R.id.radio_vertical) {
                    orientation = GMAdConstant.VERTICAL;
                    mAdUnitId = getResources().getString(R.string.reward_vertical_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                } else if (checkedId == R.id.radio_bidding) {
                    orientation = GMAdConstant.HORIZONTAL;
                    mAdUnitId = getResources().getString(R.string.reward_bidding_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                }
            }
        });
        mBtLoadReward.setOnClickListener(this);
        mBtShowReward.setOnClickListener(this);
        mBtLoadShowReward.setOnClickListener(this);
        mGMRewardedAdLoadCallback = new GMRewardedAdLoadCallback() {
            @Override
            public void onRewardVideoLoadFail(AdError adError) {
                mLoadSuccess = false;
                Log.e(TAG, "load RewardVideo ad error : " + adError.code + ", " + adError.message);
            }

            @Override
            public void onRewardVideoAdLoad() {
                mLoadSuccess = true;
                Log.e(TAG, "load RewardVideo ad success !");
            }

            @Override
            public void onRewardVideoCached() {
                mLoadSuccess = true;
                Log.d(TAG, "onRewardVideoCached....缓存成功");
                TToast.show(RewardVideoActivity.this, "激励视频素材缓存成功！");
                if (mIsLoadedAndShow) { //加载并展示
                    showRewardAd();
                }
            }
        };
        mGMRewardedAdListener = new GMRewardedAdListener() {
            /**
             * 广告的展示回调 每个广告仅回调一次
             */
            public void onRewardedAdShow() {
                TToast.show(RewardVideoActivity.this, "激励onRewardedAdShow！");
                Log.d(TAG, "onRewardedAdShow");
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onRewardedAdShowFail(AdError adError) {
                if (adError == null) {
                    return;
                }
                TToast.show(RewardVideoActivity.this, "激励onRewardedAdShowFail！ errCode: " + adError.code + ", errMsg: " + adError.message);
                Log.d(TAG, "onRewardedAdShowFail, errCode: " + adError.code + ", errMsg: " + adError.message);
                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
            }

            /**
             * 注意Admob的激励视频不会回调该方法
             */
            @Override
            public void onRewardClick() {
                Log.d(TAG, "onRewardClick");
                TToast.show(RewardVideoActivity.this, "激励onRewardClick！");
            }

            /**
             * 广告关闭的回调
             */
            public void onRewardedAdClosed() {
                Log.d(TAG, "onRewardedAdClosed");
                TToast.show(RewardVideoActivity.this, "激励onRewardedAdClosed！");
            }

            /**
             * 视频播放完毕的回调 Admob广告不存在该回调
             */
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete");
                TToast.show(RewardVideoActivity.this, "激励onVideoComplete！");
            }

            /**
             * 视频播放失败的回调
             */
            public void onVideoError() {
                Log.d(TAG, "onVideoError");
                TToast.show(RewardVideoActivity.this, "激励onVideoError！");
            }

            /**
             * 激励视频播放完毕，验证是否有效发放奖励的回调
             */
            public void onRewardVerify(RewardItem rewardItem) {
                Log.d(TAG, "onRewardVerify");
                TToast.show(RewardVideoActivity.this, "onRewardVerify！");
                // 这里是未开启gromore服务端奖励验证的逻辑，如果开启了Gromore服务端奖励验证，请参考GMRewardUtils.processRewardVerify()。
                if (rewardItem != null) {
                    // 根据isRewardVerify来判断是否进行奖励发放
                    boolean isRewardVerify = rewardItem.rewardVerify();
                    Log.d(TAG, "onRewardVerify rewardItem isRewardVerify: " + isRewardVerify);
                }
            }

            /**
             * Mintegral GDT Admob广告不存在该回调
             */
            @Override
            public void onSkippedVideo() {
                Log.d(TAG, "onSkippedVideo");
                TToast.show(RewardVideoActivity.this, "激励onSkippedVideo");
            }
        };
    }

    @Override
    public void initAdLoader() {
    }

    /**
     * 加载激励广告。如果当前已经加载配置成功，直接请求广告，否则注册监听器请求广告。
     */
    private void loadRewardAdWithCallback() {
        if (GMMediationAdSdk.configLoadSuccess()) {
            loadRewardAd();
        } else {
            GMMediationAdSdk.registerConfigCallback(new GMSettingConfigCallback() {
                @Override
                public void configLoad() {
                    loadRewardAd();
                }
            });
        }
    }

    private void loadRewardAd() {
        // 注意：每次加载广告，都需要新new一个GMBannerAd对象进行加载
        mRewardAd = new GMRewardAd(this, mAdUnitId);

        // 激励视频服务端验证功能，请求时可传入customData数据透传给各家ADN，详情参考RewardUtils类。

        // 创建Reward广告请求类型参数GMAdSlotReward，更多参数参考文档
        GMAdSlotRewardVideo adSlotRewardVideo = new GMAdSlotRewardVideo.Builder()
                .setUserID("user123")//用户id,必传参数
                .setOrientation(orientation)//必填参数，期望视频的播放方向：GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
                .build();

        mRewardAd.loadAd(adSlotRewardVideo, mGMRewardedAdLoadCallback);
    }

    /**
     * 展示广告
     */
    private void showRewardAd() {
        if (mLoadSuccess && mRewardAd != null) {
            if (mRewardAd.isReady()) {
                //在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                //展示广告，并传入广告展示的场景
                mRewardAd.setRewardAdListener(mGMRewardedAdListener);

                // 激励视频再看一个功能。参数跟普通监听器的是一样的。
                // mRewardAd.setRewardPlayAgainListener(RewardUtils.getRewardPlayAgainListener());

                mRewardAd.showRewardAd(this);
                mLoadSuccess = false;
            } else {
                TToast.show(this, "当前广告不满足show的条件");
            }
        } else {
            TToast.show(this, "请先加载广告");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRewardAd != null) {
            mRewardAd.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_load_reward: //加载激励视频
                mLoadSuccess = false;
                mIsLoadedAndShow = false;
                loadRewardAdWithCallback();
                break;
            case R.id.bt_show_reward: //展示激励视频
                showRewardAd();
                break;
            case R.id.bt_load_show_reward://加载并展示激励视频
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                loadRewardAdWithCallback();
                break;
        }
    }
}
