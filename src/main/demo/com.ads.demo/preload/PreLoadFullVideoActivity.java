package com.ads.demo.preload;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.ads.demo.AppConst;
import com.ads.demo.BaseActivity;
import com.ads.demo.manager.preload.PreLoadFullVideoManager;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdListener;
import com.bytedance.msdk.api.v2.ad.fullvideo.GMFullVideoAdLoadCallback;
import com.bytedance.msdk.api.TToast;
import com.header.app.untext.R;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadFullVideoActivity extends BaseActivity {

    private static final String TAG = AppConst.TAG_PRE + PreLoadFullVideoActivity.class.getSimpleName();

    private TextView mTvAdUnitHorizontalId;
    private Button mBtShowHorizontalFullVideo;
    //横屏广告位id
    private String mAdUnitHorizontalId;
    private PreLoadFullVideoManager mPreLoadFullVideoManager;
    //是否加载成功
    private GMFullVideoAdListener mTTFullVideoAdListener;
    //是否展示过了
    private boolean mIsShow;
    //是否load失败
    private boolean mIsLoadFail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload_full_video_main);
        mTvAdUnitHorizontalId = findViewById(R.id.tv_ad_unit_horizontal_id);
        mBtShowHorizontalFullVideo = findViewById(R.id.bt_show_horizontal_full_video);

        mAdUnitHorizontalId = getResources().getString(R.string.full_video_horizontal_unit_id);
        mTvAdUnitHorizontalId.setText(String.format(getResources().getString(R.string.horizontal_ad_unit_id), mAdUnitHorizontalId));

        initListener();
        initAdLoader();
    }

    @Override
    public void initListener() {
        mTTFullVideoAdListener = new GMFullVideoAdListener() {

            @Override
            public void onFullVideoAdShow() {
                mIsShow = true;
                Log.d(TAG, "onFullVideoAdShow");
            }

            @Override
            public void onFullVideoAdShowFail(AdError adError) {
                Log.d(TAG, "onFullVideoAdShowFail");
            }

            @Override
            public void onFullVideoAdClick() {
                Log.d(TAG, "onFullVideoAdClick");
            }

            @Override
            public void onFullVideoAdClosed() {
                Log.d(TAG, "onFullVideoAdClosed");
            }

            @Override
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete");
            }

            @Override
            public void onVideoError() {
                Log.d(TAG, "onVideoError");
            }

            @Override
            public void onSkippedVideo() {
                Log.d(TAG, "onSkippedVideo");
            }

            @Override
            public void onRewardVerify(@NonNull RewardItem rewardItem) {
                Log.d(TAG, "onRewardVerify");
            }
        };
        mBtShowHorizontalFullVideo.setOnClickListener(v -> {
            if (mIsLoadFail) {
                TToast.show(PreLoadFullVideoActivity.this, "预缓存失败，请退出页面重新进入");
            } else if (mIsShow) {
                TToast.show(PreLoadFullVideoActivity.this, "已经展示过了，请退出页面重新进入");
            }  else {
                show();
            }
        });
    }

    private void show() {
        if (mPreLoadFullVideoManager != null) {
            if (mPreLoadFullVideoManager.isReady()) {
                mPreLoadFullVideoManager.show(this);
            }
        }
    }

    @Override
    public void initAdLoader() {
        mPreLoadFullVideoManager = new PreLoadFullVideoManager(this,mAdUnitHorizontalId,GMAdConstant.HORIZONTAL, new GMFullVideoAdLoadCallback() {

            @Override
            public void onFullVideoLoadFail(AdError adError) {
                mIsLoadFail = true;
                TToast.show(PreLoadFullVideoActivity.this, "广告加载失败");
                Log.e(TAG, "onFullVideoLoadFail....全屏加载失败！");
            }

            @Override
            public void onFullVideoAdLoad() {
                TToast.show(PreLoadFullVideoActivity.this, "广告加载成功");
                Log.d(TAG, "onFullVideoAdLoad....加载成功！");
            }

            @Override
            public void onFullVideoCached() {
                TToast.show(PreLoadFullVideoActivity.this, "广告Cache成功");
                Log.d(TAG, "onFullVideoCached....缓存成功！");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreLoadFullVideoManager != null) {
            mPreLoadFullVideoManager.destroy();
        }
    }
}
