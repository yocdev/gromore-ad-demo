package com.ads.demo.ad.banner;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ads.demo.BaseActivity;
import com.bumptech.glide.Glide;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.nativeAd.TTNativeAdAppInfo;
import com.bytedance.msdk.api.nativeAd.TTViewBinder;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeAdInfo;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeToBannerListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;
import com.header.app.untext.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * banner广告使用示例。本示例为复杂版，含有banner混出信息流功能。如果未使用混出功能，请参考简单版SimpleBannerActivity。
 * <p>
 * 1. 广告加载 ： 参考下面 loadBannerAdWithCallback 方法
 * 2. 广告展示 ： 参考下面 showBannerAd 方法
 * 3. 更多功能请参考接入文档
 */
public class BannerActivity extends BaseActivity {
    private static final String TAG = AppConst.TAG_PRE + BannerActivity.class.getSimpleName();
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
    private GMNativeToBannerListener mNativeToBannerListener;

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

                TToast.show(BannerActivity.this, "广告加载失败");
                Log.e(TAG, "load banner ad error : " + adError.code + ", " + adError.message);
            }

            @Override
            public void onAdLoaded() {
                mIsLoaded = true;
                if (mIsLoadedAndShow) {
                    showBannerAd();
                }

                TToast.show(BannerActivity.this, "广告加载成功");
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
        // banner混出信息流时，将信息流自渲染广告素材转成view。信息流模板无需处理。
        mNativeToBannerListener = new GMNativeToBannerListener() {
            @Override
            public View getGMBannerViewFromNativeAd(GMNativeAdInfo ad) {
                // 1、根据GMNativeAd提供的素材，创建view
                // 2、调用ad.registerView函数进行注册
                // 3、返回view
                return getBannerViewFromNativeAd(ad);
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
        // banner中混出自渲染信息流广告时，提供素材转成view的listener
        mBannerViewAd.setNativeToBannerListener(mNativeToBannerListener);

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
        // todo czf 待删除
    }

    /**
     ********************** 以下为banner混出信息流广告时，将信息流自渲染素材转换成view的代码 *****************
     **********************              未使用混出功能，可以忽略以下代码                *****************
     */

    private View getBannerViewFromNativeAd(GMNativeAdInfo mGMNativeAd) {
        View view = null;
        if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_SMALL_IMG) { //原生小图
            view = getSmallAdView(null, mGMNativeAd);

        } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_LARGE_IMG) {//原生大图
            view = getLargeAdView(null, mGMNativeAd);

        } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_GROUP_IMG) {//原生组图
            view = getGroupAdView(null, mGMNativeAd);

        } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO) {//原生视频
            view = getVideoView(null, mGMNativeAd);

        } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VERTICAL_IMG) {//原生竖版图片
            view = getVerticalAdView(null, mGMNativeAd);

        } else if (mGMNativeAd.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {//原生视频
            view = getVideoView(null, mGMNativeAd);
        } else {
            TToast.show(this, "图片展示样式错误");
        }
        return view;
    }

    private View getVerticalAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
        VerticalAdViewHolder adViewHolder;
        View convertView = null;
        GMViewBinder viewBinder;
        convertView = LayoutInflater.from(this).inflate(R.layout.listitem_ad_vertical_pic, parent, false);
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
            Glide.with(this).load(ad.getImageUrl()).into(adViewHolder.mVerticalImage);
        }

        return convertView;
    }

    //渲染视频广告，以视频广告为例，以下说明
    @SuppressWarnings("RedundantCast")
    private View getVideoView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
        VideoAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        try {
            convertView = LayoutInflater.from(this).inflate(R.layout.listitem_ad_large_video, parent, false);
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
            //绑定广告数据、设置交互回调
            bindData(convertView, adViewHolder, ad, viewBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @SuppressWarnings("RedundantCast")
    private View getLargeAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
        final LargeAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(this).inflate(R.layout.listitem_ad_large_pic, parent, false);
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
            Glide.with(this).load(ad.getImageUrl()).into(adViewHolder.mLargeImage);
        }
        return convertView;
    }

    @SuppressWarnings("RedundantCast")
    private View getGroupAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
        GroupAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(this).inflate(R.layout.listitem_ad_group_pic, parent, false);
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
                Glide.with(this).load(image1).into(adViewHolder.mGroupImage1);
            }
            if (image2 != null) {
                Glide.with(this).load(image2).into(adViewHolder.mGroupImage2);
            }
            if (image3 != null) {
                Glide.with(this).load(image3).into(adViewHolder.mGroupImage3);
            }
        }
        return convertView;
    }


    @SuppressWarnings("RedundantCast")
    private View getSmallAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
        SmallAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(this).inflate(R.layout.listitem_ad_small_pic, null, false);
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
            Glide.with(this).load(ad.getImageUrl()).into(adViewHolder.mSmallImage);
        }
        return convertView;
    }

    private void bindData(View convertView, final AdViewHolder adViewHolder, final GMNativeAdInfo ad, GMViewBinder viewBinder) {
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
                            TToast.show(BannerActivity.this, "点击 " + value);
                        }

                        @Override
                        public void onCancel() {
                            TToast.show(BannerActivity.this, "dislike 点击了取消");
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
            Glide.with(this).load(icon).into(adViewHolder.mIcon);
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
                TToast.show(this, "交互类型异常");
        }
    }


    private void setDownLoadAppInfo(GMNativeAdInfo ttNativeAd, AdViewHolder adViewHolder) {
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
}
