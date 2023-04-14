package com.bytedance.test;


/**
 * 初始化配置单元测试
 */
public class GMAdManagerHolderTestCase {

//    @Test
//    public void testAppConfig() {
//        GMConfigUserInfoForSegment userInfo = getUserInfo();
//        GMAdConfig adConfig = new GMAdConfig.Builder()
//                .setAppId("appid")
//                .setAppName("name")
//                .setDebug(true)
//                .setPublisherDid("publisherdid")
//                .setOpenAdnTest(true)
//                .setConfigUserInfoForSegment(userInfo)
//                .build();
//        GMMediationAdSdk.initialize(getContext(), adConfig);
//        Truth.assertThat(SdkGlobalInfo.get().getAppId()).isEqualTo("appid");
//        Truth.assertThat(SdkGlobalInfo.get().getName()).isEqualTo("name");
//        Truth.assertThat(adConfig.isDebug()).isEqualTo(true);
//        Truth.assertThat(SdkGlobalInfo.get().getPublisherDid()).isEqualTo("publisherdid");
//        Truth.assertThat(SdkGlobalInfo.get().isOpenAdnTest()).isEqualTo(true);
//
//        Truth.assertThat(userInfo.getUserId()).isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getUserId());
//        Truth.assertThat(userInfo.getGender()).isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getGender());
//        Truth.assertThat(userInfo.getChannel()).isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getChannel());
//        Truth.assertThat(userInfo.getSubChannel()).isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getSubChannel());
//        Truth.assertThat(userInfo.getAge()).isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getAge());
//        Truth.assertThat(userInfo.getUserValueGroup()).isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getUserValueGroup());
//        Truth.assertThat(userInfo.getCustomInfos().get("aaaa"))
//                .isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getCustomInfos().get("aaaa"));
//        Truth.assertThat(userInfo.getCustomInfos().get("bbbb"))
//                .isEqualTo(SdkGlobalInfo.get().getGMConfigUserInfoForSegment().getCustomInfos().get("bbbb"));
//    }
//
//    @Test
//    public void testAppPangleConfig() {
//        Map<String, String> mapConfig = new HashMap<>();
//        mapConfig.put("3333", "3333");
//        GMAdConfig adConfig = new GMAdConfig.Builder()
//                .setAppId("appid")
//                .setAppName("name")
//                .setDebug(true)
//                .setPangleOption(new GMPangleOption.Builder()
//                        .setIsPaid(true)
//                        .setTitleBarTheme(GMAdConstant.TITLE_BAR_THEME_DARK)
//                        .setAllowShowNotify(true)
//                        .setAllowShowPageWhenScreenLock(true)
//                        .setDirectDownloadNetworkType(GMAdConstant.NETWORK_STATE_4G)
//                        .setIsUseTextureView(true)
//                        .setNeedClearTaskReset("NeedClearTaskReset")
//                        .setData("1111")
//                        .setData("2222", "2222")
//                        .setData(mapConfig)
//                        .setKeywords("keywords")
//                        .build())
//                .build();
//        GMMediationAdSdk.initialize(getContext(), adConfig);
//
//        Truth.assertThat(SdkGlobalInfo.get().isPanglePaid()).isEqualTo(true);
//        Truth.assertThat(SdkGlobalInfo.get().getPangleTitleBarTheme()).isEqualTo(GMAdConstant.TITLE_BAR_THEME_DARK);
//        Truth.assertThat(SdkGlobalInfo.get().isPangleAllowShowNotify()).isEqualTo(true);
//        Truth.assertThat(SdkGlobalInfo.get().isPangleAllowShowPageWhenScreenLock()).isEqualTo(true);
//        Truth.assertThat(SdkGlobalInfo.get().getPangledirectDownloadNetworkType()).isEqualTo(new int[]{GMAdConstant.NETWORK_STATE_4G});
//        Truth.assertThat(SdkGlobalInfo.get().isPangleUseTextureView()).isEqualTo(true);
//        Truth.assertThat(SdkGlobalInfo.get().getPangleNeedClearTaskReset()).isEqualTo(new String[]{"NeedClearTaskReset"});
//        Truth.assertThat(SdkGlobalInfo.get().getPangleData()).isEqualTo("1111");
//        Map<String, String> map = new HashMap<>();
//        map.put("2222", "2222");
//        map.put("3333", "3333");
//        Truth.assertThat(SdkGlobalInfo.get().getExtraData()).isEqualTo(map);
//        Truth.assertThat(SdkGlobalInfo.get().getPangleKeywords()).isEqualTo("keywords");
//    }
//
//    @Test
//    public void testAppPangleCustomContrillerConfig() {
//        GMAdConfig adConfig = new GMAdConfig.Builder()
//                .setAppId("appid")
//                .setAppName("name")
//                .setPangleOption(new GMPangleOption.Builder()
//                        .build())
//                .build();
//        GMMediationAdSdk.initialize(getContext(), adConfig);
//    }
//
//    private GMConfigUserInfoForSegment getUserInfo() {
//        GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();
//        String userId = "A-Za-z0-9-_";
//        userInfo.setUserId(userId);
//        Truth.assertThat(userInfo.getUserId()).isEqualTo(userId);
//
//        userInfo.setGender(GMConfigUserInfoForSegment.GENDER_UNKNOWN);
//        Truth.assertThat(userInfo.getGender()).isEqualTo(GMConfigUserInfoForSegment.GENDER_UNKNOWN);
//
//        String channel = "A-Za-z0-9-_";
//        userInfo.setChannel(channel);
//        Truth.assertThat(userInfo.getChannel()).isEqualTo(channel);
//
//        String subChannel = "A-Za-z0-9-_";
//        userInfo.setSubChannel(subChannel);
//        Truth.assertThat(userInfo.getSubChannel()).isEqualTo(subChannel);
//
//        int age = 999;
//        userInfo.setAge(age);
//        Truth.assertThat(userInfo.getAge()).isEqualTo(age);
//
//        String userValueGroup = "A-Za-z0-9-_";
//        userInfo.setUserValueGroup(userValueGroup);
//        Truth.assertThat(userInfo.getUserValueGroup()).isEqualTo(userValueGroup);
//
//        Map<String, String> customInfos = new HashMap<>();
//        customInfos.put("aaaa", "test111");
//        customInfos.put("bbbb", "test222");
//        userInfo.setCustomInfos(customInfos);
//
//        Truth.assertThat(userInfo.getCustomInfos().get("aaaa")).isEqualTo("test111");
//        Truth.assertThat(userInfo.getCustomInfos().get("bbbb")).isEqualTo("test222");
//        return userInfo;
//    }

}
