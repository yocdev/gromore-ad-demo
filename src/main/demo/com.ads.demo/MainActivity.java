package com.ads.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.demo.ad.banner.BannerActivity;
import com.ads.demo.ad.draw.DrawActivity;
import com.ads.demo.ad.feed.FeedManagerActivity;
import com.ads.demo.ad.fullVideo.FullVideoActivity;
import com.ads.demo.ad.interstitial.InterstitialActivity;
import com.ads.demo.ad.interstitialFull.InterstitialFullActivity;
import com.ads.demo.ad.reward.RewardVideoActivity;
import com.ads.demo.ad.splash.SplashMainActivity;
import com.ads.demo.preload.PreLoadMainActivity;
import com.ads.demo.splash.SplashMinWindowManager;
import com.bumptech.glide.Glide;
import com.bytedance.msdk.adapter.config.TTAppDialogClickListener;
import com.bytedance.msdk.api.UIUtils;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMConfigUserInfoForSegment;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashMinWindowListener;
import com.bytedance.mtesttools.api.TTMediationTestTool;
import com.header.app.untext.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends Activity {
    private static final String TAG = AppConst.TAG_PRE + MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main);

        bindButton(R.id.pre_load, PreLoadMainActivity.class);
        bindButton(R.id.btn_main_reward, RewardVideoActivity.class);
        bindButton(R.id.btn_main_full, FullVideoActivity.class);
        bindButton(R.id.banner, BannerActivity.class);
        bindButton(R.id.interstitial, InterstitialActivity.class);
        bindButton(R.id.interstitialFull, InterstitialFullActivity.class);
        bindButton(R.id.splashAd, SplashMainActivity.class);
        bindButton(R.id.feedM, FeedManagerActivity.class);
        bindButton(R.id.draw, DrawActivity.class);
        bindButton(R.id.tools, ToolActivity.class);
        findViewById(R.id.change_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();
                userInfo.setUserId("msdk-demo" + new Random(10).nextInt());
                userInfo.setGender(GMConfigUserInfoForSegment.GENDER_UNKNOWN);
                userInfo.setChannel("msdk-channel");
                userInfo.setSubChannel("msdk-sub-channel");
                userInfo.setAge(999);
                userInfo.setUserValueGroup("msdk-demo-user-value-group");

                Map<String, String> customInfos = new HashMap<>();
                customInfos.put("aaaa", "test111" + new Random(10).nextInt());
                customInfos.put("bbbb", "test222");
                userInfo.setCustomInfos(customInfos);
                GMMediationAdSdk.setUserInfoForSegment(userInfo);
            }
        });
        findViewById(R.id.test_tools).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTMediationTestTool.launchTestTools(MainActivity.this, new TTMediationTestTool.ImageCallBack() {
                    @Override
                    public void loadImage(ImageView imageView, String url) {
                        Glide.with(getApplicationContext()).load(url).into(imageView);
                    }
                });
            }
        });

        TextView sdkVersion = findViewById(R.id.tx_sdk_version);
        sdkVersion.setText("sdk version : " + GMMediationAdSdk.getSdkVersion());

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        }

        initSplashMinWindowData();
    }

    /**
     * 初始化开屏小窗
     */
    private void initSplashMinWindowData() {
        View splashMinWindowView = addSplashMinWindowView();
        if (splashMinWindowView != null) {
            overridePendingTransition(0, 0);
            SplashMinWindowManager splashMinWindowManager = SplashMinWindowManager.getInstance();
            GMSplashAd splashAd = splashMinWindowManager.getSplashAd();
            SplashMinWindowListener splashClickEyeListener = new SplashMinWindowListener(splashMinWindowView, splashAd);
            if (splashAd != null) {
                splashAd.setMinWindowListener(splashClickEyeListener);
            }
        }
    }

    /**
     * 展示开屏小窗
     */
    private View addSplashMinWindowView() {
        final SplashMinWindowManager splashClickEyeManager = SplashMinWindowManager.getInstance();
        final GMSplashAd splashAd = splashClickEyeManager.getSplashAd();
        ViewGroup containerView = findViewById(android.R.id.content);
        return splashClickEyeManager.showMinWindowInTwoActivity((ViewGroup) getWindow().getDecorView(),
                containerView, new SplashMinWindowManager.AnimationCallBack() {
                    @Override
                    public void animationStart(int animationTime) {
                    }

                    @Override
                    public void animationEnd() {
                        if (splashAd != null) {
                            if (TextUtils.equals(splashAd.getShowEcpm().getAdNetworkPlatformName(), GMAdConstant.CUSTOM_DATA_KEY_PANGLE)) {
                                /**
                                 * 接入穿山甲4.7.1.2及以上版本请使用此接口
                                 */
                                splashAd.showSplashClickEyeView(containerView);
                            } else {
                                /**
                                 * 接入穿山甲4.7.1.2以下版本请使用此接口，其他ADN也请使用此接口
                                 */
                                splashAd.splashMinWindowAnimationFinish();
                            }
                        }
                    }
                });
    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, clz));
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 快手SDK所需相关权限，存储权限，此处配置作用于流量分配功能，关于流量分配，详情请咨询商务;如果您的APP不需要快手SDK的流量分配功能，则无需申请SD卡权限
        if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // 如果需要的权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() != 0) {
            // 否则，建议请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }


    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            Log.d("MainActivity", "已有权限。。。");
        } else {
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }


    static class SplashMinWindowListener implements GMSplashMinWindowListener {

        private SoftReference<View> mSplashView;
        private SoftReference<GMSplashAd> mSplashAd;

        public SplashMinWindowListener(View splashView, GMSplashAd splashAd) {
            mSplashView = new SoftReference<>(splashView);
            mSplashAd = new SoftReference<>(splashAd);
        }


        @Override
        public void onMinWindowStart() {
            Log.e(TAG, "onMinWindowStart");
        }

        @Override
        public void onMinWindowPlayFinish() {
            Log.e(TAG, "onMinWindowPlayFinish");
            //接收点击关闭按钮的事件将开屏点睛移除。
            if (mSplashView != null && mSplashView.get() != null) {
                mSplashView.get().setVisibility(View.GONE);
                UIUtils.removeFromParent(mSplashView.get());
                mSplashView = null;
                mSplashAd = null;
            }
            SplashMinWindowManager.getInstance().clearSplashStaticData();
        }
    }

    @Override
    public void onBackPressed() {
        showOpenOrInstallAppDialog(true);
    }

    private void showOpenOrInstallAppDialog(final boolean isFromBackPress) {
        int result =
                GMMediationAdSdk.showOpenOrInstallAppDialog(new TTAppDialogClickListener() {

                    @Override
                    public void onButtonClick(int buttonType) {
                        Log.e(TAG, "onButtonClick:" + buttonType);
                        if (isFromBackPress) {
                            finish();
                        }
                    }
                });
        Log.e(TAG, "showOpenOrInstallAppDialog result:" + result);
        if (result == TTAppDialogClickListener.NO_DLG) {
            if (isFromBackPress) {
                finish();
            } else {
                Toast.makeText(this, "没有可以安装或激活的应用", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
