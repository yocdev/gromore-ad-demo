package com.bytedance.test;

import android.location.Location;

import com.bytedance.msdk.api.AdSlot;
import com.bytedance.msdk.api.UserInfoForSegment;
import com.bytedance.msdk.api.v2.GMConfigUserInfoForSegment;
import com.bytedance.msdk.api.v2.GMLocation;
import com.bytedance.msdk.api.v2.GMPrivacyConfig;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;
import com.bytedance.msdk.api.v2.slot.GMAdSlotDraw;
import com.bytedance.msdk.api.v2.slot.GMAdSlotFullVideo;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitial;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitialFull;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.bytedance.msdk.core.SdkGlobalInfo;
import com.bytedance.msdk.util.TTCollectionUtils;
import com.bytedance.msdk.util.TTSortUtil;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class GMInfoCase {
    @Test
    public void testSdkGlobalInfo() {
        Map<String ,String> map= new HashMap<>();
        int[] networkType = new int[]{1,2,3,4};
        String[] taskReset = new String[]{"1","2","3"};
        GMPrivacyConfig gmPrivacyConfig = new GMPrivacyConfig();
        Map<String,Object> mLocalExtra = new HashMap<>();
        List<String> mPrimeRitList = new ArrayList<>();


        SdkGlobalInfo.get().setWXAppId("111");//------
        SdkGlobalInfo.get().setOpenAdnTest(true);//-----
        SdkGlobalInfo.get().setAppId("55555");//
        SdkGlobalInfo.get().setPanglePaid(true);//
        SdkGlobalInfo.get().setPublisherDid("1234567890");//
        SdkGlobalInfo.get().setPangleData("pangleData");//
        SdkGlobalInfo.get().setExtraData(map);//
        SdkGlobalInfo.get().setPangleAllowShowNotify(true);//
        SdkGlobalInfo.get().setPangledirectDownloadNetworkType(networkType);//
        SdkGlobalInfo.get().setPangleAllowShowPageWhenScreenLock(false);//
        SdkGlobalInfo.get().setName("麦果");//
        SdkGlobalInfo.get().setPangleTitleBarTheme(1);//
        SdkGlobalInfo.get().setPangleUseTextureView(true);//
        SdkGlobalInfo.get().setPangleNeedClearTaskReset(taskReset);//
        SdkGlobalInfo.get().setPangleKeywords("pangleKeywords");//
        SdkGlobalInfo.get().setPanglePluginUpdateConfig(1);//
        SdkGlobalInfo.get().setPrivacyConfig(gmPrivacyConfig);//
        SdkGlobalInfo.get().setLocalExtra(mLocalExtra);//
        SdkGlobalInfo.get().setPrimeRitList(mPrimeRitList);//
        SdkGlobalInfo.get().setHttps(true);//
//        SdkGlobalInfo.get().setIsOpenPangleCustom(true);//
        SdkGlobalInfo.get().setWxInstalled(false);//
        SdkGlobalInfo.get().setOpensdkVer("opensdkVer");
        SdkGlobalInfo.get().setSupportH265(true);
        SdkGlobalInfo.get().setSupportSplashZoomout(true);


        Assert.assertEquals("111", SdkGlobalInfo.get().getWXAppId());
        Assert.assertEquals(true, SdkGlobalInfo.get().isOpenAdnTest());
        Assert.assertEquals("55555", SdkGlobalInfo.get().getAppId());
        Assert.assertEquals(true, SdkGlobalInfo.get().isPanglePaid());
        Assert.assertEquals("1234567890", SdkGlobalInfo.get().getPublisherDid());
        Assert.assertEquals("pangleData", SdkGlobalInfo.get().getPangleData());
        Assert.assertEquals(map, SdkGlobalInfo.get().getExtraData());
        Assert.assertEquals(true, SdkGlobalInfo.get().isPangleAllowShowNotify());
        Assert.assertEquals(networkType, SdkGlobalInfo.get().getPangledirectDownloadNetworkType());
        Assert.assertEquals(false, SdkGlobalInfo.get().isPangleAllowShowPageWhenScreenLock());
        Assert.assertEquals("麦果", SdkGlobalInfo.get().getName());
        Assert.assertEquals(1, SdkGlobalInfo.get().getPangleTitleBarTheme());
        Assert.assertEquals(true, SdkGlobalInfo.get().isPangleUseTextureView());
        Assert.assertEquals(taskReset, SdkGlobalInfo.get().getPangleNeedClearTaskReset());
        Assert.assertEquals("pangleKeywords", SdkGlobalInfo.get().getPangleKeywords());
        Assert.assertEquals(1, SdkGlobalInfo.get().getPanglePluginUpdateConfig());
        Assert.assertEquals(gmPrivacyConfig, SdkGlobalInfo.get().getPrivacyConfig());
        Assert.assertEquals(mLocalExtra, SdkGlobalInfo.get().getLocalExtra());
        Assert.assertEquals(mPrimeRitList, SdkGlobalInfo.get().getPrimeRitList());
        Assert.assertEquals(true, SdkGlobalInfo.get().isHttps());
//        Assert.assertEquals(false, SdkGlobalInfo.get().isOpenPangleCustom());
        Assert.assertEquals(false, SdkGlobalInfo.get().isWxInstalled());
        Assert.assertEquals("opensdkVer", SdkGlobalInfo.get().getOpensdkVer());
        Assert.assertEquals(true, SdkGlobalInfo.get().isSupportH265());
        Assert.assertEquals(true, SdkGlobalInfo.get().isSupportSplashZoomout());
    }

    @Test
    public void testGMConfigUserInfoForSegment() {
        //    GMConfigUserInfoForSegment
        GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();
        userInfo.setUserId("msdk-demo");
        userInfo.setGender("male");
        userInfo.setChannel("msdk-channel");
        userInfo.setSubChannel("msdk-sub-channel");
        userInfo.setAge(999);
        userInfo.setUserValueGroup("msdk-demo-user-value-group");

        Map<String, String> customInfos = new HashMap<>();
        customInfos.put("aaaa", "test111");
        customInfos.put("bbbb", "test222");
        userInfo.setCustomInfos(customInfos);

        Assert.assertEquals("msdk-demo", userInfo.getUserId());
        Assert.assertEquals("male", userInfo.getGender());
        Assert.assertEquals("msdk-channel", userInfo.getChannel());
        Assert.assertEquals("msdk-sub-channel", userInfo.getSubChannel());
        Assert.assertEquals(999, userInfo.getAge());
        Assert.assertEquals("msdk-demo-user-value-group", userInfo.getUserValueGroup());
        Assert.assertEquals("test111", userInfo.getCustomInfos().get("aaaa"));
        Assert.assertEquals("test222", userInfo.getCustomInfos().get("bbbb"));
    }

    @Test
    public void testGMPrivacyConfig() {
//    GMPrivacyConfig
        GMLocation gmLocation = new GMLocation(3,4);
        List<String> appList = new ArrayList<>();
        List<String> devImeis = new ArrayList<>();
        GMPrivacyConfig config = new GMPrivacyConfig(){
            @Override
            public boolean isCanUseLocation() { //
                return true;
            }

            @Override
            public GMLocation getTTLocation() { //
                return gmLocation;
            }

            @Override
            public boolean appList() { //
                return true;
            }

            @Override
            public List<String> getAppList() {//
                return appList;
            }

            @Override
            public boolean isCanUsePhoneState() { //
                return true;
            }

            @Override
            public String getDevImei() { //
                return "devImei";
            }

            @Override
            public List<String> getDevImeis() {//
                return devImeis;
            }

            @Override
            public boolean isCanUseWifiState() { //
                return true;
            }

            @Override
            public boolean isCanUseWriteExternal() { //
                return true;
            }

            @Override
            public boolean isCanUseOaid() { //
                return true;
            }

            @Override
            public String getDevOaid() { //
                return "devOaid";
            }

            @Override
            public boolean isLimitPersonalAds() { //
                return true;
            }

            @Override
            public boolean isProgrammaticRecommend() {//
                return true;
            }

            @Override
            public boolean isCanUseAndroidId() {//
                return true;
            }

            @Override
            public String getAndroidId() { //
                return "androidId";
            }

            @Override
            public boolean isCanUseMacAddress() { //
                return true;
            }

            @Override
            public String getMacAddress() {//
                return "macAddress";
            }
        };

        Assert.assertEquals(true, config.isCanUseLocation());
        Assert.assertEquals(gmLocation, config.getTTLocation());
        Assert.assertEquals(true, config.appList());
        Assert.assertEquals(appList, config.getAppList());
        Assert.assertEquals(true, config.isCanUsePhoneState());
        Assert.assertEquals("devImei", config.getDevImei());
        Assert.assertEquals(devImeis, config.getDevImeis());
        Assert.assertEquals(true, config.isCanUseWifiState());
        Assert.assertEquals(true, config.isCanUseWriteExternal());
        Assert.assertEquals(true, config.isCanUseOaid());
        Assert.assertEquals("devOaid", config.getDevOaid());
        Assert.assertEquals(true, config.isLimitPersonalAds());
        Assert.assertEquals(true, config.isProgrammaticRecommend());
        Assert.assertEquals(true, config.isCanUseAndroidId());
        Assert.assertEquals("androidId", config.getAndroidId());
        Assert.assertEquals(true, config.isCanUseMacAddress());
        Assert.assertEquals("macAddress", config.getMacAddress());


    }

}
