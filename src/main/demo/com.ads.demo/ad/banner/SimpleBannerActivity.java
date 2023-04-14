package com.ads.demo.ad.banner;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;
import com.header.app.untext.R;

/**
 * banner广告使用示例。本示例代码为简单版，如果需要使用banner混出信息流功能，请参考BannerActivity。
 * <p>
 * 1. 广告加载 ： 参考下面 loadBannerAdWithCallback 方法
 * 2. 广告展示 ： 参考下面 showBannerAd 方法
 * 3. 更多功能请参考接入文档
 */
public class SimpleBannerActivity extends BaseActivity {
    private static final String TAG = AppConst.TAG_PRE + SimpleBannerActivity.class.getSimpleName();
    private FrameLayout mBannerContainer;

    private Button mButtonDownloadShow;
    private Button mButtonDownload;
    private Button mButtonShow;
    private TextView mTvAdUnitId;
    private RadioGroup radioGroup;

    //广告位id
    private String mAdUnitId;
    //广告是否加载成功了
    private boolean mIsLoaded;
    //广告加载成功并展示
    private boolean mIsLoadedAndShow;
    // banner广告
    private GMBannerAd mBannerViewAd;

    // banner广告相关的监听器
    private GMBannerAdLoadCallback mBannerAdLoadCallback;
    private GMBannerAdListener mAdBannerListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        mButtonDownloadShow = findViewById(R.id.btn_download_show);
        mButtonDownload = findViewById(R.id.btn_download);
        mButtonShow = findViewById(R.id.btn_show);
        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        radioGroup = findViewById(R.id.radio_group);
        //放置banner的父容器
        mBannerContainer = findViewById(R.id.banner_container);
        mAdUnitId = getResources().getString(R.string.banner_unit_id);
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        initListener();
        initAdLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBannerViewAd != null) {
            mBannerViewAd.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBannerViewAd != null) {
            mBannerViewAd.onPause();
        }
    }

    /**
     * 清除状态
     */
    private void clearStatus() {
        //重置load标识
        mIsLoaded = false;
        //清空banner父容器
        mBannerContainer.removeAllViews();
    }

    @Override
    public void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_normal) {
                    mAdUnitId = getResources().getString(R.string.banner_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                } else if (checkedId == R.id.radio_bidding) {
                    mAdUnitId = getResources().getString(R.string.banner_bidding_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                }
            }
        });
        mButtonDownloadShow.setOnClickListener(v -> {
            mIsLoadedAndShow = true;
            clearStatus();
            if (mAdBannerListener != null) {
                loadBannerAdWithCallback();
            }
        });
        mButtonDownload.setOnClickListener(v -> {
            mIsLoadedAndShow = false;
            clearStatus();
            if (mAdBannerListener != null) {
                loadBannerAdWithCallback();
            }
        });
        mButtonShow.setOnClickListener(v -> {
            showBannerAd();
        });

        mBannerAdLoadCallback = new GMBannerAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(AdError adError) {
                mIsLoaded = false;
                mBannerContainer.removeAllViews();

                TToast.show(SimpleBannerActivity.this, "广告加载失败");
                Log.e(TAG, "load banner ad error : " + adError.code + ", " + adError.message);
            }

            @Override
            public void onAdLoaded() {
                mIsLoaded = true;
                if (mIsLoadedAndShow) {
                    showBannerAd();
                }

                TToast.show(SimpleBannerActivity.this, "广告加载成功");
                Log.i(TAG, "banner load success ");
            }
        };

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
                if (mBannerContainer != null) {
                    mBannerContainer.removeAllViews();
                }
                if (mBannerViewAd != null) {
                    mBannerViewAd.destroy();
                }
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                mIsLoaded = false;
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onAdShowFail(AdError adError) {
                Log.d(TAG, "onAdShowFail");
                mIsLoaded = false;
            }
        };
    }

    /**
     * 加载banner广告。如果当前已经加载配置成功，直接请求广告，否则注册监听器请求广告。
     */
    private void loadBannerAdWithCallback() {
        if (GMMediationAdSdk.configLoadSuccess()) {
            loadBannerAd();
        } else {
            GMMediationAdSdk.registerConfigCallback(new GMSettingConfigCallback() {
                @Override
                public void configLoad() {
                    loadBannerAd();
                }
            });
        }
    }

    private void loadBannerAd() {
        if (mBannerViewAd != null) {
            mBannerViewAd.destroy();
        }
        // 注意：每次加载广告，都需要新new一个GMBannerAd对象进行加载
        mBannerViewAd = new GMBannerAd(this, mAdUnitId);

        // 设置广告事件监听
        mBannerViewAd.setAdBannerListener(mAdBannerListener);

        // 创建BANNER广告请求类型参数GMAdSlotBanner，更多参数参考文档
        GMAdSlotBanner slotBanner = new GMAdSlotBanner.Builder()
                .setBannerSize(GMAdSize.BANNER_CUSTOME)
                .setImageAdSize(320, 150)// GMAdSize.BANNER_CUSTOME可以调用setImageAdSize设置大小
                .build();

        mBannerViewAd.loadAd(slotBanner, mBannerAdLoadCallback);
    }

    /**
     * 展示广告
     */
    private void showBannerAd() {
        // 加载成功才能展示
        if (mIsLoaded && mBannerViewAd != null) {
            // 在添加banner的View前需要清空父容器
            mBannerContainer.removeAllViews();

            // 在调用getBannerView之前，可以选择使用isReady进行判断，当前是否有可用广告。
            if (!mBannerViewAd.isReady()) {
                TToast.show(this, "广告已经无效，建议重新请求");
                return;
            }

            // 注意：mBannerViewAd.getBannerView()一个广告对象只能调用一次，第二次为null
            View view = mBannerViewAd.getBannerView();
            if (view != null) {
                mBannerContainer.addView(view);
            } else {
                TToast.show(this, "请重新加载广告");
            }
        } else {
            TToast.show(this, "请先加载广告");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerViewAd != null) {
            mBannerViewAd.destroy();
        }
    }

    @Override
    public void initAdLoader() {
    }

}
