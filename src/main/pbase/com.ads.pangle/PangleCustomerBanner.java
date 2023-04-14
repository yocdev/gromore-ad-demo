package com.ads.pangle;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.bumptech.glide.Glide;
import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.nativeAd.TTNativeAdAppInfo;
import com.bytedance.msdk.api.nativeAd.TTViewBinder;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMNativeAdInfo;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.banner.GMCustomBannerAdapter;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdAppInfo;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.sdk.openadsdk.ComplianceInfo;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.header.app.untext.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PangleCustomerBanner extends GMCustomBannerAdapter {

    private static final String TAG = AppConst.TAG_PRE + PangleCustomerBanner.class.getSimpleName();

    private Context mContext;
    private View mBannerView;

    @Override
    public void load(Context context, GMAdSlotBanner adSlot, GMCustomServiceConfig serviceConfig) {
        mContext = context;
        int subAdType = serviceConfig.getSubAdtype();
        if (subAdType == GMAdConstant.SUB_ADTYPE_BANNER) {
            // 加载穿山甲banner
            loadPangleBanner(context, adSlot, serviceConfig);
        } else if (subAdType == GMAdConstant.SUB_ADTYPE_FEED) {
            int adStyleType = serviceConfig.getAdStyleType();
            if (adStyleType == GMAdConstant.TYPE_EXPRESS_AD) {
                // 加载穿山甲信息流模版广告，当banner来用
                loadPangleExpressAd(context, adSlot, serviceConfig);
            } else if (adStyleType == GMAdConstant.TYPE_NATIVE_AD) {
                // 加载穿山甲信息流自渲染广告，当banner来用
                loadPangleNativeAd(context, adSlot, serviceConfig);
            }
        }
    }

    // 加载穿山甲的信息流模板广告，并调用render，在renderSuccess以后，通过getView函数将view返回，用作banner使用
    private void loadPangleExpressAd(Context context, GMAdSlotBanner adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);

        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1);
        int[] size = getSize(adSlot.getBannerSize(), adSlot);
        int width = UIUtils.dip2px(context, size[0]);
        int height = UIUtils.dip2px(context, size[1]);
        adSlotBuilder.setImageAcceptedSize(width, height); // 信息流用这个api设置尺寸
        // 模版信息流的尺寸设置
        if (height > 0) {
            adSlotBuilder.setExpressViewAcceptedSize(width, height);
        } else {
            adSlotBuilder.setExpressViewAcceptedSize(width, 0); //height = 0;//高度为0,则高度会自适应
        }

        //获取server bidding物料
        if(TextUtils.isEmpty(getAdm())){
            adSlotBuilder.withBid(getAdm());
        }

        ttAdNative.loadNativeExpressAd(adSlotBuilder.build(), new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "广告加载失败  code = " + code + " message = " + message);
                callLoadFail(new GMCustomAdError(code, message));
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    Log.i(TAG, "广告请求成功，但返回结果是空");
                    callLoadFail(new GMCustomAdError(123456, "广告请求成功，但返回结果是空")); // errorcode和msg是样例，具体由由开发者统一设置
                    return;
                }
                Log.i(TAG, "广告加载成功了");
                TTNativeExpressAd expressAd = ads.get(0);
                expressAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int i) {
                        Log.i(TAG, "onAdClicked");
                        callBannerAdClicked();
                    }

                    @Override
                    public void onAdShow(View view, int i) {
                        Log.i(TAG, "onAdShow");
                        callBannerAdShow();
                    }

                    @Override
                    public void onRenderFail(View view, String s, int i) {
                        Log.i(TAG, "onRenderFail");
                    }

                    @Override
                    public void onRenderSuccess(View view, float v, float v1) {
                        Log.i(TAG, "onRenderSuccess");
                        if (mBannerView instanceof FrameLayout) {
                            removeFromParent(view);
                            ((FrameLayout) mBannerView).addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }
                    }
                });
                expressAd.setDislikeCallback((Activity) context, new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onShow() {
                        Log.i(TAG, "setDislikeCallback onShow");
                    }

                    @Override
                    public void onSelected(int i, String s, boolean b) {
                        Log.i(TAG, "setDislikeCallback onSelected");
                        callBannerAdClosed();
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "setDislikeCallback onCancel");
                    }
                });
                mBannerView = new FrameLayout(context);
                expressAd.render();

                // 获取adn的extra信息（可选），注意需要在callLoadSuccess之前设置
                setMediaExtraInfo(expressAd.getMediaExtraInfo());

                if (isClientBidding()) { //bidding广告类型
                    Map<String, Object> extraInfo = expressAd.getMediaExtraInfo();
                    //设置cpm
                    if (extraInfo != null) {
                        double cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                        callLoadSuccess(cpm);  //bidding广告成功回调，回传竞价广告价格
                    }
                } else {
                    callLoadSuccess();
                }
            }
        });
    }

    // 加载穿山甲的信息流自渲染广告，通过getView函数将view返回，用作banner使用
    private void loadPangleNativeAd(Context context, GMAdSlotBanner adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);

        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1);
        int[] size = getSize(adSlot.getBannerSize(), adSlot);
        int width = UIUtils.dip2px(context, size[0]);
        int height = UIUtils.dip2px(context, size[1]);
        adSlotBuilder.setImageAcceptedSize(width, height); // 信息流用这个api设置尺寸

        ttAdNative.loadFeedAd(adSlotBuilder.build(), new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "广告加载失败  code = " + code + " message = " + message);
                callLoadFail(new GMCustomAdError(code, message));
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    Log.i(TAG, "广告请求成功，但返回结果是空");
                    callLoadFail(new GMCustomAdError(123456, "广告请求成功，但返回结果是空")); // errorcode和msg是样例，具体由由开发者统一设置
                    return;
                }
                Log.i(TAG, "广告加载成功了");
                final TTFeedAd feedAd = ads.get(0);

                // 构造GMNativeAdInfo
                GMNativeAdInfo nativeAdInfo = getGMNativeAdInfo(feedAd);
                // 将GMNativeAdInfo素材渲染成view，用于banner使用；在下面的bindData函数中进行了registerView操作。
                View feedView = getBannerViewFromNativeAd(nativeAdInfo);
                mBannerView = new FrameLayout(context);
                ((FrameLayout) mBannerView).addView(feedView);

                if (isClientBidding()) {
                    Map<String, Object> extraInfo = feedAd.getMediaExtraInfo();
                    //设置cpm
                    if (extraInfo != null) {
                        double cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                        Logger.d("TTMediationSDK_ECMP", "pangle native 返回的 cpm价格：" + cpm);
                        callLoadSuccess(cpm);  //bidding广告成功回调，回传竞价广告价格
                    }
                }
                callLoadSuccess();
            }
        });
    }

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
            TToast.show(mContext, "图片展示样式错误");
        }
        return view;
    }

    private View getVerticalAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
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

    //渲染视频广告，以视频广告为例，以下说明
    @SuppressWarnings("RedundantCast")
    private View getVideoView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
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

    @SuppressWarnings("RedundantCast")
    private View getGroupAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
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


    @SuppressWarnings("RedundantCast")
    private View getSmallAdView(ViewGroup parent, @NonNull final GMNativeAdInfo ad) {
        SmallAdViewHolder adViewHolder;
        GMViewBinder viewBinder;
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, null, false);
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

    private void bindData(View convertView, final AdViewHolder adViewHolder, final GMNativeAdInfo ad, GMViewBinder viewBinder) {
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
        // ********* 重要! 这个涉及到广告计费，必须正确调用。**** convertView必须是com.bytedance.msdk.api.format.TTNativeAdView ****
        ad.registerView((Activity) mContext, (ViewGroup) convertView, clickViewList, creativeViewList, viewBinder);

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

    private GMNativeAdInfo getGMNativeAdInfo(TTFeedAd feedAd) {
        return new GMNativeAdInfo() {
            @Nullable
            @Override
            public String getTitle() {
                return feedAd.getTitle();
            }

            @Nullable
            @Override
            public String getDescription() {
                return feedAd.getDescription();
            }

            @Nullable
            @Override
            public String getIconUrl() {
                return feedAd.getIcon() != null ? feedAd.getIcon().getImageUrl() : null;
            }

            @Nullable
            @Override
            public String getImageUrl() {
                if (feedAd.getImageMode() == com.bytedance.sdk.openadsdk.TTAdConstant.IMAGE_MODE_VERTICAL_IMG ||
                        feedAd.getImageMode() == com.bytedance.sdk.openadsdk.TTAdConstant.IMAGE_MODE_LARGE_IMG ||
                        feedAd.getImageMode() == com.bytedance.sdk.openadsdk.TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                    if (feedAd.getImageList() != null && !feedAd.getImageList().isEmpty() && feedAd.getImageList().get(0) != null) {
                        TTImage image = feedAd.getImageList().get(0);
                        return image.getImageUrl();
                    }
                }
                return null;
            }

            @Nullable
            @Override
            public String getActionText() {
                return feedAd.getButtonText();
            }

            @Override
            public double getStarRating() {
                return feedAd.getAppScore();
            }

            @Nullable
            @Override
            public List<String> getImageList() {
                if (feedAd.getImageMode() == com.bytedance.sdk.openadsdk.TTAdConstant.IMAGE_MODE_GROUP_IMG) {//组图
                    if (feedAd.getImageList() != null && feedAd.getImageList().size() > 0) {
                        List<String> images = new ArrayList<>();
                        for (TTImage image : feedAd.getImageList()) {
                            images.add(image.getImageUrl());
                        }
                        return images;
                    }
                }
                return null;
            }

            @Nullable
            @Override
            public String getSource() {
                return feedAd.getSource();
            }

            @Override
            public int getAdImageMode() {
                return feedAd.getImageMode();
            }

            @Override
            public int getInteractionType() {
                return feedAd.getInteractionType();
            }

            @Override
            public void registerView(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<View> clickViews,
                                     @Nullable List<View> creativeViews, GMViewBinder viewBinder) {
                feedAd.registerViewForInteraction(container, clickViews, creativeViews, new TTNativeAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                        callBannerAdClicked();
                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                        callBannerAdClicked();
                    }

                    @Override
                    public void onAdShow(TTNativeAd ttNativeAd) {
                        callBannerAdShow();
                    }
                });
            }

            @Override
            public GMNativeAdAppInfo getNativeAdAppInfo() {
                ComplianceInfo info = feedAd.getComplianceInfo();
                GMNativeAdAppInfo ttNativeAdAppInfo = new GMNativeAdAppInfo();
                ttNativeAdAppInfo.setAppName(info.getAppName());
                ttNativeAdAppInfo.setAuthorName(info.getDeveloperName());
                ttNativeAdAppInfo.setPrivacyAgreement(info.getPrivacyUrl());
                ttNativeAdAppInfo.setVersionName(info.getAppVersion());
                ttNativeAdAppInfo.setPermissionsMap(info.getPermissionsMap());
                return ttNativeAdAppInfo;
            }

            @Override
            public boolean hasDislike() {
                return true;
            }

            @Nullable
            @Override
            public GMAdDislike getDislikeDialog(Activity activity, Map<String, Object> extra) {
                final com.bytedance.sdk.openadsdk.TTAdDislike dislikeDialog = feedAd.getDislikeDialog(activity);
                return new GMAdDislike() {
                    @Override
                    public void showDislikeDialog() {
                        if (dislikeDialog != null) {
                            dislikeDialog.showDislikeDialog();
                        }
                    }

                    @Override
                    public void setDislikeCallback(final GMDislikeCallback dislikeCallback) {
                        if (dislikeCallback != null) {
                            dislikeDialog.setDislikeInteractionCallback(new com.bytedance.sdk.openadsdk.TTAdDislike.DislikeInteractionCallback() {
                                @Override
                                public void onSelected(int i, String s, boolean b) {
                                    dislikeCallback.onSelected(i, s);
                                    callBannerAdClosed();
                                }

                                @Override
                                public void onCancel() {
                                    dislikeCallback.onCancel();
                                }

                                @Override
                                public void onShow() {
                                    dislikeCallback.onShow();
                                }
                            });
                        }
                    }
                };
            }

            @Nullable
            @Override
            public GMAdDislike getDislikeDialog(Activity activity) {
                return getDislikeDialog(activity, null);
            }
        };
    }

    private void loadPangleBanner(Context context, GMAdSlotBanner adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);
        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1);
        adNativeLoader.loadBannerExpressAd(adSlotBuilder.build(), new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "广告加载失败  code = " + code + " message = " + message);
                callLoadFail(new GMCustomAdError(code, message));
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.i(TAG, "广告加载成功了");
                TTNativeExpressAd mTTAd = ads.get(0);
                mTTAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                    @Override
                    public void onAdDismiss() {
                        Log.i(TAG, "onAdDismiss");
                    }

                    @Override
                    public void onAdClicked(View view, int i) {
                        Log.i(TAG, "onAdClicked");
                        callBannerAdClicked();
                    }

                    @Override
                    public void onAdShow(View view, int i) {
                        Log.i(TAG, "onAdShow");
                        callBannerAdShow();
                    }

                    @Override
                    public void onRenderFail(View view, String s, int i) {
                        Log.i(TAG, "onRenderFail");
                    }

                    @Override
                    public void onRenderSuccess(View view, float v, float v1) {
                        Log.i(TAG, "onRenderSuccess");
                        if (mBannerView instanceof FrameLayout) {
                            removeFromParent(view);
                            ((FrameLayout) mBannerView).addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }
                    }
                });
                mTTAd.setDislikeCallback((Activity) context, new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onShow() {
                        Log.i(TAG, "setDislikeCallback onShow");
                    }

                    @Override
                    public void onSelected(int i, String s, boolean b) {
                        Log.i(TAG, "setDislikeCallback onSelected");
                        callBannerAdClosed();
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "setDislikeCallback onCancel");
                    }
                });
                mTTAd.render();
                mBannerView = new FrameLayout(context);

                if (isClientBidding()) { //bidding广告类型
                    Map<String, Object> extraInfo = mTTAd.getMediaExtraInfo();
                    //设置cpm
                    double cpm = 0;
                    if (extraInfo != null) {
                        cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                    }
                    callLoadSuccess(cpm);  //bidding广告成功回调，回传竞价广告价格
                } else {
                    callLoadSuccess();
                }
            }
        });

    }

    private void removeFromParent(View view) {
        if (view != null) {
            ViewParent vp = view.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(view);
            }
        }
    }

    @Override
    public View getAdView() {
        return mBannerView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    /**
     * 是否clientBidding广告
     *
     * @return
     */
    public boolean isClientBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }

    private int[] getSize(int type, GMAdSlotBanner adSlotBanner) {
        switch (type) {
            case GMAdSize.BANNER_320_50:
                return new int[]{320, 50};
            case GMAdSize.BANNER_320_100:
                return new int[]{320, 100};
            case GMAdSize.BANNER_300_250:
                return new int[]{300, 250};
            case GMAdSize.BANNER_468_60:
                return new int[]{468, 60};
            case GMAdSize.BANNER_728_90:
                return new int[]{728, 90};
            case GMAdSize.BANNER_CUSTOME:
                if (adSlotBanner.getWidth() > 0 && adSlotBanner.getHeight() > 0) {
                    return new int[]{adSlotBanner.getWidth(), adSlotBanner.getHeight()};
                }
        }
        return new int[]{320, 50};
    }
}
