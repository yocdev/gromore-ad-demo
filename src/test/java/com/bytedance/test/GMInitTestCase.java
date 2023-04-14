package com.bytedance.test;

import com.bytedance.msdk.api.v2.GMAdConfig;
import com.bytedance.msdk.api.v2.GMBaiduOption;
import com.bytedance.msdk.api.v2.GMConfigUserInfoForSegment;
import com.bytedance.msdk.api.v2.GMGdtOption;
import com.bytedance.msdk.api.v2.GMPangleOption;
import com.bytedance.msdk.api.v2.GMPrivacyConfig;
import com.google.common.truth.Truth;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GMInitTestCase {
    @Test
    public void testGMAdConfig() {
        GMAdConfig config = new GMAdConfig.Builder()
                .setAppId("5001121")
                .setAppName("APP测试媒体")
                .setDebug(true)
                .setPublisherDid("11111")
                .setOpenAdnTest(true)
                .setLocalExtra(null)
                .setHttps(true)
                .setIsOpenPangleCustom(true)
                .setCustomLocalConfig(null)
                .setGroMoreExtra(null)
                .build();

        Truth.assertThat(config.getAppId()).isEqualTo("5001121");
        Truth.assertThat(config.getAppName()).isEqualTo("APP测试媒体");
        Truth.assertThat(config.isDebug()).isEqualTo(true);
        Truth.assertThat(config.getPublisherDid()).isEqualTo("11111");
        Truth.assertThat(config.isOpenAdnTest()).isEqualTo(true);
        Truth.assertThat(config.getLocalExtra()).isEqualTo(null);
        Truth.assertThat(config.isHttps()).isEqualTo(true);
        Truth.assertThat(config.isOpenPangleCustom()).isEqualTo(true);
        Truth.assertThat(config.getCutstomLocalConfig()).isEqualTo(null);
        Truth.assertThat(config.getGromoreExtra()).isEqualTo(null);
    }

    //---------------------------------

    @Test
    public void testGMPangleOption() {
        GMPangleOption option = getGMPangleOption();

        Truth.assertThat(option.isPaid()).isEqualTo(true);
        Truth.assertThat(option.getTitleBarTheme()).isEqualTo(1);
        Truth.assertThat(option.isAllowShowNotify()).isEqualTo(true);
        Truth.assertThat(option.isAllowShowPageWhenScreenLock()).isEqualTo(true);
        Truth.assertThat(option.getDirectDownloadNetworkType()).isEqualTo(new int[]{1,2,3});
        Truth.assertThat(option.isIsUseTextureView()).isEqualTo(true);
        Truth.assertThat(option.getNeedClearTaskReset()).isEqualTo(new String[]{"aa","bb"});
        Truth.assertThat(option.getData()).isEqualTo("111111");
        Map<String,String> extraData = option.getExtraData();
        Truth.assertThat(extraData.get("key1")).isEqualTo("value1");
        Truth.assertThat(extraData.get("key2")).isEqualTo("value2");
        Truth.assertThat(extraData.get("key3")).isEqualTo("value3");
        Truth.assertThat(option.getKeywords()).isEqualTo("keywords");
    }

    private GMPangleOption getGMPangleOption(){
        int[] netWortType = new int[]{1,2,3};
        String[] taskReset =new String[]{"aa","bb"};
        Map<String,String> mapData = new HashMap<>();
        mapData.put("key1","value1");
        mapData.put("key2","value2");

        return new GMPangleOption.Builder()
                .setIsPaid(true)
                .setTitleBarTheme(1)
                .setAllowShowNotify(true)
                .setAllowShowPageWhenScreenLock(true)
                .setDirectDownloadNetworkType(netWortType)
                .setIsUseTextureView(true)
                .setNeedClearTaskReset(taskReset)
                .setData("111111")
                .setData(mapData)
                .setData("key3","value3")
                .setKeywords("keywords")
                .build();
    }

    //---------------------------------

    @Test
    public void testGMGdtOption() {

    }

    private GMGdtOption getGMGdtOption(){
        return null;
    }


    //---------------------------------


    public void testGMBaiduOption() {

    }

    private GMBaiduOption getGMBaiduOption(){
        return null;
    }

    //---------------------------------



    public void testGMConfigUserInfoForSegment(){

    }

    private GMConfigUserInfoForSegment getGMConfigUserInfoForSegment(){
        return null;
    }

    //---------------------------------



    public void testGMPrivacyConfig(){

    }

    private GMPrivacyConfig getGMPrivacyConfig(){
        return  null;
    }

    //---------------------------------

}
