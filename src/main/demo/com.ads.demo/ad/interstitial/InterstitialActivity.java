package com.ads.demo.ad.interstitial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAd;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdListener;
import com.bytedance.msdk.api.v2.ad.interstitial.GMInterstitialAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitial;
import com.header.app.untext.R;

/**
 * 插屏广告加载展示demo
 * <p>
 * 1. 广告加载 参考下面 loadInterstitialAdWithCallback 方法
 * 2. 广告渲染 ： 参考下面 showInterstitial 方法
 */
public class InterstitialActivity extends Activity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE + InterstitialActivity.class.getSimpleName();

    private String mAdUnitId;//插屏广告位ID
    private boolean mLoadSuccess;//是否加载成功
    private boolean mIsLoadedAndShow;//是否加载成功后进行展示

    /**
     * 插屏对应的广告加载对象
     * 每次加载全屏视频广告的时候需要新建一个GMInterstitialAd，否则可能会出现广告填充问题
     */
    private GMInterstitialAd mInterstitialAd;


    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intersitial_ad);
        mAdUnitId = getResources().getString(R.string.interstitial_unit_id);
        ((TextView) findViewById(R.id.tv_ad_unit_id)).setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        ;
        findViewById(R.id.btn_download).setOnClickListener(this); //加载广告
        findViewById(R.id.btn_show).setOnClickListener(this);//展示广告
        findViewById(R.id.btn_download_show).setOnClickListener(this);//加载并展示广告
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                mLoadSuccess = false;
                mIsLoadedAndShow = false;
                /**
                 * 广告加载入口
                 */
                loadInterstitialAdWithCallback();
                break;
            case R.id.btn_show:
                /**
                 * 广告展示入口
                 */
                showInterstitial();
                break;
            case R.id.btn_download_show:
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                /**
                 * 广告加载入口
                 */
                loadInterstitialAdWithCallback();
                break;
        }
    }

    /**
     * 加载插屏广告
     * 注：广告需要再config配置加载成功后进行加载，如果没有config配置会等到加载完config配置后才去请求广告
     */
    public void loadInterstitialAdWithCallback() {
        if (GMMediationAdSdk.configLoadSuccess()) {//当前config配置已经加载成功
            /**
             * 加载广告入口
             */
            loadInterstitialAd();
        } else {
            /**
             * 注册config回调，当config配置加载成功后会触发该回调，广告加载放到该回调中
             */
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback);
        }
    }

    /**
     * 加载插屏广告
     */
    private void loadInterstitialAd() {
        /**
         * 每次加载广告的时候需要新建一个 GMInterstitialAd，否则可能会出现广告异常问题
         */
        mInterstitialAd = new GMInterstitialAd(this, mAdUnitId);

        /**
         * 创建全屏广告请求类型参数GMAdSlotInterstitial,具体参数含义参考文档
         */
        GMAdSlotInterstitial adSlotInterstitial = new GMAdSlotInterstitial.Builder()
                .build();

        mInterstitialAd.loadAd(adSlotInterstitial, new GMInterstitialAdLoadCallback() {
            @Override
            public void onInterstitialLoadFail(AdError adError) {
                Log.e(TAG, "load interaction ad error : " + adError.code + ", " + adError.message);
                mLoadSuccess = false;

            }

            @Override
            public void onInterstitialLoad() {
                Log.e(TAG, "load interaction ad success ! ");
                mLoadSuccess = true;
                if (mIsLoadedAndShow) {
                    /**
                     * 展示插屏广告入口
                     */
                    showInterstitial();
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
            loadInterstitialAd();
        }
    };

    /**
     * 展示插屏广告
     */
    private void showInterstitial() {
        /**
         * 展示广告前需要调用 mInterstitialAd.isReady()来判断广告是否可以展示  true:可以展示 false：不可以展示 ，如果是false 建议重新加载广告
         */
        if (mLoadSuccess && mInterstitialAd != null && mInterstitialAd.isReady()) {
            mInterstitialAd.setAdInterstitialListener(new GMInterstitialAdListener() {
                @Override
                public void onInterstitialShow() {
                    Log.d(TAG, "onInterstitialShow");
                }

                /**
                 * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
                 * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
                 * @param adError showFail的具体原因
                 */
                @Override
                public void onInterstitialShowFail(AdError adError) {
                    Log.d(TAG, "onInterstitialShowFail");
                    // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
                }


                @Override
                public void onInterstitialAdClick() {
                    Log.d(TAG, "onInterstitialAdClick");
                }


                @Override
                public void onInterstitialClosed() {
                    Log.d(TAG, "onInterstitialClosed");
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
            });

            mInterstitialAd.showAd(this);
            mLoadSuccess = false;
        } else {
            TToast.show(InterstitialActivity.this, "请先加载广告");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 页面销毁时推荐进行下面操作
         */
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }
}
