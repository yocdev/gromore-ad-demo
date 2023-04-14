package com.bytedance.test;


/**
 * banner配置单元测试
 */
public class GMAdNativeAdSlotTestCase {
//
//    @Test
//    public void testAppConfig() {
//        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
//
//        Map<String,Object> map = new HashMap<>();
//        map.put("1111","1111");
//
//        GMAdSlotNative slotBanner = new GMAdSlotNative.Builder()
//                .setImageAdSize(320,150)// GMAdSize.BANNER_CUSTOME可以调用setImageAdSize设置大小
//                .setAdCount(10)
//                .setAdStyleType(AdSlot.TYPE_EXPRESS_AD)
//                .setTestSlotId("testslotid")
//                .setMuted(true)
//                .setVolume(0.9f)
//                .setUseSurfaceView(true)
//                .setExtraObject("1111","1111")
//                .setDownloadType(GMAdConstant.DOWNLOAD_TYPE_POPUP)
//                .build();
//        GMUnifiedNativeAd gmBannerAd = new GMUnifiedNativeAd(activity, "11111");
//        gmBannerAd.loadAd(slotBanner,null);
//
//        AdSlot adSlot = gmBannerAd.getAdSlot();
//
//        Truth.assertThat(adSlot.getImgAcceptedWidth()).isEqualTo(320);
//        Truth.assertThat(adSlot.getImgAcceptedHeight()).isEqualTo(150);
//        Truth.assertThat(adSlot.getTestSlotId()).isEqualTo("testslotid");
//        Truth.assertThat(adSlot.getTTVideoOption().isMuted()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getAdmobAppVolume()).isEqualTo(0.9f);
//        Truth.assertThat(adSlot.getTTVideoOption().useSurfaceView()).isEqualTo(true);
//        Truth.assertThat(adSlot.getAdCount()).isEqualTo(10);
//        Truth.assertThat(adSlot.getAdStyleType()).isEqualTo(AdSlot.TYPE_EXPRESS_AD);
//        Truth.assertThat(adSlot.getDownloadType()).isEqualTo(GMAdConstant.DOWNLOAD_TYPE_POPUP);
//
////        Truth.assertThat(adSlot.getReuestParam().getExtraObject()).isEqualTo(map);
//    }
//
//    @Test
//    public void testGDTConfig() {
//        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
//
////        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(activity, null);
//
//        GMAdSlotNative slotBanner = new GMAdSlotNative.Builder()
//                .setGMAdSlotGDTOption(new GMAdSlotGDTOption.Builder()
//                        .setNativeAdLogoParams(null)
//                        .setAutoPlayPolicy(GMAdSlotGDTOption.AutoPlayPolicy.WIFI)
////                        .setBrowserType(GMAdSlotGDTOption.BrowserType.TYPE_DEFAULT)
//                        .setDownAPPConfirmPolicy(GMAdSlotGDTOption.DownAPPConfirmPolicy.TYPE_DEFAULT)
//                        .setGDTAutoPlayMuted(true)
//                        .setGDTDetailPageMuted(true)
//                        .setGDTEnableDetailPage(true)
//                        .setGDTEnableUserControl(true)
//                        .setGDTMaxVideoDuration(22)
//                        .setGDTMinVideoDuration(33)
//                        .build())
//                .build();
//        GMUnifiedNativeAd gmBannerAd = new GMUnifiedNativeAd(activity, "11111");
//        gmBannerAd.loadAd(slotBanner, null);
//
//        AdSlot adSlot = gmBannerAd.getAdSlot();
//        Truth.assertThat(adSlot.getGdtNativeAdLogoParams()).isNull();
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().getGDTAutoPlayPolicy()).isEqualTo(GMAdSlotGDTOption.AutoPlayPolicy.WIFI);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().getBrowserType()).isEqualTo(GMAdSlotGDTOption.BrowserType.TYPE_DEFAULT);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().getDownAPPConfirmPolicy()).isEqualTo(GMAdSlotGDTOption.DownAPPConfirmPolicy.TYPE_DEFAULT);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().isGDTAutoPlayMuted()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().isGDTDetailPageMuted()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().isGDTEnableDetailPage()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().isGDTEnableUserControl()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().getGDTMaxVideoDuration()).isEqualTo(22);
//        Truth.assertThat(adSlot.getTTVideoOption().getGDTExtraOption().getGDTMinVideoDuration()).isEqualTo(33);
//
//    }
//
//    @Test
//    public void testBaiduConfig() {
//        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
//
//        GMAdSlotNative slotBanner = new GMAdSlotNative.Builder()
//                .setGMAdSlotBaiduOption(new GMAdSlotBaiduOption.Builder()
//                        .setDownloadAppConfirmPolicy(GMAdSlotBaiduOption.DOWNLOAD_APP_CONFIRM_ALWAYS)
//                        .setCacheVideoOnlyWifi(true)
//                        .setAppSid("11111")
//                        .setBaiduNativeSmartOptStyleParams(null)
//                        .setBaiduRequestParameters(null)
//                        .setBaiduSplashParams(null)
//                        .setShowDialogOnSkip(true)
//                        .setUseRewardCountdown(true)
//                        .build())
//                .build();
//        GMUnifiedNativeAd gmBannerAd = new GMUnifiedNativeAd(activity, "11111");
//        gmBannerAd.loadAd(slotBanner, null);
//
//        AdSlot adSlot = gmBannerAd.getAdSlot();
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getDownloadAppConfirmPolicy()).isEqualTo(GMAdSlotBaiduOption.DOWNLOAD_APP_CONFIRM_ALWAYS);
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().isCacheVideoOnlyWifi()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getAppSid()).isEqualTo("11111");
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getBaiduNativeSmartOptStyleParams()).isNull();
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getBaiduRequestParameters()).isNull();
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getBaiduSplashParams()).isNull();
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getShowDialogOnSkip()).isEqualTo(true);
//        Truth.assertThat(adSlot.getTTVideoOption().getBaiduExtraOption().getUseRewardCountdown()).isEqualTo(true);
//
//    }


}
