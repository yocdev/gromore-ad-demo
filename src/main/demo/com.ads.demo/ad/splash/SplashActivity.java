package com.ads.demo.ad.splash;

import static com.ads.demo.ad.splash.SplashMainActivity.KEY_AD_UNIT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.util.SplashUtils;
import com.bytedance.msdk.adapter.util.UIUtils;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.v2.GMNetworkRequestInfo;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdLoadCallback;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.header.app.untext.R;

/**
 * GroMore预加载功能，详细使用请参考接入文档1.8节。
 *
 * 注意：预加载时设置AdSlot参数要与正常加载时一样，比如静音等参数，否则可能出现广告播放行为不符合预期。
 *
 * 预加载说明：GroMore内部会根据开发者传入的广告位信息，并行数，时间间隔进行预请求，期间会产生较大的网络负载，因此建议开发者
 * 根据自己的情况进行预加载：
 * 1、如果app接入了开屏广告，建议在开屏广告展示结束后再触发预加载，以免增加开屏的加载耗时；
 * 2、如果没有接入开屏，则在MainActivity里进行预加载。
 */

/**
 * splash广告使用示例。这个是简单版的开屏使用实例，如果需要使用开屏卡片、开屏小窗等复杂功能，请参考HomeSplashActivity。
 * <p>
 * 1. 广告加载 ： 参考下面 loadSplashAd 方法
 * 2. 广告展示 ： 广告加载成功回调里进行展示
 * 3. 更多功能请参考接入文档
 */
public class SplashActivity extends BaseActivity {
    public static final String EXTRA_FORCE_LOAD_BOTTOM = "extra_force_load_bottom";

    private static final String TAG = AppConst.TAG_PRE + SplashActivity.class.getSimpleName();

    private FrameLayout mSplashContainer;
    private String mAdUnitId = "";
    private GMSplashAd mSplashAd;
    private GMSplashAdLoadCallback mGMSplashAdLoadCallback;
    private GMSplashAdListener mSplashAdListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashContainer = findViewById(R.id.splash_container);

        mAdUnitId = getIntent().getStringExtra(KEY_AD_UNIT_ID);
        if (TextUtils.isEmpty(mAdUnitId)) {
            mAdUnitId = getResources().getString(R.string.splash_unit_id);
        }

        initListener();
        initAdLoader();

        loadSplashAd();
    }

    @Override
    public void initListener() {
        mGMSplashAdLoadCallback = new GMSplashAdLoadCallback() {
            @Override
            public void onSplashAdLoadFail(AdError adError) {
                TToast.show(SplashActivity.this, "广告加载失败");
                Log.e(TAG, "load splash ad error : " + adError.code + ", " + adError.message);
                goToMainActivity();
            }

            @Override
            public void onSplashAdLoadSuccess() {
                TToast.show(SplashActivity.this, "广告加载成功");
                Log.e(TAG, "load splash ad success ");
                mSplashAd.showAd(mSplashContainer);
            }

            // 注意：***** 开屏广告加载超时回调已废弃，统一走onSplashAdLoadFail，GroMore作为聚合不存在SplashTimeout情况。*****
            @Override
            public void onAdLoadTimeout() {
            }
        };
        mSplashAdListener = new GMSplashAdListener() {
            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onAdShowFail(AdError adError) {
                Log.d(TAG, "onAdShowFail");
                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
            }

            @Override
            public void onAdSkip() {
                Log.d(TAG, "onAdSkip");
                goToMainActivity();
            }

            @Override
            public void onAdDismiss() {
                Log.d(TAG, "onAdDismiss");
                goToMainActivity();
            }
        };
    }

    @Override
    public void initAdLoader() {
    }

    /**
     * 加载开屏广告
     */
    public void loadSplashAd() {
        // 注意：每次加载广告，都需要新new一个GMBannerAd对象进行加载
        mSplashAd = new GMSplashAd(this, mAdUnitId);
        mSplashAd.setAdSplashListener(mSplashAdListener);

        // 创建Reward广告请求类型参数GMAdSlotSplash，更多参数参考文档
        GMAdSlotSplash adSlot = new GMAdSlotSplash.Builder()
                .setImageAdSize(UIUtils.getScreenWidth(this), UIUtils.getScreenHeight(this)) // 单位px
                .build();

        //自定义兜底方案 选择使用
        GMNetworkRequestInfo networkRequestInfo = SplashUtils.getGMNetworkRequestInfo();

        mSplashAd.loadAd(adSlot, networkRequestInfo, mGMSplashAdLoadCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSplashAd != null) {
            mSplashAd.destroy();
        }
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, SplashMainActivity.class);
        startActivity(intent);
        mSplashContainer.removeAllViews();
        this.finish();
    }

}
