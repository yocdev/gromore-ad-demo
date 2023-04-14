package com.ads.demo.ad.fullVideo;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAd;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdListener;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotFullVideo;
import com.header.app.untext.R;


/**
 * 全屏广告加载展示demo
 * <p>
 * 1. 广告加载 参考下面 loadFullVideoAdWithCallback 方法
 * 2. 广告展示 ： 参考下面 showFullVideo 方法
 */
public class FullVideoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE + FullVideoActivity.class.getSimpleName();

    private String mAdUnitId;// 全屏广告位id

    private boolean mLoadSuccess;//是否加载成功
    private boolean mIsLoadedAndShow;//广告加载成功并展示

    /**
     * 广告加载对象 每次加载广告的时候需要新建一个 GMFullVideoAd，否则可能会出现广告异常问题
     */
    private GMFullVideoAd mGMFullVideoAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video_main);
        mAdUnitId = getResources().getString(R.string.full_video_vertical_unit_id);
        ((TextView) findViewById(R.id.tv_ad_unit_id)).setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        findViewById(R.id.bt_load_full_video).setOnClickListener(this); //加载广告
        findViewById(R.id.bt_show_full_video).setOnClickListener(this);//展示广告
        findViewById(R.id.bt_load_show_full_video).setOnClickListener(this);//加载并展示广告
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_load_full_video:
                mLoadSuccess = false;
                mIsLoadedAndShow = false;
                /**
                 * 广告加载入口
                 */
                loadFullVideoAdWithCallback();
                break;
            case R.id.bt_show_full_video:
                /**
                 * 广告展示入口
                 */
                showFullVideo();
                break;
            case R.id.bt_load_show_full_video:
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                /**
                 * 广告加载入口
                 */
                loadFullVideoAdWithCallback();
                break;
        }
    }


    /**
     * 加载全屏广告
     * 注：广告需要再config配置加载成功后进行加载，如果没有config配置会等到加载完config配置后才去请求广告
     */
    public void loadFullVideoAdWithCallback() {
        if (GMMediationAdSdk.configLoadSuccess()) { //当前config配置已经加载成功
            /**
             * 加载广告入口
             */
            loadFullVideoAd();
        } else {
            /**
             * 注册config回调，当config配置加载成功后会触发该回调，广告加载放到该回调中
             */
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }


    /**
     * 加载全屏广告
     */
    private void loadFullVideoAd() {
        /**
         * 每次加载广告的时候需要新建一个 GMFullVideoAd，否则可能会出现广告异常问题
         */
        mGMFullVideoAd = new GMFullVideoAd(this, mAdUnitId);

        /**
         * 创建全屏广告请求类型参数GMAdSlotFullVideo,具体参数含义参考文档
         */
        GMAdSlotFullVideo adSlotFullVideo = new GMAdSlotFullVideo.Builder()
                .setUserID("user123")//用户id,必传参数
                .setOrientation(GMAdConstant.VERTICAL)//必填参数，期望视频的播放方向
                .build();
        mGMFullVideoAd.loadAd(adSlotFullVideo, new GMFullVideoAdLoadCallback() {
            @Override
            public void onFullVideoLoadFail(AdError adError) {
                Log.e(TAG, "onFullVideoLoadFail....全屏加载失败！");
                mLoadSuccess = false;
            }

            @Override
            public void onFullVideoAdLoad() {
                Log.d(TAG, "onFullVideoAdLoad....加载成功！");
                mLoadSuccess = true;
            }

            @Override
            public void onFullVideoCached() {
                Log.d(TAG, "onFullVideoCached....缓存成功！");
                mLoadSuccess = true;
                if (mIsLoadedAndShow) {
                    /**
                     * 建议在Cached回调中进行广告展示
                     */
                    showFullVideo();
                }
            }
        });
    }

    /**
     * config配置加载成功回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            /**
             * 加载广告入口
             */
            loadFullVideoAd();
        }
    };

    /**
     * 展示广告
     */
    private void showFullVideo() {
        /**
         * 展示广告前需要调用 mGMFullVideoAd.isReady()来判断广告是否可以展示  true:可以展示 false：不可以展示 ，如果是false 建议重新加载广告
         */
        if (mLoadSuccess && mGMFullVideoAd != null && mGMFullVideoAd.isReady()) {
            mGMFullVideoAd.setFullVideoAdListener(new GMFullVideoAdListener() {
                @Override
                public void onFullVideoAdShow() {
                    Log.d(TAG, "onFullVideoAdShow");
                }

                /**
                 * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
                 * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
                 * @param adError showFail的具体原因
                 */
                @Override
                public void onFullVideoAdShowFail(AdError adError) {
                    Log.d(TAG, "onFullVideoAdShowFail");
                    // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
                }

                @Override
                public void onFullVideoAdClick() {
                    Log.d(TAG, "onFullVideoAdClick");
                }

                @Override
                public void onFullVideoAdClosed() {
                    Log.d(TAG, "onFullVideoAdClosed");
                }

                @Override
                public void onVideoComplete() {
                    Log.d(TAG, "onVideoComplete");
                }

                /**
                 * 1、视频播放失败的回调 - Mintegral GDT Admob广告不存在该回调；
                 */
                @Override
                public void onVideoError() {
                    Log.d(TAG, "onVideoError");
                }

                @Override
                public void onSkippedVideo() {
                    Log.d(TAG, "onSkippedVideo");
                }

                @Override
                public void onRewardVerify(@NonNull RewardItem rewardItem) {
                    Log.d(TAG, "onRewardVerify");
                }
            });
            mGMFullVideoAd.showFullAd(this);
            mLoadSuccess = false;
        } else {
            TToast.show(FullVideoActivity.this, "请先加载广告");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 页面销毁时推荐进行下面操作
         */
        if (mGMFullVideoAd != null) {
            mGMFullVideoAd.destroy();
        }
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }
}
