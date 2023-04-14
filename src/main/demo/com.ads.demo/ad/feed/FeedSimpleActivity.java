package com.ads.demo.ad.feed;


import static com.ads.demo.ad.feed.FeedManagerActivity.KEY_AD_UNIT_ID;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.ads.demo.AppConst;
import com.bumptech.glide.Glide;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.nativeAd.TTNativeAdAppInfo;
import com.bytedance.msdk.api.nativeAd.TTViewBinder;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.GMAdAppDownloadListener;
import com.bytedance.msdk.api.v2.ad.GMShakeViewListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeCustomVideoReporter;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeExpressAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMUnifiedNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMVideoListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.header.app.untext.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Feed广告使用示例
 * <p>
 * 1. 广告加载 参考下面 loadListAdWithCallback 方法
 * 2. 广告渲染 ： 参考下面 showAd 方法
 */

/**
 * 注：
 * 1. 通过GMNativeAd.isExpressAd()判断是否是模板广告
 * 2. 自渲染广告：通过GMNativeAd.getImageMode()来判断 大图、小图、组图和视频广告
 * 详见demo
 */
public class FeedSimpleActivity extends Activity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE + FeedSimpleActivity.class.getSimpleName();


    private Context mContext;
    private TextView mTvAdlId; //原生广告位id
    private Button mBtLoadFeed; //加载原生广告
    private Button mBtShowFeed;//展示原生广告
    private Button mBtLoadShowFeed;//加载并展示原生广告
    private FrameLayout mFeedContainer; //展示原生广告的容器

    private boolean mLoadSuccess; //是否加载成功
    private boolean mIsLoadedAndShow;//广告加载成功并展示

    private GMUnifiedNativeAd mGMUnifiedNativeAd; //广告加载对象
    private GMNativeAd mGMNativeAd; //原生广告model
    private String mAdUnitId; //横屏广告位id


    //---------------------------------
    private VideoView mVideoView;
    private GMNativeCustomVideoReporter customVideoReporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_simple);
        mAdUnitId = getIntent().getStringExtra(KEY_AD_UNIT_ID);
        if (TextUtils.isEmpty(mAdUnitId)) {
            TToast.show(mContext, "广告位不能为空");
            finish();
            return;
        }
        mContext = this;
        mTvAdlId = findViewById(R.id.tv_ad_unit_id); //原生广告位id
        mBtLoadFeed = findViewById(R.id.bt_load_feed); //加载原生广告
        mBtShowFeed = findViewById(R.id.bt_show_feed);//展示原生广告
        mBtLoadShowFeed = findViewById(R.id.bt_load_show_feed);//加载并展示原生广告
        mFeedContainer = findViewById(R.id.feed_container);  //展示原生广告的容器
        mTvAdlId.setText("原生广告位ID:" + mAdUnitId);

        //--------------注册监听器----------------
        mBtLoadFeed.setOnClickListener(this);
        mBtShowFeed.setOnClickListener(this);
        mBtLoadShowFeed.setOnClickListener(this);
    }

    /**
     * config回调
     * 需要再onDestroy进行销毁 ，详见onDestroy方法
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            Log.e(TAG, "load ad 在config 回调中加载广告");
            loadFeedAd();
        }
    };

    private void loadListAdWithCallback() {
        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (GMMediationAdSdk.configLoadSuccess()) {
            Log.e(AppConst.TAG, "load ad 当前config配置存在，直接加载广告");
            loadFeedAd();
        } else {
            Log.e(AppConst.TAG, "load ad 当前config配置不存在，正在请求config配置....");
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不能使用内部类，否则在ondestory中无法移除该回调
        }
    }

    /**
     * 加载feed广告
     */
    private void loadFeedAd() {
        /**
         * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
         */
        mGMUnifiedNativeAd = new GMUnifiedNativeAd(this, mAdUnitId);//模板视频

        /**
         * 创建feed广告请求类型参数GMAdSlotNative,更多参数含义参考文档
         * 更多配置请参考接入文档
         */
        GMAdSlotNative adSlotNative = new GMAdSlotNative.Builder()

                /**
                 * 注：
                 *  1:如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                 *  2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                 */
                .setImageAdSize((int) UIUtils.getScreenWidthDp(getApplicationContext()), 340)// 必选参数 单位dp ，详情见上面备注解释
                .setAdCount(3)//请求广告数量为1到3条 （优先采用平台配置的数量）
                .build();

        /**
         *
         * 请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
         *
         * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
         * (例如：mTTAdNative = new GMUnifiedNativeAd(this, mAdUnitId);）
         */
        mGMUnifiedNativeAd.loadAd(adSlotNative, new GMNativeAdLoadCallback() {
            @Override
            public void onAdLoaded(List<GMNativeAd> ads) {

                if (ads == null || ads.isEmpty()) {
                    Log.e(TAG, "on FeedAdLoaded: ad is null!");
                    TToast.show(mContext, "广告加载失败！");
                    return;
                }
                mLoadSuccess = true;
                mGMNativeAd = ads.get(0);
                TToast.show(mContext, "广告加载成功！");
                if (mIsLoadedAndShow) {
                    showAd();
                }
            }

            @Override
            public void onAdLoadedFail(AdError adError) {
                TToast.show(mContext, "广告加载失败！");
                Log.e(TAG, "load feed ad error : " + adError.code + ", " + adError.message);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.canPause()) {
            mVideoView.pause();
            if (customVideoReporter != null) {
                customVideoReporter.reportVideoPause(mVideoView.getCurrentPosition());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null && !mVideoView.isPlaying()) {
            mVideoView.start();
            if (customVideoReporter != null) {
                customVideoReporter.reportVideoContinue(mVideoView.getCurrentPosition());
            }
        }
        if (mGMNativeAd != null) {
            mGMNativeAd.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            // 如果视频还在播放就退出，表示视频被打断了，需要上报break
            if (customVideoReporter != null) {
                customVideoReporter.reportVideoBreak(mVideoView.getCurrentPosition());
            }
            mVideoView.stopPlayback();
        }

        if (mGMUnifiedNativeAd != null) {
            mGMUnifiedNativeAd.destroy();
        }
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
        mGMNativeAd = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_load_feed:
                mLoadSuccess = false;
                mIsLoadedAndShow = false;
                removeAdView();

                /**
                 * 广告加载入口
                 */
                loadListAdWithCallback();
                break;
            case R.id.bt_show_feed:
                /**
                 * 广告展示入口
                 */
                showAd();
                break;
            case R.id.bt_load_show_feed:
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                removeAdView();
                /**
                 * 广告加载入口
                 */
                loadListAdWithCallback();
                break;
        }
    }


    /**
     * 展示信息流广告
     */
    private void showAd() {
        if (!mLoadSuccess || mGMNativeAd == null) {
            TToast.show(this, "请先加载广告");
            return;
        }
        if (!mGMNativeAd.isReady()) {
            TToast.show(this, "广告已经无效，请重新请求");
            return;
        }
        mLoadSuccess = false;
        mIsLoadedAndShow = false;


        View view = null;

        //--------------信息流模板广告渲染----------------
        if (mGMNativeAd.isExpressAd()) { //信息模板模板广告渲染
            view = getExpressAdView(mFeedContainer, mGMNativeAd);
        }

        //--------------信息流自渲染广告渲染----------------
        else {
            if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_SMALL_IMG) { //信息流自渲染广告渲染 ：小图广告
                view = getSmallAdView(mFeedContainer, mGMNativeAd);

            } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_LARGE_IMG) { //信息流自渲染广告渲染 ：大图广告
                view = getLargeAdView(mFeedContainer, mGMNativeAd);

            } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_GROUP_IMG) { //信息流自渲染广告渲染 ：组图广告
                view = getGroupAdView(mFeedContainer, mGMNativeAd);

            } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO) { //信息流自渲染广告渲染 ：视频广告
                view = getVideoView(mFeedContainer, mGMNativeAd);

            } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VERTICAL_IMG) { //信息流自渲染广告渲染 ：竖图广告
                view = getVerticalAdView(mFeedContainer, mGMNativeAd);

            } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) { //信息流自渲染广告渲染 ：竖版视频广告
                view = getVideoView(mFeedContainer, mGMNativeAd);
            } else {
                TToast.show(mContext, "图片展示样式错误");
            }

        }


        if (view != null) {
            view.setLayoutParams(new
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mFeedContainer.removeAllViews();
            mFeedContainer.addView(view);
        }
    }

    /**
     * 信息流模板广告渲染
     */
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
//                    Log.d(TAG, "onProgressUpdate  current:"+current + "  duration: "+ duration);
                }
            });

            ad.render();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    /**
     * 信息流自渲染广告渲染 ： 竖图广告
     */
    private View getVerticalAdView(ViewGroup parent, @NonNull final GMNativeAd ad) {
        VerticalAdViewHolder adViewHolder;
        View convertView = null;
        GMViewBinder viewBinder;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_vertical_pic, parent, false);
        adViewHolder = new VerticalAdViewHolder();
        adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
        adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
        adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
        adViewHolder.mVerticalImage = convertView.findViewById(R.id.iv_listitem_image);
        adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
        adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
        adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.btn_listitem_creative);
        adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

        adViewHolder.app_info = convertView.findViewById(R.id.app_info);
        adViewHolder.app_name = convertView.findViewById(R.id.app_name);
        adViewHolder.author_name = convertView.findViewById(R.id.author_name);
        adViewHolder.package_size = convertView.findViewById(R.id.package_size);
        adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
        adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
        adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
        adViewHolder.version_name = convertView.findViewById(R.id.version_name);

        viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_vertical_pic)
                .titleId(R.id.tv_listitem_ad_title)
                .descriptionTextId(R.id.tv_listitem_ad_desc)
                .mainImageId(R.id.iv_listitem_image)
                .iconImageId(R.id.iv_listitem_icon)
                .callToActionId(R.id.btn_listitem_creative)
                .sourceId(R.id.tv_listitem_ad_source)
                .logoLayoutId(R.id.tt_ad_logo)//logoView 建议传入GroupView类型
                .build();
        adViewHolder.viewBinder = viewBinder;
        bindData(convertView, adViewHolder, ad, viewBinder);
        if (ad.getImageUrl() != null) {
            Glide.with(mContext).load(ad.getImageUrl()).into(adViewHolder.mVerticalImage);
        }

        return convertView;
    }


    /**
     * 信息流自渲染广告渲染 ： 视频广告
     */
    @SuppressWarnings("RedundantCast")
    private View getVideoView(ViewGroup parent, @NonNull final GMNativeAd ad) {
        VideoAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        try {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_video, parent, false);
            adViewHolder = new VideoAdViewHolder();
            adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
            adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
            adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
            adViewHolder.videoView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_video);
            // 可以通过GMNativeAd.getVideoWidth()、GMNativeAd.getVideoHeight()来获取视频的尺寸，进行UI调整（如果有需求的话）。
            // 在使用时需要判断返回值，如果返回为0，即表示该adn的广告不支持。目前仅Pangle和ks支持。
//                    int videoWidth = ad.getVideoWidth();
//                    int videoHeight = ad.getVideoHeight();
            adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
            adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
            adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.btn_listitem_creative);
            adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

            adViewHolder.app_info = convertView.findViewById(R.id.app_info);
            adViewHolder.app_name = convertView.findViewById(R.id.app_name);
            adViewHolder.author_name = convertView.findViewById(R.id.author_name);
            adViewHolder.package_size = convertView.findViewById(R.id.package_size);
            adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
            adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
            adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
            adViewHolder.version_name = convertView.findViewById(R.id.version_name);

            //TTViewBinder 是必须类,需要开发者在确定好View之后把Id设置给TTViewBinder类，并在注册事件时传递给SDK
            viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_large_video).
                    titleId(R.id.tv_listitem_ad_title).
                    sourceId(R.id.tv_listitem_ad_source).
                    descriptionTextId(R.id.tv_listitem_ad_desc).
                    mediaViewIdId(R.id.iv_listitem_video).
                    callToActionId(R.id.btn_listitem_creative).
                    logoLayoutId(R.id.tt_ad_logo).//logoView 建议传入GroupView类型
                    iconImageId(R.id.iv_listitem_icon).build();
            adViewHolder.viewBinder = viewBinder;

            //视频广告设置播放状态回调（可选）
            ad.setVideoListener(new GMVideoListener() {

                @Override
                public void onVideoStart() {
                    TToast.show(mContext, "广告视频开始播放");
                    Log.d(TAG, "onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    TToast.show(mContext, "广告视频暂停");
                    Log.d(TAG, "onVideoPause");
                }

                @Override
                public void onVideoResume() {
                    TToast.show(mContext, "广告视频继续播放");
                    Log.d(TAG, "onVideoResume");
                }

                @Override
                public void onVideoCompleted() {
                    TToast.show(mContext, "广告播放完成");
                    Log.d(TAG, "onVideoCompleted");
                }

                @Override
                public void onVideoError(AdError adError) {
                    TToast.show(mContext, "广告视频播放出错");
                    Log.d(TAG, "onVideoError");
                }

                @Override
                public void onProgressUpdate(long current, long duration) {
                    TToast.show(mContext, "广告视频播放进度");
//                    Log.d(TAG, "onProgressUpdate  current:"+current + "  duration: "+ duration);
                }
            });

            //广告下载事件监听，仅下载类广告生效，可选
            ad.setAppDownloadListener(new GMAdAppDownloadListener() {
                @Override
                public void onIdle() {
                    TToast.show(mContext, "未开始下载");
                    Log.d(TAG, "onIdle");
                }

                @Override
                public void onDownloadStarted() {
                    TToast.show(mContext, "开始下载");
                    Log.d(TAG, "onDownloadStarted");
                }

                @Override
                public void onDownloadProgress(long totalBytes, long currBytes, int progress, int adnType) {
                    TToast.show(mContext, "下载中回调");
                    //Log.d(TAG, "onDownloadActive");
                }

                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    TToast.show(mContext, "下载暂停回调");
                    Log.d(TAG, "onDownloadPaused");
                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                    TToast.show(mContext, "下载失败回调");
                    Log.d(TAG, "onDownloadFailed");
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    TToast.show(mContext, "安装完成回调");
                    Log.d(TAG, "onDownloadFinished");
                }

                @Override
                public void onInstalled(String fileName, String appName) {
                    TToast.show(mContext, "下载完成回调");
                    Log.d(TAG, "onInstalled");
                }
            });

            //绑定广告数据、设置交互回调
            bindData(convertView, adViewHolder, ad, viewBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    /**
     * 信息流自渲染广告渲染 ： 大图广告
     */
    @SuppressWarnings("RedundantCast")
    private View getLargeAdView(ViewGroup parent, @NonNull final GMNativeAd ad) {
        final LargeAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_pic, parent, false);
        adViewHolder = new LargeAdViewHolder();
        adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
        adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
        adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
        adViewHolder.mLargeImage = (ImageView) convertView.findViewById(R.id.iv_listitem_image);
        adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
        adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
        adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.btn_listitem_creative);
        adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型
        adViewHolder.shakeViewContainer = convertView.findViewById(R.id.fl_shakeview_container);

        adViewHolder.app_info = convertView.findViewById(R.id.app_info);
        adViewHolder.app_name = convertView.findViewById(R.id.app_name);
        adViewHolder.author_name = convertView.findViewById(R.id.author_name);
        adViewHolder.package_size = convertView.findViewById(R.id.package_size);
        adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
        adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
        adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
        adViewHolder.version_name = convertView.findViewById(R.id.version_name);

        viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_large_pic).
                titleId(R.id.tv_listitem_ad_title).
                descriptionTextId(R.id.tv_listitem_ad_desc).
                sourceId(R.id.tv_listitem_ad_source).
                mainImageId(R.id.iv_listitem_image).
                callToActionId(R.id.btn_listitem_creative).
                logoLayoutId(R.id.tt_ad_logo).//logoView 建议传入GroupView类型
                shakeViewContainerId(R.id.fl_shakeview_container).
                iconImageId(R.id.iv_listitem_icon).build();
        adViewHolder.viewBinder = viewBinder;
        bindData(convertView, adViewHolder, ad, viewBinder);
        if (ad.getImageUrl() != null) {
            Glide.with(mContext).load(ad.getImageUrl()).into(adViewHolder.mLargeImage);
        }
        return convertView;
    }

    /**
     * 信息流自渲染广告渲染 ： 组图广告
     */
    @SuppressWarnings("RedundantCast")
    private View getGroupAdView(ViewGroup parent, @NonNull final GMNativeAd ad) {
        GroupAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_group_pic, parent, false);
        adViewHolder = new GroupAdViewHolder();
        adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
        adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
        adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
        adViewHolder.mGroupImage1 = (ImageView) convertView.findViewById(R.id.iv_listitem_image1);
        adViewHolder.mGroupImage2 = (ImageView) convertView.findViewById(R.id.iv_listitem_image2);
        adViewHolder.mGroupImage3 = (ImageView) convertView.findViewById(R.id.iv_listitem_image3);
        adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
        adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
        adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.btn_listitem_creative);
        adViewHolder.mLogo = convertView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

        adViewHolder.app_info = convertView.findViewById(R.id.app_info);
        adViewHolder.app_name = convertView.findViewById(R.id.app_name);
        adViewHolder.author_name = convertView.findViewById(R.id.author_name);
        adViewHolder.package_size = convertView.findViewById(R.id.package_size);
        adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
        adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
        adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
        adViewHolder.version_name = convertView.findViewById(R.id.version_name);

        viewBinder = new TTViewBinder.Builder(R.layout.listitem_ad_group_pic).
                titleId(R.id.tv_listitem_ad_title).
                descriptionTextId(R.id.tv_listitem_ad_desc).
                sourceId(R.id.tv_listitem_ad_source).
                mainImageId(R.id.iv_listitem_image1).//传第一张即可
                logoLayoutId(R.id.tt_ad_logo).//logoView 建议传入GroupView类型
                callToActionId(R.id.btn_listitem_creative).
                iconImageId(R.id.iv_listitem_icon).
                groupImage1Id(R.id.iv_listitem_image1).
                groupImage2Id(R.id.iv_listitem_image2).
                groupImage3Id(R.id.iv_listitem_image3).
                build();
        adViewHolder.viewBinder = viewBinder;

        bindData(convertView, adViewHolder, ad, viewBinder);
        if (ad.getImageList() != null && ad.getImageList().size() >= 3) {
            String image1 = ad.getImageList().get(0);
            String image2 = ad.getImageList().get(1);
            String image3 = ad.getImageList().get(2);
            if (image1 != null) {
                Glide.with(mContext).load(image1).into(adViewHolder.mGroupImage1);
            }
            if (image2 != null) {
                Glide.with(mContext).load(image2).into(adViewHolder.mGroupImage2);
            }
            if (image3 != null) {
                Glide.with(mContext).load(image3).into(adViewHolder.mGroupImage3);
            }
        }
        return convertView;
    }


    /**
     * 信息流自渲染广告渲染 ： 小图广告
     */
    @SuppressWarnings("RedundantCast")
    private View getSmallAdView(ViewGroup parent, @NonNull final GMNativeAd ad) {
        SmallAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, parent, false);
        adViewHolder = new SmallAdViewHolder();
        adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
        adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
        adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
        adViewHolder.mSmallImage = (ImageView) convertView.findViewById(R.id.iv_listitem_image);
        adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
        adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
        adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.btn_listitem_creative);

        adViewHolder.app_info = convertView.findViewById(R.id.app_info);
        adViewHolder.app_name = convertView.findViewById(R.id.app_name);
        adViewHolder.author_name = convertView.findViewById(R.id.author_name);
        adViewHolder.package_size = convertView.findViewById(R.id.package_size);
        adViewHolder.permissions_url = convertView.findViewById(R.id.permissions_url);
        adViewHolder.permissions_content = convertView.findViewById(R.id.permissions_content);
        adViewHolder.privacy_agreement = convertView.findViewById(R.id.privacy_agreement);
        adViewHolder.version_name = convertView.findViewById(R.id.version_name);

        viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_small_pic).
                titleId(R.id.tv_listitem_ad_title).
                sourceId(R.id.tv_listitem_ad_source).
                descriptionTextId(R.id.tv_listitem_ad_desc).
                mainImageId(R.id.iv_listitem_image).
                logoLayoutId(R.id.tt_ad_logo).//logoView 建议为GroupView 类型
                callToActionId(R.id.btn_listitem_creative).
                iconImageId(R.id.iv_listitem_icon).build();
        adViewHolder.viewBinder = viewBinder;
        bindData(convertView, adViewHolder, ad, viewBinder);
        if (ad.getImageUrl() != null) {
            Glide.with(mContext).load(ad.getImageUrl()).into(adViewHolder.mSmallImage);
        }
        return convertView;
    }


    GMNativeAdListener mTTNativeAdListener = new GMNativeAdListener() {
        @Override
        public void onAdClick() {
            Log.d(TAG, "onAdClick");
            TToast.show(mContext, "自渲染广告被点击");
        }


        @Override
        public void onAdShow() {
            Log.d(TAG, "onAdShow");
            TToast.show(mContext, "广告展示");
        }
    };

    /**
     * 绑定广告
     */
    private void bindData(View convertView, final AdViewHolder adViewHolder, final GMNativeAd ad, GMViewBinder viewBinder) {
        //设置dislike弹窗，如果有
        if (ad.hasDislike()) {
            final GMAdDislike ttAdDislike = ad.getDislikeDialog((Activity) mContext);
            adViewHolder.mDislike.setVisibility(View.VISIBLE);
            adViewHolder.mDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //使用接口来展示
                    ttAdDislike.showDislikeDialog();
                    ttAdDislike.setDislikeCallback(new GMDislikeCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                            TToast.show(mContext, "点击 " + value);
                            //用户选择不喜欢原因后，移除广告展示
                            removeAdView();
                        }

                        @Override
                        public void onCancel() {
                            TToast.show(mContext, "dislike 点击了取消");
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
            });
        } else {
            if (adViewHolder.mDislike != null)
                adViewHolder.mDislike.setVisibility(View.GONE);
        }

        setDownLoadAppInfo(ad, adViewHolder);

        //设置事件回调
        ad.setNativeAdListener(mTTNativeAdListener);
        ad.setGMShakeViewListener(new GMShakeViewListener() {
            @Override
            public void onDismissed() {
                Log.d(TAG, "shake view dismissed");
            }
        });
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(convertView);
        clickViewList.add(adViewHolder.mSource);
        clickViewList.add(adViewHolder.mTitle);
        clickViewList.add(adViewHolder.mDescription);
        clickViewList.add(adViewHolder.mIcon);
        //添加点击区域
        if (adViewHolder instanceof LargeAdViewHolder) {
            clickViewList.add(((LargeAdViewHolder) adViewHolder).mLargeImage);
        } else if (adViewHolder instanceof SmallAdViewHolder) {
            clickViewList.add(((SmallAdViewHolder) adViewHolder).mSmallImage);
        } else if (adViewHolder instanceof VerticalAdViewHolder) {
            clickViewList.add(((VerticalAdViewHolder) adViewHolder).mVerticalImage);
        } else if (adViewHolder instanceof VideoAdViewHolder) {
            clickViewList.add(((VideoAdViewHolder) adViewHolder).videoView);
        } else if (adViewHolder instanceof GroupAdViewHolder) {
            clickViewList.add(((GroupAdViewHolder) adViewHolder).mGroupImage1);
            clickViewList.add(((GroupAdViewHolder) adViewHolder).mGroupImage2);
            clickViewList.add(((GroupAdViewHolder) adViewHolder).mGroupImage3);
        }
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(adViewHolder.mCreativeButton);
        //重要! 这个涉及到广告计费，必须正确调用。**** convertView必须是com.bytedance.msdk.api.format.TTNativeAdView ****
        ad.registerView(this, (ViewGroup) convertView, clickViewList, creativeViewList, viewBinder);

        adViewHolder.mTitle.setText(ad.getTitle()); //title为广告的简单信息提示
        adViewHolder.mDescription.setText(ad.getDescription()); //description为广告的较长的说明
        adViewHolder.mSource.setText(TextUtils.isEmpty(ad.getSource()) ? "广告来源" : ad.getSource());

        String icon = ad.getIconUrl();
        if (icon != null) {
            Glide.with(mContext).load(icon).into(adViewHolder.mIcon);
        }
        Button adCreativeButton = adViewHolder.mCreativeButton;
        switch (ad.getInteractionType()) {
            case GMAdConstant.INTERACTION_TYPE_DOWNLOAD:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText(TextUtils.isEmpty(ad.getActionText()) ? "立即下载" : ad.getActionText());
                break;
            case GMAdConstant.INTERACTION_TYPE_DIAL:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("立即拨打");
                break;
            case GMAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case GMAdConstant.INTERACTION_TYPE_BROWSER:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText(TextUtils.isEmpty(ad.getActionText()) ? "查看详情" : ad.getActionText());
                break;
            default:
                adCreativeButton.setVisibility(View.GONE);
                TToast.show(mContext, "交互类型异常");
        }
    }


    private void setDownLoadAppInfo(GMNativeAd ttNativeAd, AdViewHolder adViewHolder) {
        if (adViewHolder == null) {
            return;
        }
        if (ttNativeAd == null || ttNativeAd.getNativeAdAppInfo() == null) {
            adViewHolder.app_info.setVisibility(View.GONE);
        } else {
            adViewHolder.app_info.setVisibility(View.VISIBLE);
            TTNativeAdAppInfo appInfo = ttNativeAd.getNativeAdAppInfo();
            adViewHolder.app_name.setText("应用名称：" + appInfo.getAppName());
            adViewHolder.author_name.setText("开发者：" + appInfo.getAuthorName());
            adViewHolder.package_size.setText("包大小：" + appInfo.getPackageSizeBytes());
            adViewHolder.permissions_url.setText("权限url:" + appInfo.getPermissionsUrl());
            adViewHolder.privacy_agreement.setText("隐私url：" + appInfo.getPrivacyAgreement());
            adViewHolder.version_name.setText("版本号：" + appInfo.getVersionName());
            adViewHolder.permissions_content.setText("权限内容:" + getPermissionsContent(appInfo.getPermissionsMap()));
        }
    }

    private String getPermissionsContent(Map<String, String> permissionsMap) {
        if (permissionsMap == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> keyList = permissionsMap.keySet();
        for (String s : keyList) {
            stringBuffer.append(s + " : " + permissionsMap.get(s) + " \n");
        }

        return stringBuffer.toString();
    }

    private static class VideoAdViewHolder extends AdViewHolder {
        FrameLayout videoView;
    }

    private static class LargeAdViewHolder extends AdViewHolder {
        ImageView mLargeImage;
    }

    private static class SmallAdViewHolder extends AdViewHolder {
        ImageView mSmallImage;
    }

    private static class VerticalAdViewHolder extends AdViewHolder {
        ImageView mVerticalImage;
    }

    private static class GroupAdViewHolder extends AdViewHolder {
        ImageView mGroupImage1;
        ImageView mGroupImage2;
        ImageView mGroupImage3;
    }

    private static class ExpressAdViewHolder {
        FrameLayout mAdContainerView;
    }

    private static class AdViewHolder {
        GMViewBinder viewBinder;
        ImageView mIcon;
        ImageView mDislike;
        Button mCreativeButton;
        TextView mTitle;
        TextView mDescription;
        TextView mSource;
        RelativeLayout mLogo;

        LinearLayout app_info;
        TextView app_name;
        TextView author_name;
        TextView package_size;
        TextView permissions_url;
        TextView privacy_agreement;
        TextView version_name;
        TextView permissions_content;

        FrameLayout shakeViewContainer;
    }


    private void removeAdView() {
        if (mFeedContainer != null) {
            mFeedContainer.removeAllViews();
        }
    }

}
