package com.bytedance.test;

import com.bytedance.msdk.api.AdSlot;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;
import com.bytedance.msdk.api.v2.slot.GMAdSlotDraw;
import com.bytedance.msdk.api.v2.slot.GMAdSlotFullVideo;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitial;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitialFull;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.bytedance.msdk.util.TTCollectionUtils;
import com.bytedance.msdk.util.TTSortUtil;

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
public class GMAdTestCase {
    @Test
    public void testAdslot() {
        Map<String, String> customData = new HashMap<>();
        AdSlot.Builder builder = new AdSlot.Builder();
        builder.setScenarioId("ScenarioId");
        builder.setV2Request(false);
        builder.setAdCount(3);
        builder.setBannerSize(2);
        builder.setCustomData(customData);
        builder.setDownloadType(1);
        builder.setForceLoadBottom(false);
        builder.setImageAdSize(100, 300);
        builder.setBidNotify(true);
        builder.setUserID("ueserId");
        builder.setSupportDeepLink(false);
        builder.setOrientation(1);
        builder.setRewardAmount(3);
        builder.setRewardName("仙豆");
        builder.setSplashButtonType(1);
        builder.setSplashShakeButton(true);
        AdSlot adSlot = builder.build();
        Assert.assertEquals("ScenarioId", adSlot.getScenarioId());
        Assert.assertEquals(false, adSlot.isV2Request());
        Assert.assertEquals(3, adSlot.getAdCount());
        Assert.assertEquals(2, adSlot.getBannerSize());
        Assert.assertEquals(customData, adSlot.getCustomData());
        Assert.assertEquals(1, adSlot.getDownloadType());
        Assert.assertEquals(false, adSlot.isForceLoadBottom());
        Assert.assertEquals(300, adSlot.getImgAcceptedHeight());
        Assert.assertEquals(100, adSlot.getImgAcceptedWidth());
        Assert.assertEquals(true, adSlot.isBidNotify());
        Assert.assertEquals("ueserId", adSlot.getUserID());
        Assert.assertEquals(false, adSlot.isSupportDeepLink());
        Assert.assertEquals(1, adSlot.getOrientation());
        Assert.assertEquals(2, adSlot.getAdStyleType());
        Assert.assertEquals(3, adSlot.getRewardAmount());
        Assert.assertEquals("仙豆", adSlot.getRewardName());
        Assert.assertEquals(1, adSlot.getSplashButtonType());
        Assert.assertEquals(true, adSlot.getSplashShakeButton());
    }

    @Test
    public void testFeedAdslot() {
        GMAdSlotNative adSlotNative = new GMAdSlotNative.Builder()
                .setImageAdSize(100, 100)
                .setShakeViewSize(200, 200)
                .setAdCount(3)
                .setUserID("1111")
                .setMuted(true)
                .setVolume(3)
                .setUseSurfaceView(false)
                .setExtraObject("key1", "value1")
                .setTestSlotId("111")
                .setDownloadType(2)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .build();

        Assert.assertEquals(100, adSlotNative.getWidth());
        Assert.assertEquals(100, adSlotNative.getHeight());
        Assert.assertEquals(200, adSlotNative.getShakeViewWidth());
        Assert.assertEquals(200, adSlotNative.getShakeViewHeight());
        Assert.assertEquals(3, adSlotNative.getAdCount());
        Assert.assertEquals("1111", adSlotNative.getUserID());
        Assert.assertEquals(true, adSlotNative.isMuted());
//        Assert.assertEquals(3, adSlotNative.getVolume());
        Assert.assertEquals(false, adSlotNative.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotNative.getParams().get("key1"));
        Assert.assertEquals("111", adSlotNative.getTestSlotId());
        Assert.assertEquals(2, adSlotNative.getDownloadType());
        Assert.assertEquals(true, adSlotNative.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotNative.getScenarioId());

    }

    @Test
    public void testDrawAdslot() {
        GMAdSlotDraw adSlotDraw = new GMAdSlotDraw.Builder()
                .setImageAdSize(100, 100)
                .setAdCount(3)
                .setExtraObject("key1", "value1")
                .setMuted(true)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .setTestSlotId("111")
                .build();

        Assert.assertEquals(100, adSlotDraw.getWidth());
        Assert.assertEquals(100, adSlotDraw.getHeight());
        Assert.assertEquals(3, adSlotDraw.getAdCount());
        Assert.assertEquals("value1", adSlotDraw.getParams().get("key1"));
        Assert.assertEquals(true, adSlotDraw.isMuted());
        Assert.assertEquals(true, adSlotDraw.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotDraw.getScenarioId());
        Assert.assertEquals("111", adSlotDraw.getTestSlotId());

    }

    @Test
    public void testBannerAdslot() {
        GMAdSlotBanner adSlotBanner = new GMAdSlotBanner.Builder()
                .setUserID("234")
                .setImageAdSize(100, 100)
                .setBannerSize(3)
                .setAllowShowCloseBtn(true)
                .setMuted(true)
                .setTestSlotId("111")
                .setVolume(30)
                .setUseSurfaceView(true)
                .setExtraObject("key1", "value1")
                .setDownloadType(2)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .build();

        Assert.assertEquals("234", adSlotBanner.getUserID());
        Assert.assertEquals(100, adSlotBanner.getWidth());
        Assert.assertEquals(100, adSlotBanner.getHeight());
        Assert.assertEquals(3, adSlotBanner.getBannerSize());
        Assert.assertEquals(true, adSlotBanner.isAllowShowCloseBtn());
        Assert.assertEquals(true, adSlotBanner.isMuted());
        Assert.assertEquals("111", adSlotBanner.getTestSlotId());
//        Assert.assertEquals(30, adSlotBanner.getVolume());
        Assert.assertEquals(true, adSlotBanner.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotBanner.getParams().get("key1"));
        Assert.assertEquals(2, adSlotBanner.getDownloadType());
        Assert.assertEquals(true, adSlotBanner.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotBanner.getScenarioId());

    }

    @Test
    public void testSplshAdslot() {
        GMAdSlotSplash adSlotSplash = new GMAdSlotSplash.Builder()
                .setSplashShakeButton(true)
                .setSplashButtonType(1)
                .setDownloadType(2)
                .setImageAdSize(100, 200)
                .setUserID("111")
                .setSplashPreLoad(true)
                .setMuted(true)
                .setTimeOut(3000)
                .setVolume(2)
                .setUseSurfaceView(true)
                .setExtraObject("key1", "value1")
                .setTestSlotId("111")
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .setForceLoadBottom(false)
                .build();

        Assert.assertEquals(true, adSlotSplash.getSplashShakeButton());
        Assert.assertEquals(1, adSlotSplash.getSplashButtonType());
        Assert.assertEquals(2, adSlotSplash.getDownloadType());
        Assert.assertEquals(100, adSlotSplash.getWidth());
        Assert.assertEquals(200, adSlotSplash.getHeight());
        Assert.assertEquals("111", adSlotSplash.getUserID());
        Assert.assertEquals(true, adSlotSplash.isSplashPreLoad());
        Assert.assertEquals(true, adSlotSplash.isMuted());
        Assert.assertEquals(3000, adSlotSplash.getTimeOut());
//        Assert.assertEquals(2, adSlotSplash.getVolume());
        Assert.assertEquals(true, adSlotSplash.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotSplash.getParams().get("key1"));
        Assert.assertEquals("111", adSlotSplash.getTestSlotId());
        Assert.assertEquals(true, adSlotSplash.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotSplash.getScenarioId());
        Assert.assertEquals(false, adSlotSplash.isForceLoadBottom());
    }

    @Test
    public void testInterstitialAdslot() {
        GMAdSlotInterstitial adSlotInterstitial = new GMAdSlotInterstitial.Builder()
                .setImageAdSize(100, 200)
                .setMuted(true)
                .setVolume(2)
                .setUseSurfaceView(true)
                .setExtraObject("key1", "value1")
                .setTestSlotId("111")
                .setDownloadType(2)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .build();

        Assert.assertEquals(2, adSlotInterstitial.getDownloadType());
        Assert.assertEquals(100, adSlotInterstitial.getWidth());
        Assert.assertEquals(200, adSlotInterstitial.getHeight());
        Assert.assertEquals(true, adSlotInterstitial.isMuted());
//        Assert.assertEquals(2, adSlotInterstitial.getVolume());
        Assert.assertEquals(true, adSlotInterstitial.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotInterstitial.getParams().get("key1"));
        Assert.assertEquals("111", adSlotInterstitial.getTestSlotId());
        Assert.assertEquals(true, adSlotInterstitial.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotInterstitial.getScenarioId());
    }

    @Test
    public void testInterstitialFullAdslot() {
        Map<String, String> customData = new HashMap<>();
        GMAdSlotInterstitialFull adSlotInterstitialFull = new GMAdSlotInterstitialFull.Builder()
                .setOrientation(1)
                .setRewardName("金币")
                .setRewardAmount(3)
                .setCustomData(customData)
                .setImageAdSize(100, 200)
                .setMuted(true)
                .setVolume(2)
                .setUseSurfaceView(true)
                .setExtraObject("key1", "value1")
                .setTestSlotId("111")
                .setDownloadType(2)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .build();

        Assert.assertEquals(1, adSlotInterstitialFull.getOrientation());
        Assert.assertEquals("金币", adSlotInterstitialFull.getRewardName());
        Assert.assertEquals(3, adSlotInterstitialFull.getRewardAmount());
        Assert.assertEquals(customData, adSlotInterstitialFull.getCustomData());
        Assert.assertEquals(100, adSlotInterstitialFull.getWidth());
        Assert.assertEquals(200, adSlotInterstitialFull.getHeight());
        Assert.assertEquals(true, adSlotInterstitialFull.isMuted());
//        Assert.assertEquals(2, adSlotInterstitialFull.getVolume());
        Assert.assertEquals(true, adSlotInterstitialFull.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotInterstitialFull.getParams().get("key1"));
        Assert.assertEquals("111", adSlotInterstitialFull.getTestSlotId());
        Assert.assertEquals(true, adSlotInterstitialFull.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotInterstitialFull.getScenarioId());
    }

    @Test
    public void testFullAdslot() {
        Map<String, String> customData = new HashMap<>();
        GMAdSlotFullVideo adSlotFullVideo = new GMAdSlotFullVideo.Builder()
                .setOrientation(1)
                .setRewardName("金币")
                .setRewardAmount(3)
                .setCustomData(customData)
                .setMuted(true)
                .setVolume(2)
                .setUseSurfaceView(true)
                .setExtraObject("key1", "value1")
                .setTestSlotId("111")
                .setDownloadType(2)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .build();

        Assert.assertEquals(1, adSlotFullVideo.getOrientation());
        Assert.assertEquals("金币", adSlotFullVideo.getRewardName());
        Assert.assertEquals(3, adSlotFullVideo.getRewardAmount());
        Assert.assertEquals(customData, adSlotFullVideo.getCustomData());
        Assert.assertEquals(true, adSlotFullVideo.isMuted());
//        Assert.assertEquals(2, adSlotFullVideo.getVolume());
        Assert.assertEquals(true, adSlotFullVideo.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotFullVideo.getParams().get("key1"));
        Assert.assertEquals("111", adSlotFullVideo.getTestSlotId());
        Assert.assertEquals(true, adSlotFullVideo.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotFullVideo.getScenarioId());
    }

    @Test
    public void testRewardAdslot() {
        Map<String, String> customData = new HashMap<>();
        GMAdSlotRewardVideo adSlotRewardVideo = new GMAdSlotRewardVideo.Builder()
                .setOrientation(1)
                .setRewardName("金币")
                .setRewardAmount(3)
                .setCustomData(customData)
                .setMuted(true)
                .setVolume(2)
                .setUseSurfaceView(true)
                .setExtraObject("key1", "value1")
                .setTestSlotId("111")
                .setDownloadType(2)
                .setBidNotify(true)
                .setScenarioId("scenarioId")
                .build();

        Assert.assertEquals(1, adSlotRewardVideo.getOrientation());
        Assert.assertEquals("金币", adSlotRewardVideo.getRewardName());
        Assert.assertEquals(3, adSlotRewardVideo.getRewardAmount());
        Assert.assertEquals(customData, adSlotRewardVideo.getCustomData());
        Assert.assertEquals(true, adSlotRewardVideo.isMuted());
//        Assert.assertEquals(2, adSlotRewardVideo.getVolume());
        Assert.assertEquals(true, adSlotRewardVideo.isUseSurfaceView());
        Assert.assertEquals("value1", adSlotRewardVideo.getParams().get("key1"));
        Assert.assertEquals("111", adSlotRewardVideo.getTestSlotId());
        Assert.assertEquals(true, adSlotRewardVideo.isBidNotify());
        Assert.assertEquals("scenarioId", adSlotRewardVideo.getScenarioId());
    }


    @Test
    public void TestTTCollectionUtils() {
        List<Object> empty = new ArrayList<>();
        List<Object> empty_null = null;
        List<Object> oneList = new ArrayList<>();
        oneList.add(null);
        oneList.add("2222");
        List<Object> twoList = new ArrayList<>();
        twoList.add("1111");
        twoList.add("2222");
        Assert.assertEquals(true, TTCollectionUtils.isEmpty(empty));
        Assert.assertEquals(true, TTCollectionUtils.isEmpty(empty_null));
        Assert.assertEquals(false, TTCollectionUtils.isEmpty(oneList));
        Assert.assertEquals(true, TTCollectionUtils.isFirstEmpty(empty));
        Assert.assertEquals(true, TTCollectionUtils.isFirstEmpty(empty_null));
        Assert.assertEquals(true, TTCollectionUtils.isFirstEmpty(oneList));
        Assert.assertEquals(false, TTCollectionUtils.isFirstEmpty(twoList));
    }

    @Test
    public void TestTTSortUtil() {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(1);
        list.add(2);
        list.add(7);
        list.add(0);
        TTSortUtil.sort(list);

        Assert.assertEquals(0, list.get(0).intValue());
        Assert.assertEquals(1, list.get(1).intValue());
        Assert.assertEquals(2, list.get(2).intValue());
        Assert.assertEquals(3, list.get(3).intValue());
        Assert.assertEquals(7, list.get(4).intValue());

        List<TestModel> listPoint = new ArrayList<>();
        listPoint.add(new TestModel(99));
        listPoint.add(new TestModel(76));
        listPoint.add(new TestModel(85));
        listPoint.add(new TestModel(90));
        listPoint.add(new TestModel(60));
        TTSortUtil.sort(listPoint, createComparator());
        Assert.assertEquals(60, listPoint.get(0).point);
        Assert.assertEquals(76, listPoint.get(1).point);
        Assert.assertEquals(85, listPoint.get(2).point);
        Assert.assertEquals(90, listPoint.get(3).point);
        Assert.assertEquals(99, listPoint.get(4).point);

    }


    private Comparator<TestModel> createComparator() {
        return new Comparator<TestModel>() {
            @Override
            public int compare(TestModel ad1, TestModel ad2) {//降序排序
                if (ad1.point > ad2.point) {
                    return 1;
                } else if (ad1.point < ad2.point) {
                    return -1;
                }

                return 0;
            }
        };
    }

    class TestModel {
        public int point;

        public TestModel(int point) {
            this.point = point;
        }
    }
}
