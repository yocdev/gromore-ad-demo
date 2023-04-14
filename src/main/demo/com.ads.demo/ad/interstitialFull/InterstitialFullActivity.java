package com.ads.demo.ad.interstitialFull;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAd;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdListener;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitialFull;
import com.header.app.untext.R;


/**
 * 插全屏广告加载展示demo
 * <p>
 * 1. 广告加载 参考下面 loadInterstitialFullAdWithCallback 方法
 * 2. 广告渲染 ： 参考下面 showFullVideo 方法
 */
public class InterstitialFullActivity extends Activity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE;

    private static String mAdUnitId; //插全屏广告位
    private boolean mLoadSuccess; //广告是否加载成功
    private boolean mIsLoadedAndShow;//广告加载成功后是否直接展示

    /**
     * 广告加载对象 每次加载广告的时候需要新建一个 GMInterstitialFullAd，否则可能会出现广告异常问题
     */
    GMInterstitialFullAd mGMInterstitialFullAd;


    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intersitial_full_ad);
        mAdUnitId = getResources().getString(R.string.interstitial_full_unit_id);

        ((TextView) findViewById(R.id.tv_ad_unit_id)).setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        findViewById(R.id.bt_load_inter_full).setOnClickListener(this); //加载广告
        findViewById(R.id.bt_show_inter_full).setOnClickListener(this); //展示广告
        findViewById(R.id.bt_load_show_inter_full).setOnClickListener(this); //加载并展示广告
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_load_inter_full: //加载广告
                mLoadSuccess = false;
                mIsLoadedAndShow = false;
                /**
                 * 插全屏广告加载入口
                 */
                loadInterstitialFullAdWithCallback();
                break;
            case R.id.bt_show_inter_full: //展示广告
                /**
                 * 插全屏广告展示入口
                 */
                showInterstitialFullAd();
                break;
            case R.id.bt_load_show_inter_full://加载并展示广告
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                /**
                 * 插全屏广告加载入口
                 */
                loadInterstitialFullAdWithCallback();
                break;
        }
    }

    /**
     * 加载插全屏广告
     * 注：广告需要再config配置加载成功后进行加载，如果没有config配置会等到加载完config配置后才去请求广告
     */
    public void loadInterstitialFullAdWithCallback() {

        if (GMMediationAdSdk.configLoadSuccess()) {
            loadInterstitialFullAd();
        } else {
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 加载插全屏广告
     */
    private void loadInterstitialFullAd() {
        /**
         * 每次加载广告的时候需要新建一个 GMInterstitialFullAd，否则可能会出现广告异常问题
         */
        mGMInterstitialFullAd = new GMInterstitialFullAd(this, mAdUnitId);

        /**
         * 创建全屏广告请求类型参数GMAdSlotInterstitialFull,更多参数配置请参考接入文档
         */
        GMAdSlotInterstitialFull adSlotInterstitialFull = new GMAdSlotInterstitialFull.Builder()
                .setImageAdSize(600, 600)  //单位dp（插全屏类型下_插屏广告使用）
                .setUserID("user123")//用户id,必传参数 (插全屏类型下_全屏广告使用)
                .setOrientation(GMAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL; (插全屏类型下_全屏广告使用)
                .build();

        mGMInterstitialFullAd.loadAd(adSlotInterstitialFull, new GMInterstitialFullAdLoadCallback() {
            @Override
            public void onInterstitialFullLoadFail(@NonNull AdError adError) {
                Log.e(TAG, "load interaction ad error : " + adError.code + ", " + adError.message);
                mLoadSuccess = false;
            }

            @Override
            public void onInterstitialFullAdLoad() {
                Log.e(TAG, "load interaction ad success ! ");
                mLoadSuccess = true;
            }

            @Override
            public void onInterstitialFullCached() {
                Log.d(TAG, "onFullVideoCached....缓存成功！");
                mLoadSuccess = true;
                if (mIsLoadedAndShow) {
                    /**
                     * 建议在Cached回调中进行广告展示
                     */
                    showInterstitialFullAd();
                }
            }
        });
    }

    /**
     * config配置回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            loadInterstitialFullAd();
        }
    };

    /**
     * 展示广告
     */
    private void showInterstitialFullAd() {
        if (mLoadSuccess && mGMInterstitialFullAd != null && mGMInterstitialFullAd.isReady()) {
            mGMInterstitialFullAd.setAdInterstitialFullListener(new GMInterstitialFullAdListener() {
                @Override
                public void onInterstitialFullShow() {
                    Log.d(TAG, "onInterstitialFullShow");
                }

                /**
                 * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
                 * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
                 * @param adError showFail的具体原因
                 */
                @Override
                public void onInterstitialFullShowFail(@NonNull AdError adError) {
                    Log.d(TAG, "onInterstitialFullShowFail");
                    // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载

                }

                @Override
                public void onInterstitialFullClick() {
                    Log.d(TAG, "onInterstitialFullClick");
                }

                @Override
                public void onInterstitialFullClosed() {
                    Log.d(TAG, "onInterstitialFullClosed");
                }

                @Override
                public void onVideoComplete() {
                    Log.d(TAG, "onVideoComplete");
                }

                @Override
                public void onVideoError() {
                    Log.d(TAG, "onVideoError");
                }

                @Override
                public void onSkippedVideo() {
                    Log.d(TAG, "onSkippedVideo");
                }

                /**
                 * 当广告打开浮层时调用，如打开内置浏览器、内容展示浮层，一般发生在点击之后
                 * 常常在onAdLeftApplication之前调用
                 */
                @Override
                public void onAdOpened() {
                    Log.d(TAG, "onAdOpened");
                }

                @Override
                public void onAdLeftApplication() {
                    Log.d(TAG, "onAdLeftApplication");
                }

                @Override
                public void onRewardVerify(@NonNull RewardItem rewardItem) {
                    Log.d(TAG, "onRewardVerify");
                }
            });

            mGMInterstitialFullAd.showAd(this);
            mLoadSuccess = false;
        } else {
            TToast.show(this, "请先加载广告");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 页面销毁时推荐进行下面操作
         */
        if (mGMInterstitialFullAd != null) {
            mGMInterstitialFullAd.destroy();
        }
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }

}
