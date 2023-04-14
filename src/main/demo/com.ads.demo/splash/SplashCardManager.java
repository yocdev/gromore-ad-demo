package com.ads.demo.splash;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ads.demo.util.UIUtils;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashCardListener;

import java.lang.ref.SoftReference;

/**
 * 卡片开屏管理类
 * Created by zhy on date
 * Usage:
 * Doc:
 */
public class SplashCardManager {
    // 必选 根据您平台上开屏代码位的配置决定开关
    final boolean isSplashCard = true; // 启用卡片开屏
    // 必选 根据您要展示的业务场景赋值，两个选项互斥，仅可打开一个
    final boolean isEnableBetweenActivity = false; // 启用Activity间展示
    final boolean isEnableInnerActivity = true; // 启用Activity内展示

    private volatile static SplashCardManager mInstance;
    //@[classname]
    private SoftReference<GMSplashAd> mSplashAdRef;
    private View mSplashView;
    private ViewGroup mSplashContainerView;
    // 开屏卡片是否Ready ，由Sdk内部通知
    private boolean isReady = false;

    private SplashCardPrivateListener mPrivateListener;
    private SoftReference<Callback> mCallbackRef;

    //@[classname]
    public void init(Activity activity, GMSplashAd splashAd, View splashView, Callback callback) {
        isReady = false;
        mSplashContainerView = null;
        if (activity == null || splashAd == null || splashView == null) {
            return;
        }
        mSplashAdRef = new SoftReference<>(splashAd);
        mSplashView = splashView;
        mCallbackRef = new SoftReference<>(callback);

        mPrivateListener = new SplashCardPrivateListener(activity, splashAd, mCallbackRef.get());
        splashAd.setSplashCardListener(mPrivateListener);
    }

    /**
     * 单例获取SplashCardManager对象
     */
    public static SplashCardManager getInstance() {
        if (mInstance == null) {
            synchronized (SplashCardManager.class) {
                if (mInstance == null) {
                    mInstance = new SplashCardManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 清除开屏卡片数据
     */
    private void clearSplashStaticData() {
        mSplashAdRef = null;
        mSplashView = null;
        mSplashContainerView = null;
    }

    //@[classname]
    private GMSplashAd getSplashAd() {
        return mSplashAdRef != null ? mSplashAdRef.get() : null;
    }

    private void notifySplashViewShow(ViewGroup viewGroup, Activity activity) {
        //@[classname]
        final GMSplashAd splashAd = SplashCardManager.getInstance().getSplashAd();
        if (splashAd != null) {
            if(TextUtils.equals(splashAd.getShowEcpm().getAdNetworkPlatformName(), GMAdConstant.CUSTOM_DATA_KEY_PANGLE)){
                /**
                 * 接入穿山甲4.7.1.2及以上版本请使用此接口
                 */
                splashAd.showSplashCardView(viewGroup,activity);
            } else {
                /**
                 * 接入穿山甲4.7.1.2以下版本请使用此接口，其他ADN也请使用此接口
                 */
                splashAd.splashMinWindowAnimationFinish();
            }
        }
    }

    /**
     * 卡片开屏，调用入口
     */
    private void startAnimation(final View splashContainer, final ViewGroup content,final Activity activity) {
        if (isSplashCard) {
            mSplashContainerView = startSplashCardAnimation(splashContainer, content,activity);
        }
    }

    /**
     * 打开卡片开屏
     */
    private ViewGroup startSplashCardAnimation(final View splash, final ViewGroup splashViewContainer,final Activity activity) {
        if (mCallbackRef != null && mCallbackRef.get() != null) {
            mCallbackRef.get().onSplashCardStart();
        }
        UIUtils.removeFromParent(splash);
        final FrameLayout splashViewLayout = new FrameLayout(splash.getContext());
        splashViewLayout.addView(splash, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        splashViewContainer.addView(splashViewLayout, FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        notifySplashViewShow(splashViewContainer,activity);
        return splashViewLayout;
    }

    private void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    /**
     * 可展示Activity间的卡片开屏
     */
    private boolean canShowBetweenActivityCard() {
        if (!isReady || !isEnableBetweenActivity) {
            // 不允许Activity间展示 或 SDK告知此次不能展示，清除数据
            clearSplashStaticData();
            return false;
        }
        return true;
    }

    /**
     * 展示Activity间的卡片开屏
     */
    public void showBetweenActivityCard(Activity activity, Callback callback) {
        if (!canShowBetweenActivityCard()) {
            // 不可展示Activity间卡片，直接返回
            return;
        }
        if (activity == null || mSplashAdRef == null || mSplashAdRef.get() == null || mSplashView == null) {
            return;
        }
        startAnimation(mSplashView, (ViewGroup) activity.getWindow().getDecorView(),activity);
        if (mSplashContainerView != null) {
            // activity间的卡片，取消转屏动画
            activity.overridePendingTransition(0, 0);
        }
        //@[classname]
        GMSplashAd splashAd = SplashCardManager.getInstance().getSplashAd();
        mPrivateListener = new SplashCardPrivateListener(activity, splashAd, callback);
        mPrivateListener.setSplashContainerView(mSplashContainerView);
        if (splashAd != null) {
            splashAd.setSplashCardListener(mPrivateListener);
        }
    }

    /**
     * Activity内是否可展示
     */
    public boolean canShowInnerActivityCard() {
        if (isReady) {
            // 支持Activity内展示，跳过关闭
            if (isEnableInnerActivity) {
                return true;
            }
            // 不支持Activity内展示同时不支持Activity间展示，清除数据
            if (!isEnableBetweenActivity) {
                clearSplashStaticData();
            }
        }
        return false;
    }

    /**
     * 展示Activity内的卡片
     */
    private void showInnerActivitySplashCard(Activity activity) {
        if (!canShowInnerActivityCard()) {
            // 不可展示Activity间卡片，直接返回
            return;
        }
        if (activity == null || mSplashAdRef == null || mSplashView == null) {
            return;
        }
        startAnimation(mSplashView, (ViewGroup) activity.getWindow().getDecorView(),activity);
        if (mPrivateListener != null) {
            mPrivateListener.setSplashContainerView(mSplashContainerView);
        }

    }

    private void splashCardClick(){
        if (mCallbackRef != null && mCallbackRef.get() != null) {
            mCallbackRef.get().onSplashCardClick();
        }
    }

    private void splashClickEyeClick(){
        if (mCallbackRef != null && mCallbackRef.get() != null) {
            mCallbackRef.get().onSplashClickEyeClick();
        }
    }

    /**
     * 开屏卡片的Listener
     */
    private static class SplashCardPrivateListener implements GMSplashCardListener {

        private final SoftReference<Activity> mActivity;

        private SoftReference<View> mSplashContainerView;
        //@[classname]
        private final SoftReference<GMSplashAd> mSplashAd;

        private final SoftReference<Callback> mCallback;

        //@[classname]
        public SplashCardPrivateListener(Activity activity, GMSplashAd splashAd, Callback callback) {
            mActivity = new SoftReference<>(activity);
            mSplashAd = new SoftReference<>(splashAd);
            mCallback = new SoftReference<>(callback);
        }

        public void setSplashContainerView(View splashContainerView) {
            mSplashContainerView = new SoftReference<>(splashContainerView);
        }

        @Override
        public void onSplashEyeReady() {
            if (getInstance().canShowInnerActivityCard()) {
                // 在Activity内实现卡片开屏时，调起卡片开屏
                SplashCardManager.getInstance().showInnerActivitySplashCard(mActivity.get());
            }
        }

        @Override
        public void onSplashClickEyeClose() {
            if (getInstance().isEnableInnerActivity) {
                // 卡片关闭，清除数据
                if (mSplashContainerView != null && mSplashContainerView.get() != null) {
                    mSplashContainerView.get().setVisibility(View.GONE);
                    UIUtils.removeFromParent(mSplashContainerView.get());
                }
            } else if (getInstance().isEnableBetweenActivity) {
                //接收点击关闭按钮的事件将开屏卡片移除。
                if (mSplashContainerView != null && mSplashContainerView.get() != null) {
                    mSplashContainerView.get().setVisibility(View.GONE);
                    UIUtils.removeFromParent(mSplashContainerView.get());
                }
            }
            if (mCallback.get() != null) {
                mCallback.get().onSplashCardClose();
            }
            // 开屏卡片关闭时清除数据
            SplashCardManager.getInstance().clearSplashStaticData();
        }

        @Override
        public void setSupportSplashClickEye(boolean isReady) {
            SplashCardManager.getInstance().setReady(isReady);
        }

        @Override
        public Activity getActivity() {
            return mActivity.get();
        }

        @Override
        public void onSplashCardClick() {
            SplashCardManager.getInstance().splashCardClick();
        }

        @Override
        public void onSplashClickEyeClick() {
            SplashCardManager.getInstance().splashClickEyeClick();
        }
    }

    public interface Callback {
        /**
         * 当开屏卡片开始时回调
         */
        void onSplashCardStart();

        /**
         * 当开屏卡片关闭时回调
         */
        void onSplashCardClose();
        /**
         * 媒体卡片点击回调
         */
        void onSplashCardClick();
        /**
         * 媒体点睛点击回调
         */
        void onSplashClickEyeClick();
    }
}
