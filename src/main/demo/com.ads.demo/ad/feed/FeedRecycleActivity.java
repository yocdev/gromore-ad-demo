package com.ads.demo.ad.feed;


import static com.ads.demo.ad.feed.FeedManagerActivity.KEY_AD_UNIT_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMUnifiedNativeAd;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.header.app.untext.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed广告使用示例,使用RecycleView
 * 注：
 * 1. 广告加载 参考下面 loadListAdWithCallback 方法
 * 2. 广告渲染 ： 参考下面RecycleViewAdapter类
 */

/**
 * 注：
 * 1. 通过GMNativeAd.isExpressAd()判断是否是模板广告
 * 2. 自渲染广告：通过GMNativeAd.getImageMode()来判断 大图、小图、组图和视频广告
 * 详见demo
 */
public class FeedRecycleActivity extends Activity {
    private static final String TAG = AppConst.TAG_PRE + "Feed";

    private int LIST_ITEM_COUNT = 10;

    /**
     * 信息流渲染
     */
    private RecycleViewAdapter myAdapter;
    private RecyclerView mRecyclerView;
    private List<GMNativeAd> mDataList;
    private static int sumCount;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    private GMUnifiedNativeAd mTTAdNative; //广告加载对象
    private List<GMNativeAd> mAds = new ArrayList<>(); //广告对象
    private String mAdUnitId;  //广告位id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_recycleview);
        mAdUnitId = getIntent().getStringExtra(KEY_AD_UNIT_ID);
        if (TextUtils.isEmpty(mAdUnitId)) {
            mAdUnitId = getResources().getString(R.string.feed_express_unit_id);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDataList = new ArrayList<>();
        myAdapter = new RecycleViewAdapter(this, mDataList, new RecycleViewAdapter.DislikeCallBack() {
            @Override
            public void dislikeClick() {
                sumCount--;
            }
        });
        mRecyclerView.setAdapter(myAdapter);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 * 加载广告入口
                 */
                loadListAdWithCallback();
            }
        }, 500);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAds != null) {
            for (GMNativeAd ad : mAds) {
                ad.resume();
            }
        }
    }

    /**
     * config回调
     * 需要再onDestroy进行销毁 ，详见onDestroy方法
     */
    @SuppressLint("LongLogTag")
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            Log.e(TAG, "load ad 在config 回调中加载广告");
            loadListAd();
        }
    };

    private void loadListAdWithCallback() {
        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (GMMediationAdSdk.configLoadSuccess()) {
            Log.e(AppConst.TAG, "load ad 当前config配置存在，直接加载广告");
            loadListAd();
        } else {
            Log.e(AppConst.TAG, "load ad 当前config配置不存在，正在请求config配置....");
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不能使用内部类，否则在ondestory中无法移除该回调
        }
    }

    /**
     * 加载feed广告
     */
    private void loadListAd() {
        /**
         * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
         */
        mTTAdNative = new GMUnifiedNativeAd(this, mAdUnitId);

        /**
         * 创建feed广告请求类型参数GMAdSlotNative,更多配置请参考接入文档
         *
         */
        GMAdSlotNative adSlotNative = new GMAdSlotNative.Builder()

                /**
                 * 备注
                 * 1:如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                 * 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                 */
                .setImageAdSize((int) UIUtils.getScreenWidthDp(getApplicationContext()), 340)// 必选参数 单位dp ，详情见上面备注解释
                .setAdCount(3)//请求广告数量为1到3条
                .build();

        /**
         * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
         * (例如：mTTAdNative = new GMUnifiedNativeAd(this, mAdUnitId);）
         */
        mTTAdNative.loadAd(adSlotNative, new GMNativeAdLoadCallback() {
            @Override
            public void onAdLoaded(List<GMNativeAd> ads) {

                if (ads == null || ads.isEmpty()) {
                    Log.e(TAG, "on FeedAdLoaded: ad is null!");
                    return;
                }

                for (GMNativeAd ttNativeAd : ads) {

                    GMAdEcpmInfo gmAdEcpmInfo = ttNativeAd.getShowEcpm();
                    if (gmAdEcpmInfo != null) {
                        Log.e(AppConst.TAG, "   ");
                        Log.e(AppConst.TAG, "adNetworkPlatformName: " + gmAdEcpmInfo.getAdNetworkPlatformName() + "   adNetworkRitId：" + gmAdEcpmInfo.getAdNetworkRitId() + "   preEcpm: " + gmAdEcpmInfo.getPreEcpm());
                    }
                }

                if (mAds != null) {
                    mAds.addAll(ads);
                }
                //总广告数量
                int adCount = ads.size();
                Log.d(TAG, "onAdLoaded feed adCount=" + adCount);
                for (int i = 0; i < LIST_ITEM_COUNT * adCount + 2; i++) {
                    mDataList.add(null);
                }
                //每隔10条放一条广告
                int idx = 0;
                for (int i = 1; i < mDataList.size(); i++) {
                    if (i % 10 == 0 && idx < ads.size()) {
                        if ((i + sumCount - 1) < mDataList.size()) {
                            mDataList.set(i + sumCount - 1, ads.get(idx));
                        }
                        idx++;
                    }
                    if (idx > ads.size()) break;
                }
                //记录容器中的元素数量
                sumCount = mDataList.size();
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdLoadedFail(AdError adError) {
                Log.e(TAG, "load feed ad error : " + adError.code + ", " + adError.message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销config回调
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback);
        if (mAds != null) {
            for (GMNativeAd ad : mAds) {
                ad.destroy();
            }
        }
        mAds = null;
        TToast.reset();
        mHandler.removeCallbacksAndMessages(null);
        sumCount = 0; //静态变量
    }

}
