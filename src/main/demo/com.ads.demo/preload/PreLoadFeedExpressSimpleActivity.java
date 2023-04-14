package com.ads.demo.preload;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.manager.preload.PreLoadFeedManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeExpressAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMVideoListener;
import com.header.app.untext.R;

import java.util.List;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadFeedExpressSimpleActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE + PreLoadFeedExpressSimpleActivity.class.getSimpleName();
    private TextView mTvAdlId; //原生广告位id
    private Button mBtShowFeed;//展示原生广告
    private FrameLayout mFeedContainer; //展示原生广告的容器

    private String mAdUnitId = "945493687";
    private int mStyleType = GMAdConstant.TYPE_EXPRESS_AD;
    private PreLoadFeedManager mPreLoadFeedManager;
    private Context mContext;
    //是否展示过了
    private boolean mIsShow;
    //是否load失败
    private boolean mIsLoadFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload_feed_simple);
        mContext = this;
        mTvAdlId = findViewById(R.id.tv_ad_unit_id); //原生广告位id
        mBtShowFeed = findViewById(R.id.bt_show_feed);//展示原生广告
        mFeedContainer = findViewById(R.id.feed_container);  //展示原生广告的容器
        mTvAdlId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        initListener();
        initAdLoader();
    }

    @Override
    public void initListener() {
        mBtShowFeed.setOnClickListener(this);
    }

    @Override
    public void initAdLoader() {
        mPreLoadFeedManager = new PreLoadFeedManager(this, mAdUnitId, 1, mStyleType, new GMNativeAdLoadCallback() {
            @Override
            public void onAdLoaded(List<GMNativeAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    mIsLoadFail = true;
                    Log.e(TAG, "on FeedAdLoaded: ad is null!");
                    TToast.show(mContext, "广告加载失败！");
                    return;
                }
                TToast.show(mContext, "广告加载成功！");
            }

            @Override
            public void onAdLoadedFail(AdError adError) {
                mIsLoadFail = true;
                TToast.show(mContext, "广告加载失败！");
                Log.e(TAG, "load feed ad error : " + adError.code + ", " + adError.message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreLoadFeedManager != null && mPreLoadFeedManager.getGMNativeAd() != null) {
            mPreLoadFeedManager.getGMNativeAd().resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreLoadFeedManager != null) {
            mPreLoadFeedManager.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_show_feed:
                if (mIsLoadFail) {
                    TToast.show(PreLoadFeedExpressSimpleActivity.this, "预缓存失败，请退出页面重新进入");
                } else if (mIsShow) {
                    TToast.show(PreLoadFeedExpressSimpleActivity.this, "已经展示过了，请退出页面重新进入");
                } else {
                    showAd();
                }
                break;
        }
    }


    /**
     * 展示原生广告
     */
    private void showAd() {
        if (mPreLoadFeedManager != null) {
            View view = null;
            GMNativeAd nativeAd = mPreLoadFeedManager.getGMNativeAd();
            if (nativeAd != null) {
                if (nativeAd.isExpressAd()) { //模板
                    view = getExpressAdView(mFeedContainer, nativeAd);
                } else {
                    TToast.show(mContext, "加载广告样式错误");
                }

                if (view != null) {
                    view.setLayoutParams(new
                            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    mFeedContainer.removeAllViews();
                    mFeedContainer.addView(view);
                }
            }
        }
    }

    //渲染模板广告
    @SuppressWarnings("RedundantCast")
    private View getExpressAdView(ViewGroup parent, @NonNull final GMNativeAd ad) {
        final ExpressAdViewHolder adViewHolder;
        View convertView = null;
        try {
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listitem_ad_native_express, parent, false);
            adViewHolder = new ExpressAdViewHolder();
            adViewHolder.mAdContainerView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_express);
            convertView.setTag(adViewHolder);

            //判断是否存在dislike按钮
            if (ad.hasDislike()) {
                ad.setDislikeCallback((Activity) mContext, new GMDislikeCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        TToast.show(mContext, "点击 " + value);
                        //用户选择不喜欢原因后，移除广告展示
                        removeAdView();
                    }

                    @Override
                    public void onCancel() {
                        TToast.show(mContext, "dislike 点击了取消");
                        Log.d(TAG, "dislike 点击了取消");
                    }

                    /**
                     * 拒绝再次提交
                     */
                    @Override
                    public void onRefuse() {

                    }

                    @Override
                    public void onShow() {
                    }
                });
            }

            //设置点击展示回调监听
            ad.setNativeAdListener(new GMNativeExpressAdListener() {
                @Override
                public void onAdClick() {
                    Log.d(TAG, "onAdClick");
                    TToast.show(mContext, "模板广告被点击");
                }

                @Override
                public void onAdShow() {
                    mIsShow = true;
                    Log.d(TAG, "onAdShow");
                    TToast.show(mContext, "模板广告show");

                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
                    TToast.show(mContext, "模板广告渲染失败code=" + code + ",msg=" + msg);
                    Log.d(TAG, "onRenderFail   code=" + code + ",msg=" + msg);

                }

                // ** 注意点 ** 不要在广告加载成功回调里进行广告view展示，要在onRenderSucces进行广告view展示，否则会导致广告无法展示。
                @Override
                public void onRenderSuccess(float width, float height) {
                    Log.d(TAG, "onRenderSuccess");
                    TToast.show(mContext, "模板广告渲染成功:width=" + width + ",height=" + height);
                    //回调渲染成功后将模板布局添加的父View中
                    if (adViewHolder.mAdContainerView != null) {
                        //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                        int sWidth;
                        int sHeight;
                        /**
                         * 如果存在父布局，需要先从父布局中移除
                         */
                        final View video = ad.getExpressView(); // 获取广告view  如果存在父布局，需要先从父布局中移除
                        if (width == GMAdSize.FULL_WIDTH && height == GMAdSize.AUTO_HEIGHT) {
                            sWidth = FrameLayout.LayoutParams.MATCH_PARENT;
                            sHeight = FrameLayout.LayoutParams.WRAP_CONTENT;
                        } else {
                            sWidth = UIUtils.getScreenWidth(mContext);
                            sHeight = (int) ((sWidth * height) / width);
                        }
                        if (video != null) {
                            /**
                             * 如果存在父布局，需要先从父布局中移除
                             */
                            UIUtils.removeFromParent(video);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sWidth, sHeight);
                            adViewHolder.mAdContainerView.removeAllViews();
                            adViewHolder.mAdContainerView.addView(video, layoutParams);
                        }
                    }
                }
            });


            //视频广告设置播放状态回调（可选）
            ad.setVideoListener(new GMVideoListener() {

                @Override
                public void onVideoStart() {
                    TToast.show(mContext, "模板广告视频开始播放");
                    Log.d(TAG, "onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    TToast.show(mContext, "模板广告视频暂停");
                    Log.d(TAG, "onVideoPause");

                }

                @Override
                public void onVideoResume() {
                    TToast.show(mContext, "模板广告视频继续播放");
                    Log.d(TAG, "onVideoResume");

                }

                @Override
                public void onVideoCompleted() {
                    TToast.show(mContext, "模板播放完成");
                    Log.d(TAG, "onVideoCompleted");
                }

                @Override
                public void onVideoError(AdError adError) {
                    TToast.show(mContext, "模板广告视频播放出错");
                    Log.d(TAG, "onVideoError");
                }

                @Override
                public void onProgressUpdate(long current, long duration) {
                    TToast.show(mContext, "模板广告视频播放进度");
//                    Log.d(TAG, "onProgressUpdate");
                }
            });

            ad.render();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static class ExpressAdViewHolder {
        FrameLayout mAdContainerView;
    }

    private void removeAdView() {
        if (mFeedContainer != null) {
            mFeedContainer.removeAllViews();
        }
    }

}
