package com.ads.demo.ad.feed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

import com.ads.demo.AppConst;
import com.bumptech.glide.Glide;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.TToast;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.nativeAd.TTNativeAdAppInfo;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.GMAdAppDownloadListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeExpressAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMVideoListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.header.app.untext.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecycleViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = AppConst.TAG_PRE + "Feed";

    //--------------------信息流内容-------------------------
    private static final int ITEM_VIEW_TYPE_NORMAL = 0;

    //--------------------信息流模板广告-------------------------
    private static final int ITEM_VIEW_TYPE_EXPRESS_AD = 6;//自渲染模板广告

    //--------------------信息流自渲染广告-------------------------
    private static final int ITEM_VIEW_TYPE_GROUP_PIC_AD = 1; //自渲染：组图广告
    private static final int ITEM_VIEW_TYPE_SMALL_PIC_AD = 2;  //自渲染：小图广告
    private static final int ITEM_VIEW_TYPE_LARGE_PIC_AD = 3; //自渲染：大图广告
    private static final int ITEM_VIEW_TYPE_VIDEO = 4;  //自渲染：视频广告
    private static final int ITEM_VIEW_TYPE_VERTICAL_IMG = 5;//自渲染：竖版图片
    private static final int ITEM_VIEW_TYPE_VIDEO_VERTICAL = 7; //自渲染：竖版视频

    private Context mContext;


    private List<GMNativeAd> mData; //广告对象
    private DislikeCallBack mCallBack;

    public RecycleViewAdapter(Context context, List<GMNativeAd> data, DislikeCallBack callBack) {
        this.mContext = context;
        this.mData = data;
        this.mCallBack = callBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            //---------------信息流模板广告渲染--------------------
            case ITEM_VIEW_TYPE_EXPRESS_AD:
                return new ExpressViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_native_express, parent, false));

            //----------------信息流自渲染广告渲染----------------
            case ITEM_VIEW_TYPE_SMALL_PIC_AD: //信息流自渲染广告渲染 ： 小图广告渲染
                return new SmallAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, parent, false));
            case ITEM_VIEW_TYPE_LARGE_PIC_AD://信息流自渲染广告渲染 ： 大图广告渲染
                return new LargeAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_pic, parent, false));
            case ITEM_VIEW_TYPE_GROUP_PIC_AD://信息流自渲染广告渲染 ： 组图广告渲染
                return new GroupAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_group_pic, parent, false));
            case ITEM_VIEW_TYPE_VIDEO: //信息流自渲染广告渲染 ： 视频广告渲染
            case ITEM_VIEW_TYPE_VIDEO_VERTICAL://信息流自渲染广告渲染 ： 视频广告渲染
                return new VideoAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_video, parent, false));
            case ITEM_VIEW_TYPE_VERTICAL_IMG://信息流自渲染广告渲染 ： 竖图广告渲染
                return new VerticalAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_vertical_pic, parent, false));

            //---------------非广告：信息流内容--------------------
            default:
                return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_normal, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //信息流广告的样式，有大图、小图、组图和视频，通过ad.getImageMode()来判断
    @Override
    public int getItemViewType(int position) {
        GMNativeAd ad = mData.get(position);

        //---------------非广告：信息流内容--------------------
        if (ad == null) {
            return ITEM_VIEW_TYPE_NORMAL;
        }


        //---------------信息流模板广告--------------------
        if (ad != null && ad.isExpressAd()) {
            return ITEM_VIEW_TYPE_EXPRESS_AD;
        }

        //---------------信息流自渲染广告--------------------
        if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_SMALL_IMG) { //信息流自渲染广告 ： 小图广告
            return ITEM_VIEW_TYPE_SMALL_PIC_AD;
        } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_LARGE_IMG) {//信息流自渲染广告 ： 大图广告
            return ITEM_VIEW_TYPE_LARGE_PIC_AD;
        } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_GROUP_IMG) {//信息流自渲染广告 ： 组图广告
            return ITEM_VIEW_TYPE_GROUP_PIC_AD;
        } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO) {//信息流自渲染广告 ： 视频广告
            return ITEM_VIEW_TYPE_VIDEO;
        } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VERTICAL_IMG) {//信息流自渲染广告 ： 竖图广告
            return ITEM_VIEW_TYPE_VERTICAL_IMG;
        } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {//信息流自渲染广告 ： 竖版视频广告
            return ITEM_VIEW_TYPE_VIDEO_VERTICAL;
        }

        //---------------非广告：信息流内容--------------------
        TToast.show(mContext, "图片展示样式错误");
        return ITEM_VIEW_TYPE_NORMAL;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        GMNativeAd mGMNativeAd = mData.get(position);
        if (getItemViewType(position) != ITEM_VIEW_TYPE_NORMAL && mGMNativeAd != null && !mGMNativeAd.isReady()) {
            onBindNormalAdHolder(viewHolder, mGMNativeAd, position, "广告已经无效，请重新请求");
            return;
        }


        switch (getItemViewType(position)) {
            //----------------信息流模板广告渲染----------------
            case ITEM_VIEW_TYPE_EXPRESS_AD:
                onBindExpressAdHolder(viewHolder, mGMNativeAd);

            //----------------信息流自渲染广告渲染----------------
            case ITEM_VIEW_TYPE_SMALL_PIC_AD: //信息流自渲染广告渲染 ： 小图广告渲染
                onBindSmallAdHolder(viewHolder, mGMNativeAd);
            case ITEM_VIEW_TYPE_LARGE_PIC_AD://信息流自渲染广告渲染 ： 大图广告渲染
                onBindLargeAdHolder(viewHolder, mGMNativeAd);
            case ITEM_VIEW_TYPE_GROUP_PIC_AD://信息流自渲染广告渲染 ： 组图广告渲染
                onBindGroupAdHolder(viewHolder, mGMNativeAd);
            case ITEM_VIEW_TYPE_VIDEO://信息流自渲染广告渲染 ： 视频广告渲染
            case ITEM_VIEW_TYPE_VIDEO_VERTICAL://信息流自渲染广告渲染 ： 竖版视频广告渲染
                onBindVideoAdHolder(viewHolder, mGMNativeAd);
            case ITEM_VIEW_TYPE_VERTICAL_IMG://信息流自渲染广告渲染 ： 竖图广告渲染
                onBindVertialAdHolder(viewHolder, mGMNativeAd);

            //----------------信息流内容渲染----------------
            default:
                onBindNormalAdHolder(viewHolder, mGMNativeAd, position, "");
        }
    }

    /**
     *信息流自渲染广告渲染 ： 小图广告渲染
     */
    public void onBindSmallAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad) {
        if (viewHolder instanceof SmallAdViewHolder) {
            SmallAdViewHolder smallAdViewHolder = (SmallAdViewHolder) viewHolder;
            bindData(viewHolder.itemView, smallAdViewHolder, ad, smallAdViewHolder.viewBinder);
            if (ad.getImageUrl() != null) {
                Glide.with(mContext).load(ad.getImageUrl()).into(smallAdViewHolder.mSmallImage);
            }
        }
    }

    /**
     *信息流自渲染广告渲染 ： 大图广告渲染
     */
    public void onBindLargeAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad) {
        if (viewHolder instanceof LargeAdViewHolder) {
            LargeAdViewHolder largeAdViewHolder = (LargeAdViewHolder) viewHolder;
            bindData(viewHolder.itemView, largeAdViewHolder, ad, largeAdViewHolder.viewBinder);
            if (ad.getImageUrl() != null) {
                Glide.with(mContext).load(ad.getImageUrl()).into(largeAdViewHolder.mLargeImage);
            }
        }
    }

    /**
     *信息流自渲染广告渲染 ： 组图广告渲染
     */
    public void onBindGroupAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad) {
        if (viewHolder instanceof GroupAdViewHolder) {
            GroupAdViewHolder groupAdViewHolder = (GroupAdViewHolder) viewHolder;
            bindData(viewHolder.itemView, groupAdViewHolder, ad, groupAdViewHolder.viewBinder);
            if (ad.getImageList() != null && ad.getImageList().size() >= 3) {
                String image1 = ad.getImageList().get(0);
                String image2 = ad.getImageList().get(1);
                String image3 = ad.getImageList().get(2);
                if (image1 != null) {
                    Glide.with(mContext).load(image1).into(groupAdViewHolder.mGroupImage1);
                }
                if (image2 != null) {
                    Glide.with(mContext).load(image2).into(groupAdViewHolder.mGroupImage2);
                }
                if (image3 != null) {
                    Glide.with(mContext).load(image3).into(groupAdViewHolder.mGroupImage3);
                }
            }
        }
    }

    /**
     *信息流自渲染广告渲染 ： 视频广告渲染
     */
    @SuppressLint("LongLogTag")
    public void onBindVideoAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad) {
        if (viewHolder instanceof VideoAdViewHolder) {
            try {
                VideoAdViewHolder videoAdViewHolder = (VideoAdViewHolder) viewHolder;
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
//                            Log.d(TAG, "onProgressUpdate current：" + current );
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

                bindData(viewHolder.itemView, videoAdViewHolder, ad, videoAdViewHolder.viewBinder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *信息流自渲染广告渲染 ： 竖图广告渲染
     */
    public void onBindVertialAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad) {
        if (viewHolder instanceof VerticalAdViewHolder) {
            VerticalAdViewHolder verticalAdViewHolder = (VerticalAdViewHolder) viewHolder;
            bindData(viewHolder.itemView, verticalAdViewHolder, ad, verticalAdViewHolder.viewBinder);
            if (ad.getImageUrl() != null) {
                Glide.with(mContext).load(ad.getImageUrl()).into(verticalAdViewHolder.mVerticalImage);
            }
        }
    }

    /**
     *信息流模板广告渲染
     */
    @SuppressLint("LongLogTag")
    public void onBindExpressAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad) {
        if (viewHolder instanceof ExpressViewHolder) {
            ExpressViewHolder expressViewHolder = (ExpressViewHolder) viewHolder;
            try {
                //判断是否存在dislike按钮
                if (ad.hasDislike()) {
                    ad.setDislikeCallback((Activity) mContext, new GMDislikeCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                            TToast.show(mContext, "点击 " + value);
                            //用户选择不喜欢原因后，移除广告展示
                            mData.remove(ad);
                            mCallBack.dislikeClick();
                            notifyDataSetChanged();
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
                        if (expressViewHolder.mAdContainerView != null) {
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
                                expressViewHolder.mAdContainerView.removeAllViews();
                                expressViewHolder.mAdContainerView.addView(video, layoutParams);
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
//                            Log.d(TAG, "onProgressUpdate current：" + current );
                    }
                });

                ad.render();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     *信息流内容渲染  ： 非广告
     */
    public void onBindNormalAdHolder(@NonNull RecyclerView.ViewHolder viewHolder, GMNativeAd ad, int position, String errorMsg) {
        if (viewHolder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
            normalViewHolder.idle.setText("ListView item " + position + " " + errorMsg);
        }
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
                            mData.remove(ad);
                            mCallBack.dislikeClick();
                            notifyDataSetChanged();
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


    private void setDownLoadAppInfo(GMNativeAd gmNativeAd, AdViewHolder adViewHolder) {
        if (adViewHolder == null) {
            return;
        }
        if (gmNativeAd == null || gmNativeAd.getNativeAdAppInfo() == null) {
            adViewHolder.app_info.setVisibility(View.GONE);
        } else {
            adViewHolder.app_info.setVisibility(View.VISIBLE);
            TTNativeAdAppInfo appInfo = gmNativeAd.getNativeAdAppInfo();
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

    private abstract static class AdViewHolder extends RecyclerView.ViewHolder {

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
        }

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


    /**
     * 信息流自渲染广告：小图广告ViewHolder
     */
    private static class SmallAdViewHolder extends AdViewHolder {
        ImageView mSmallImage;

        public SmallAdViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSmallImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);

            app_info = itemView.findViewById(R.id.app_info);
            app_name = itemView.findViewById(R.id.app_name);
            author_name = itemView.findViewById(R.id.author_name);
            package_size = itemView.findViewById(R.id.package_size);
            permissions_url = itemView.findViewById(R.id.permissions_url);
            permissions_content = itemView.findViewById(R.id.permissions_content);
            privacy_agreement = itemView.findViewById(R.id.privacy_agreement);
            version_name = itemView.findViewById(R.id.version_name);

            viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_small_pic).
                    titleId(R.id.tv_listitem_ad_title).
                    sourceId(R.id.tv_listitem_ad_source).
                    descriptionTextId(R.id.tv_listitem_ad_desc).
                    mainImageId(R.id.iv_listitem_image).
                    logoLayoutId(R.id.tt_ad_logo).//logoView 建议为GroupView 类型
                    callToActionId(R.id.btn_listitem_creative).
                    iconImageId(R.id.iv_listitem_icon).build();
        }
    }

    /**
     * 信息流自渲染广告：大图广告ViewHolder
     */
    private static class LargeAdViewHolder extends AdViewHolder {
        ImageView mLargeImage;

        public LargeAdViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mLargeImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mLogo = itemView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

            app_info = itemView.findViewById(R.id.app_info);
            app_name = itemView.findViewById(R.id.app_name);
            author_name = itemView.findViewById(R.id.author_name);
            package_size = itemView.findViewById(R.id.package_size);
            permissions_url = itemView.findViewById(R.id.permissions_url);
            permissions_content = itemView.findViewById(R.id.permissions_content);
            privacy_agreement = itemView.findViewById(R.id.privacy_agreement);
            version_name = itemView.findViewById(R.id.version_name);

            viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_large_pic).
                    titleId(R.id.tv_listitem_ad_title).
                    descriptionTextId(R.id.tv_listitem_ad_desc).
                    sourceId(R.id.tv_listitem_ad_source).
                    mainImageId(R.id.iv_listitem_image).
                    callToActionId(R.id.btn_listitem_creative).
                    logoLayoutId(R.id.tt_ad_logo).//logoView 建议传入GroupView类型
                    iconImageId(R.id.iv_listitem_icon).build();
        }

    }

    /**
     * 信息流自渲染广告：组图广告ViewHolder
     */
    private static class GroupAdViewHolder extends AdViewHolder {
        ImageView mGroupImage1;
        ImageView mGroupImage2;
        ImageView mGroupImage3;

        public GroupAdViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mGroupImage1 = (ImageView) itemView.findViewById(R.id.iv_listitem_image1);
            mGroupImage2 = (ImageView) itemView.findViewById(R.id.iv_listitem_image2);
            mGroupImage3 = (ImageView) itemView.findViewById(R.id.iv_listitem_image3);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mLogo = itemView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

            app_info = itemView.findViewById(R.id.app_info);
            app_name = itemView.findViewById(R.id.app_name);
            author_name = itemView.findViewById(R.id.author_name);
            package_size = itemView.findViewById(R.id.package_size);
            permissions_url = itemView.findViewById(R.id.permissions_url);
            permissions_content = itemView.findViewById(R.id.permissions_content);
            privacy_agreement = itemView.findViewById(R.id.privacy_agreement);
            version_name = itemView.findViewById(R.id.version_name);

            viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_group_pic).
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
        }
    }

    /**
     * 信息流自渲染广告：竖图广告ViewHolder
     */
    private static class VerticalAdViewHolder extends AdViewHolder {
        ImageView mVerticalImage;

        public VerticalAdViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mVerticalImage = itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mLogo = itemView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

            app_info = itemView.findViewById(R.id.app_info);
            app_name = itemView.findViewById(R.id.app_name);
            author_name = itemView.findViewById(R.id.author_name);
            package_size = itemView.findViewById(R.id.package_size);
            permissions_url = itemView.findViewById(R.id.permissions_url);
            permissions_content = itemView.findViewById(R.id.permissions_content);
            privacy_agreement = itemView.findViewById(R.id.privacy_agreement);
            version_name = itemView.findViewById(R.id.version_name);

            viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_vertical_pic)
                    .titleId(R.id.tv_listitem_ad_title)
                    .descriptionTextId(R.id.tv_listitem_ad_desc)
                    .mainImageId(R.id.iv_listitem_image)
                    .iconImageId(R.id.iv_listitem_icon)
                    .callToActionId(R.id.btn_listitem_creative)
                    .sourceId(R.id.tv_listitem_ad_source)
                    .logoLayoutId(R.id.tt_ad_logo)//logoView 建议传入GroupView类型
                    .build();
        }
    }

    /**
     * 信息流自渲染广告：视频广告ViewHolder
     */
    private static class VideoAdViewHolder extends AdViewHolder {
        FrameLayout videoView;

        public VideoAdViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            videoView = (FrameLayout) itemView.findViewById(R.id.iv_listitem_video);
            // 可以通过GMNativeAd.getVideoWidth()、GMNativeAd.getVideoHeight()来获取视频的尺寸，进行UI调整（如果有需求的话）。
            // 在使用时需要判断返回值，如果返回为0，即表示该adn的广告不支持。目前仅Pangle和ks支持。
//                    int videoWidth = ad.getVideoWidth();
//                    int videoHeight = ad.getVideoHeight();
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mLogo = itemView.findViewById(R.id.tt_ad_logo);//logoView 建议传入GroupView类型

            app_info = itemView.findViewById(R.id.app_info);
            app_name = itemView.findViewById(R.id.app_name);
            author_name = itemView.findViewById(R.id.author_name);
            package_size = itemView.findViewById(R.id.package_size);
            permissions_url = itemView.findViewById(R.id.permissions_url);
            permissions_content = itemView.findViewById(R.id.permissions_content);
            privacy_agreement = itemView.findViewById(R.id.privacy_agreement);
            version_name = itemView.findViewById(R.id.version_name);

            //TTViewBinder 是必须类,需要开发者在确定好View之后把Id设置给TTViewBinder类，并在注册事件时传递给SDK
            viewBinder = new GMViewBinder.Builder(R.layout.listitem_ad_large_video).
                    titleId(R.id.tv_listitem_ad_title).
                    sourceId(R.id.tv_listitem_ad_source).
                    descriptionTextId(R.id.tv_listitem_ad_desc).
                    mediaViewIdId(R.id.iv_listitem_video).
                    callToActionId(R.id.btn_listitem_creative).
                    logoLayoutId(R.id.tt_ad_logo).//logoView 建议传入GroupView类型
                    iconImageId(R.id.iv_listitem_icon).build();
        }

    }

    /**
     * 信息流模板广告ViewHolder
     */
    private static class ExpressViewHolder extends RecyclerView.ViewHolder {
        FrameLayout mAdContainerView;

        public ExpressViewHolder(View itemView) {
            super(itemView);
            mAdContainerView = (FrameLayout) itemView.findViewById(R.id.iv_listitem_express);
        }
    }

    /**
     * 信息流内容ViewHolder （非广告）
     */
    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView idle;

        public NormalViewHolder(View itemView) {
            super(itemView);
            idle = (TextView) itemView.findViewById(R.id.text_idle);
        }
    }

    public interface DislikeCallBack {
        void dislikeClick();
    }

}
