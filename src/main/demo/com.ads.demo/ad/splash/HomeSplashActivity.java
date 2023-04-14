package com.ads.demo.ad.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ads.demo.AppConst;
import com.ads.demo.MainActivity;
import com.ads.demo.splash.SplashCardManager;
import com.ads.demo.splash.SplashMinWindowManager;
import com.ads.demo.util.SplashUtils;
import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMNetworkPlatformConst;
import com.bytedance.msdk.api.v2.GMNetworkRequestInfo;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashMinWindowListener;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.header.app.untext.R;

import java.lang.ref.SoftReference;

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
 * splash广告使用示例
 * 本实例代码包含 开屏小窗、开屏卡片 等复杂功能，如果未使用这些复杂功能可参考简单版SplashActivity。
 * 使用开屏小窗、开屏卡片功能时，可直接使用SplashCardManager和SplashMinWindowManager类。
 *
 * 使用步骤：
 * 1. 创建开屏广告加载监听器和展示监听器
 * 2. 构造GMAdSlotSplash，进行广告加载，参考下面 loadSplashAd 方法
 * 3. 在广告加载成功后，对开屏小窗、开屏卡片进行相关设置，然后进行广告展示，参考下面 onSplashAdLoadSuccess 方法
 * 4. 在开屏卡片关闭时或者开屏小窗展示回调里跳转到App的主页面。
 */
public class HomeSplashActivity extends Activity {
    private static final String TAG = AppConst.TAG_PRE + HomeSplashActivity.class.getSimpleName();
    private GMSplashAd mSplashAd;
    private FrameLayout mSplashContainer;
    private GMSplashAdLoadCallback mSplashAdLoadCallback;
    private GMSplashAdListener mSplashAdListener;

    //是否强制跳转到主页面
    private boolean mForceGoMain;
    private String mAdUnitId = null;

    // 百度开屏广告点击跳转落地页后倒计时不暂停，即使在看落地页，倒计时结束后仍然会强制跳转，需要特殊处理：
    // 检测到广告被点击，且走了activity的onPaused证明跳转到了落地页，这时候onAdDismiss回调中不进行跳转，而是在activity的onResume中跳转。
    private boolean isBaiduSplashAd = false;
    private boolean baiduSplashAdClicked = false;
    private boolean onPaused = false;

    //----------------开屏小窗参数-------------------
    private boolean showInCurrent = false; //开屏小窗是否在当前页面展示
    private SplashMinWindowListener mSplashMinWindowListener; //开屏小窗相关监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashContainer = findViewById(R.id.splash_container);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mAdUnitId = getResources().getString(R.string.splash_unit_id);
        initListener();
        //加载开屏广告
        mSplashContainer.post(new Runnable() {
            @Override
            public void run() {
                loadSplashAd();
            }
        });
    }

    // 初始化加载和展示监听器
    private void initListener() {
        mSplashAdLoadCallback = new GMSplashAdLoadCallback() {
            @Override
            public void onSplashAdLoadFail(AdError adError) {
                Log.d(TAG, adError.message);
                Log.e(TAG, "load splash ad error : " + adError.code + ", " + adError.message);
                goToMainActivity();

                // 获取本次waterfall加载中，加载失败的adn错误信息。
                if (mSplashAd != null) {
                    Log.d(TAG, "ad load infos: " + mSplashAd.getAdLoadInfoList().toString());
                }
            }

            @Override
            public void onSplashAdLoadSuccess() {
                if (mSplashAd != null) {
                    //初始化卡片开屏相关数据
                    SplashCardManager.getInstance().init(HomeSplashActivity.this, mSplashAd, mSplashContainer, new SplashCardManager.Callback() {
                        @Override
                        public void onSplashCardStart() {
                            // 当动画开始时回调，您可以在此处理渲染卡片背后的界面等操作
                            Log.e(TAG, "onSplashCardStart");
                        }

                        @Override
                        public void onSplashCardClose() {
                            Log.e(TAG, "onSplashCardClose");
                            // 当卡片关闭时回调，您可以在这里处理Activity的关闭操作等
                            Intent intent = new Intent(HomeSplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            if (mSplashContainer != null) {
                                mSplashContainer.removeAllViews();
                            }
                            finish();
                        }

                        @Override
                        public void onSplashCardClick() {
                            Log.e(TAG, "onSplashCardClick");
                        }

                        @Override
                        public void onSplashClickEyeClick() {
                            Log.e(TAG, "onSplashClickEyeClick");
                        }
                    });
                    mSplashAd.showAd(mSplashContainer);
                    initSplashMinWindowData(mSplashAd, mSplashContainer);
                    isBaiduSplashAd = mSplashAd.getAdNetworkPlatformId() == GMNetworkPlatformConst.SDK_NAME_BAIDU;
                    Logger.e(AppConst.TAG, "adNetworkPlatformId: " + mSplashAd.getAdNetworkPlatformId() +
                            "   adNetworkRitId：" + mSplashAd.getAdNetworkRitId() + "   preEcpm: " + mSplashAd.getPreEcpm());
                    // 获取本次waterfall加载中，加载失败的adn错误信息。
                    Log.d(TAG, "ad load infos: " + mSplashAd.getAdLoadInfoList());
                }
                Log.e(TAG, "load splash ad success ");
            }

            // 注意：***** 开屏广告加载超时回调已废弃，统一走onSplashAdLoadFail，GroMore作为聚合不存在SplashTimeout情况。*****
            @Override
            public void onAdLoadTimeout() {
            }
        };
        mSplashAdListener = new GMSplashAdListener() {
            @Override
            public void onAdClicked() {
                baiduSplashAdClicked = true;
                showToast("开屏广告被点击");
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                showToast("开屏广告展示");
                Log.d(TAG, "onAdShow");
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onAdShowFail(AdError adError) {
                showToast("开屏广告展示失败");
                Log.d(TAG, "onAdShowFail");

                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
                loadSplashAd();
            }

            @Override
            public void onAdSkip() {
                showToast("开屏广告点击跳过按钮");
                Log.d(TAG, "onAdSkip");

                goToMainActivity();
            }

            @Override
            public void onAdDismiss() {
                showToast("开屏广告倒计时结束关闭");
                if (isBaiduSplashAd && onPaused && baiduSplashAdClicked) {
                    // 这种情况下，百度开屏广告不能在onAdDismiss中跳转，需要在onResume中跳转主页。
                    return;
                }
                goToMainActivity();
            }
        };
    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        // 设置不支持小窗模式
        SplashMinWindowManager.getInstance().setSupportSplashMinWindow(false);
        if (mAdUnitId == null) return;

        // 注：每次加载开屏广告的时候需要新建一个GMSplashAd，否则可能会出现广告填充问题
        mSplashAd = new GMSplashAd(this, mAdUnitId);
        mSplashAd.setAdSplashListener(mSplashAdListener);

        // 创建开屏广告请求参数AdSlot,具体参数含义参考文档
        GMAdSlotSplash adSlot = new GMAdSlotSplash.Builder()
                .setImageAdSize(UIUtils.getScreenWidth(this), UIUtils.getScreenHeight(this)) // 单位px
                .build();

        // 自定义兜底方案 选择使用
        GMNetworkRequestInfo networkRequestInfo = SplashUtils.getGMNetworkRequestInfo();

        // 请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mSplashAd.loadAd(adSlot, networkRequestInfo, mSplashAdLoadCallback);
    }

    //初始化小窗相关数据
    private void initSplashMinWindowData(GMSplashAd splashAd, ViewGroup splashContainer) {
        if (splashAd == null || splashContainer == null || splashContainer.getChildCount() <= 0) {
            return;
        }
        mSplashMinWindowListener = new SplashMinWindowListener(HomeSplashActivity.this, splashAd, splashContainer.getChildAt(0), showInCurrent);
        splashAd.setMinWindowListener(mSplashMinWindowListener);
    }

    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            goToMainActivity();
        }
        if (isBaiduSplashAd && onPaused && baiduSplashAdClicked) {
            // 这种情况下，百度开屏广告不能在onAdDismiss中跳转，需要自己在onResume中跳转主页。
            goToMainActivity();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPaused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSplashContainer.removeAllViews();
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Log.d(TAG, "goToMainActivity");
        //在当前页播放开屏小窗时不跳主页，当小窗视频播放完成后跳转主页
        if (SplashCardManager.getInstance().canShowInnerActivityCard()) {
            return;
        }
        Log.d(TAG, "SplashMinWindowManager.getInstance().isSupportSplashMinWindow() = " + SplashMinWindowManager.getInstance().isSupportSplashMinWindow());
        if (showInCurrent && SplashMinWindowManager.getInstance().isSupportSplashMinWindow()) {
            return;
        }
        if (mSplashAd != null && mSplashContainer != null && mSplashContainer.getChildCount() > 0) {
            SplashMinWindowManager mSplashMinWindowManager = SplashMinWindowManager.getInstance();
            mSplashMinWindowManager.setSplashInfo(mSplashAd, mSplashContainer.getChildAt(0), getWindow().getDecorView());
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        mSplashContainer.removeAllViews();
        this.finish();
        Log.d(TAG, "finish");
    }

    private void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    /**
     * 开屏广告小窗模式相关监听器
     */
    public class SplashMinWindowListener implements GMSplashMinWindowListener {
        private SoftReference<Activity> mActivity;
        private GMSplashAd mSplashAd; //开屏广告
        private View mSplashView; //开屏广告View
        private boolean mShowInCurrent = false;

        public SplashMinWindowListener(Activity activity, GMSplashAd splashAd, View splashView, boolean showInCurrent) {
            mActivity = new SoftReference<>(activity);
            mSplashAd = splashAd;
            mSplashView = splashView;
            mShowInCurrent = showInCurrent;
        }

        private void finishActivity() {
            if (mActivity.get() == null) {
                return;
            }

            Intent intent = new Intent(mActivity.get().getApplicationContext(), MainActivity.class);
            mActivity.get().startActivity(intent);
            mActivity.get().finish();
        }

        private void startSplashAnimationStart() {
            if (mActivity.get() == null || mSplashAd == null || mSplashView == null) {
                return;
            }
            SplashMinWindowManager splashMinWindowManager = SplashMinWindowManager.getInstance();
            ViewGroup content = mActivity.get().findViewById(android.R.id.content);

            splashMinWindowManager.showMinWindow(mSplashAd, mSplashView, content, content, new SplashMinWindowManager.AnimationCallBack() {
                @Override
                public void animationStart(int animationTime) {
                }

                @Override
                public void animationEnd() {
                    if (mSplashAd != null) {
                        if(TextUtils.equals(mSplashAd.getShowEcpm().getAdNetworkPlatformName(), GMAdConstant.CUSTOM_DATA_KEY_PANGLE)){
                            /**
                             * 接入穿山甲4.7.1.2及以上版本请使用此接口
                             */
                            mSplashAd.showSplashClickEyeView(content);
                        } else {
                            /**
                             * 接入穿山甲4.7.1.2以下版本请使用此接口，其他ADN也请使用此接口
                             */
                            mSplashAd.splashMinWindowAnimationFinish();
                        }
                    }
                }
            });
        }

        /**
         * 该广告支持开屏小窗模式，在这里展示开屏小窗
         */
        @Override
        public void onMinWindowStart() {
            //开始执行开屏小窗动画
            Log.d(TAG, "onMinWindowStart");
            SplashMinWindowManager splashMinWindowManager = SplashMinWindowManager.getInstance();
            splashMinWindowManager.setSupportSplashMinWindow(true);
            if (mShowInCurrent) {
                Log.d(TAG, "onMinWindowStart mShowInCurrent true");
                startSplashAnimationStart();
            } else {
                Log.d(TAG, "onMinWindowStart mShowInCurrent false");
                goToMainActivity();
            }
        }

        /**
         * 开屏小窗播放结束或者手动关闭开屏小窗
         */
        @Override
        public void onMinWindowPlayFinish() {
            //sdk关闭了开屏小窗
            Log.d(TAG, "onMinWindowPlayFinish");
            if (mShowInCurrent) {
                SplashMinWindowManager splashMinWindowManager = SplashMinWindowManager.getInstance();
                splashMinWindowManager.clearSplashStaticData();
                finishActivity();
            }
        }
    }

}
