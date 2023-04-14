package com.ads.demo.custom.toutiao;

import android.view.View;
import android.view.ViewGroup;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;

import java.util.ArrayList;
import java.util.List;

/**
 * YLH 信息流 开发者自渲染（自渲染）广告对象
 */
public class TouTiaoNativeAd extends GMCustomNativeAd {

    /**
     * ps 广告展示时需调用callDrawAdShow();回传GroMore
     * ps 广告点击时需调用callDrawAdClick();回传GroMore
     */
    private static final String TAG = AppConst.TAG_PRE + TouTiaoNativeAd.class.getSimpleName();

    private TTFeedAd mTTFeedAd;


    /**
     * 这个是以穿山甲TTFeedAd为例，来构建GroMore的native广告对象
     *
     * @param feedAd
     */
    public TouTiaoNativeAd(TTFeedAd feedAd) {
        this.mTTFeedAd = feedAd;
        //------------通过以下api设置相关字段 具体请参考自定义adapter接入文档-------------
        this.setTitle(feedAd.getTitle()); // appName
        this.setDescription(feedAd.getDescription());
        this.setActionText(feedAd.getButtonText());
        this.setIconUrl(feedAd.getIcon() != null ? feedAd.getIcon().getImageUrl() : null);
        this.setAdImageMode(feedAd.getImageMode());
        this.setInteractionType(feedAd.getInteractionType());
        this.setSource(feedAd.getSource()); // 从数据看也是appName
        this.setStarRating(feedAd.getAppScore());

        //大图和小图
        if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG ||
                feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG ||
                feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
            if (feedAd.getImageList() != null && !feedAd.getImageList().isEmpty() && feedAd.getImageList().get(0) != null) {
                TTImage image = feedAd.getImageList().get(0);
                this.setImageUrl(image.getImageUrl());
                this.setImageHeight(image.getHeight());
                this.setImageWidth(image.getWidth());
            }
        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {//组图(3图)
            if (feedAd.getImageList() != null && feedAd.getImageList().size() > 0) {
                List<String> images = new ArrayList<>();
                for (TTImage image : feedAd.getImageList()) {
                    images.add(image.getImageUrl());
                }
                this.setImageList(images);
            }
        }
        this.setMediaExtraInfo(feedAd.getMediaExtraInfo());
    }


    @Override
    public void registerViewForInteraction(ViewGroup container, List<View> clickViews, List<View> creativeViews, GMViewBinder viewBinder) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        return GMAdConstant.AdIsReadyStatus.AD_IS_READY;
    }
}
