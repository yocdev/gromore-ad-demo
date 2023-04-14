package com.ads.demo.ad.draw;

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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.bumptech.glide.Glide;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.nativeAd.TTViewBinder;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawAd;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawAdListener;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawExpressAdListener;
import com.bytedance.msdk.api.v2.ad.draw.GMUnifiedDrawAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdAppInfo;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMVideoListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.msdk.api.v2.slot.GMAdSlotDraw;
import com.header.app.untext.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * draw广告使用示例
 * <p>
 * 1. 广告加载 参考下面 loadDrawAdWithCallback 方法
 * 2. 广告渲染 ： 参考下面 showAd 方法
 */

/**
 * 注：
 * 1. 通过 GMDrawAd.isExpressAd()判断是否是模板广告
 * 2. 自渲染广告：通过 GMDrawAd.getImageMode()来判断 大图、小图、组图和视频广告
 * 详见demo
 */
public class DrawActivity extends Activity implements View.OnClickListener {
    private static final String TAG = AppConst.TAG_PRE;
    private Context mContext;

    private TextView mTvAdUnitId; //draw广告位id
    private Button mBtLoadDraw; //加载draw广告
    private Button mBtShowDraw;//展示draw广告
    private Button mBtLoadShowDraw;//加载并展示draw广告
    private FrameLayout mDrawContainer; //展示draw广告的容器
    private RadioGroup radioGroup;


    /**
     * draw对应的广告对象
     * 每次加载draw广告的时候需要新建一个GMUnifiedDrawAd，否则可能会出现广告填充问题
     */
    private GMUnifiedDrawAd mGMUnifiedDrawAd; //广告加载对象
    private GMDrawAd mGMDrawAd; //draw广告对象
    private String mAdUnitId; //draw广告位

    private boolean mLoadSuccess; //是否加载成功
    private boolean mIsLoadedAndShow;//广告加载成功并展示


    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_ad);
        mContext = this.getBaseContext();
        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        mBtLoadDraw = findViewById(R.id.bt_load_draw);
        mBtShowDraw = findViewById(R.id.bt_show_draw);
        mBtLoadShowDraw = findViewById(R.id.bt_load_show_draw);
        mDrawContainer = findViewById(R.id.draw_container);
        radioGroup = findViewById(R.id.radio_group);
        mAdUnitId = getResources().getString(R.string.draw_unit_id);
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        mBtLoadDraw.setOnClickListener(this);
        mBtShowDraw.setOnClickListener(this);
        mBtLoadShowDraw.setOnClickListener(this);

        initRadioGroup();
    }

    private void initRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_draw) {
                    mAdUnitId = getResources().getString(R.string.draw_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                } else if (checkedId == R.id.radio_draw_express) {
                    mAdUnitId = getResources().getString(R.string.draw_express_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                }
            }
        });
    }

    /**
     * config回调
     * 需要再onDestroy进行销毁 ，详见onDestroy方法
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            Log.e(TAG, "load ad 在config 回调中加载广告");
            loadDrawAd();
        }
    };

    private void loadDrawAdWithCallback() {
        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (GMMediationAdSdk.configLoadSuccess()) {
            Log.e(AppConst.TAG, "load ad 当前config配置存在，直接加载广告");
            loadDrawAd();
        } else {
            Log.e(AppConst.TAG, "load ad 当前config配置不存在，正在请求config配置....");
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不能使用内部类，否则在ondestory中无法移除该回调
        }
    }

    /**
     * 加载feed广告
     */
    private void loadDrawAd() {
        /**
         * 注：每次加载信息流广告的时候需要新建一个 GMUnifiedDrawAd，否则可能会出现广告填充问题
         */
        mGMUnifiedDrawAd = new GMUnifiedDrawAd(this, mAdUnitId);

        /**
         * 创建draw广告请求类型参数GMAdSlotDraw,更多参数配置请参考文档
         *
         */
        GMAdSlotDraw adSlotDraw = new GMAdSlotDraw.Builder()
                .setImageAdSize(600, 600)
                .setAdCount(1)
                .build();

        /**
         *
         * 请求广告，调用draw广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
         *
         * 注：每次加载信息流广告的时候需要新建一个 GMUnifiedDrawAd，否则可能会出现广告填充问题
         *
         */
        mGMUnifiedDrawAd.loadAd(adSlotDraw, new GMDrawAdLoadCallback() {
            @Override
            public void onAdLoadSuccess(List<GMDrawAd> ads) {
                /**
                 * 无广告可用
                 */
                if (ads == null || ads.isEmpty()) {
                    Log.e(TAG, "on draw: ad is null!");
                    TToast.show(mContext, "广告加载失败！");
                    return;
                }

                mLoadSuccess = true;
                mGMDrawAd = ads.get(0);
                TToast.show(mContext, "广告加载成功！");

                if (mIsLoadedAndShow) { //展示广告
                    showAd();
                }
            }

            @Override
            public void onAdLoadFail(AdError adError) {
                mLoadSuccess = false;
                Log.e(TAG, "load draw ad error : " + adError.code + ", " + adError.message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGMDrawAd != null) {
            mGMDrawAd.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGMUnifiedDrawAd != null) {
            mGMUnifiedDrawAd.destroy();
        }
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
        mGMDrawAd = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_load_draw: //加载Draw广告
                mLoadSuccess = false;
                mIsLoadedAndShow = false;
                /**
                 * 广告加载入口
                 */
                loadDrawAdWithCallback();
                break;
            case R.id.bt_show_draw://展示Draw广告
                showAd();
                break;
            case R.id.bt_load_show_draw://加载并展示Draw广告
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                /**
                 * 广告加载入口
                 */
                loadDrawAdWithCallback();
                break;
        }
    }


    /**
     * 展示Draw广告
     * 注：
     * 1. 通过GMDrawAd.isExpressAd()判断是否是模板广告
     * 2. 自渲染广告：通过GMNativeAd.getImageMode()来判断 大图、小图、组图和视频广告
     */
    private void showAd() {
        if (!mLoadSuccess || mGMDrawAd == null) {
            TToast.show(this, "请先加载广告");
            return;
        }
        if (!mGMDrawAd.isReady()) {
            TToast.show(this, "广告已经无效，请重新请求");
            return;
        }
        mLoadSuccess = false;
        mIsLoadedAndShow = false;

        View view = null;

        //--------------Draw模板广告渲染----------------
        if (mGMDrawAd.isExpressAd()) { //模板
            view = getExpressAdView(mDrawContainer, mGMDrawAd);
        }
        //--------------Draw自渲染广告渲染----------------
        else {
            if (mGMDrawAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_SMALL_IMG) { //Draw自渲染广告渲染 ：小图广告
                view = getSmallAdView(mDrawContainer, mGMDrawAd);
            } else if (mGMDrawAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_LARGE_IMG) {//Draw自渲染广告渲染 ：大图广告
                view = getLargeAdView(mDrawContainer, mGMDrawAd);
            } else if (mGMDrawAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_GROUP_IMG) {//Draw自渲染广告渲染 ：组图广告
                view = getGroupAdView(mDrawContainer, mGMDrawAd);
            } else if (mGMDrawAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VERTICAL_IMG) {//Draw自渲染广告渲染 ：竖图广告
                view = getVerticalAdView(mDrawContainer, mGMDrawAd);
            } else if (mGMDrawAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO) {//Draw自渲染广告渲染 ：视频广告
                view = getVideoView(mDrawContainer, mGMDrawAd);
            } else if (mGMDrawAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {///Draw自渲染广告渲染 ：竖版视频广告
                view = getVideoView(mDrawContainer, mGMDrawAd);
            } else {
                TToast.show(mContext, "图片展示样式错误");
            }
        }

        if (view != null) {
            view.setLayoutParams(new
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mDrawContainer.removeAllViews();
            mDrawContainer.addView(view);
        }
    }

    /**
     * Draw模板广告渲染
     */
    @SuppressWarnings("RedundantCast")
    private View getExpressAdView(ViewGroup parent, @NonNull final GMDrawAd ad) {
        final ExpressAdViewHolder adViewHolder;
        View convertView = null;
        try {
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listitem_ad_native_express, parent, false);
            adViewHolder = new ExpressAdViewHolder();
            adViewHolder.mAdContainerView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_express);
            convertView.setTag(adViewHolder);

            //判断是否存在dislike按钮
            if (ad.hasDislike()) {
                ad.setDislikeCallback(this, new GMDislikeCallback() {
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
            ad.setDrawAdListener(new GMDrawExpressAdListener() {
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
//                    Log.d(TAG, "onProgressUpdate");
                }
            });

            ad.render();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    /**
     * Draw自渲染广告渲染 ： 竖图广告
     */
    private View getVerticalAdView(ViewGroup parent, @NonNull final GMDrawAd ad) {
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
     * Draw自渲染广告渲染 ： 视频广告
     */
    @SuppressWarnings("RedundantCast")
    private View getVideoView(ViewGroup parent, @NonNull final GMDrawAd ad) {
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
//                    Log.d(TAG, "onProgressUpdate");
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
     * Draw自渲染广告渲染 ： 大图广告
     */
    @SuppressWarnings("RedundantCast")
    private View getLargeAdView(ViewGroup parent, @NonNull final GMDrawAd ad) {
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
                iconImageId(R.id.iv_listitem_icon).build();
        adViewHolder.viewBinder = viewBinder;
        bindData(convertView, adViewHolder, ad, viewBinder);
        if (ad.getImageUrl() != null) {
            Glide.with(mContext).load(ad.getImageUrl()).into(adViewHolder.mLargeImage);
        }
        return convertView;
    }

    /**
     * Draw自渲染广告渲染 ： 组图广告
     */
    @SuppressWarnings("RedundantCast")
    private View getGroupAdView(ViewGroup parent, @NonNull final GMDrawAd ad) {
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
     * Draw自渲染广告渲染 ： 小图广告
     */
    @SuppressWarnings("RedundantCast")
    private View getSmallAdView(ViewGroup parent, @NonNull final GMDrawAd ad) {
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


    GMDrawAdListener mGMDrawAdListener = new GMDrawAdListener() {
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

    private void bindData(View convertView, final AdViewHolder adViewHolder, final GMDrawAd ad, GMViewBinder viewBinder) {
        //设置dislike弹窗，如果有
        if (ad.hasDislike()) {
            final GMAdDislike ttAdDislike = ad.getDislikeDialog(this);
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
        ad.setDrawAdListener(mGMDrawAdListener);
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


    private void setDownLoadAppInfo(GMDrawAd drawAd, AdViewHolder adViewHolder) {
        if (adViewHolder == null) {
            return;
        }
        if (drawAd == null || drawAd.getNativeAdAppInfo() == null) {
            adViewHolder.app_info.setVisibility(View.GONE);
        } else {
            adViewHolder.app_info.setVisibility(View.VISIBLE);
            GMNativeAdAppInfo appInfo = drawAd.getNativeAdAppInfo();
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
    }


    private void removeAdView() {
        if (mDrawContainer != null) {
            mDrawContainer.removeAllViews();
        }
    }
}
