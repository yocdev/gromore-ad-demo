package com.ads.demo.config;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bytedance.msdk.api.UserInfoForSegment;
import com.bytedance.msdk.api.v2.GMAdConfig;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMConfigUserInfoForSegment;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMPrivacyConfig;

import java.util.HashMap;
import java.util.Map;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class GMAdManagerHolder {
    private static boolean sInit;

    public static void init(Context context) {
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(@NonNull Context context) {
        if (!sInit) {
            GMMediationAdSdk.initialize(context, buildV2Config(context));
            sInit = true;
        }
    }

    /**
     * 初始化配置类
     * 更多配置参数请参考接入文档
     *
     * @param context
     * @return
     */
    public static GMAdConfig buildV2Config(Context context) {
        return new GMAdConfig.Builder()
                /**
                 * 注：需要替换成在媒体平台申请的appID ，切勿直接复制
                 */
                .setAppId("5001121")
                .setAppName("APP测试媒体")
                /**
                 * 上线前需要关闭debug开关，否则会影响性能
                 */
                .setDebug(true)
//                .setConfigUserInfoForSegment(getConfigUserInfo()) //如果您需要配置流量分组信息请参考该api
//                .setPrivacyConfig(getPrivacyConfig()) //如果您需要设置隐私策略请参考该api
                .build();
    }


    /**
     * 如果设置流量分组可参考该方法
     *
     * @return
     */
    private static GMConfigUserInfoForSegment getConfigUserInfo() {
        /**
         * GMConfigUserInfoForSegment设置流量分组的信息
         * 注意：
         * 1、请每次都传入新的info对象
         * 2、字符串类型的值只能是大小写字母，数字，下划线，连字符，字符个数100以内 ( [A-Za-z0-9-_]{1,100} ) ，不符合规则的信息将被过滤掉，不起作用。
         */
        GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();
        userInfo.setUserId("msdk-demo");
        userInfo.setGender(UserInfoForSegment.GENDER_MALE);
        userInfo.setChannel("msdk-channel");
        userInfo.setSubChannel("msdk-sub-channel");
        userInfo.setAge(999);
        userInfo.setUserValueGroup("msdk-demo-user-value-group");

        Map<String, String> customInfos = new HashMap<>();
        customInfos.put("aaaa", "test111");
        customInfos.put("bbbb", "test222");
        userInfo.setCustomInfos(customInfos);
        return userInfo;
    }


    /**
     * 如果设置隐私政策分组可参考该方法,更多隐私api说明请参考接入文档1.6.5章节
     */
    private static GMPrivacyConfig getPrivacyConfig() {
        return new GMPrivacyConfig() {
            // 重写相应的函数，设置需要设置的权限开关，不重写的将采用默认值
            // 例如，重写isCanUsePhoneState函数返回true，表示允许使用ReadPhoneState权限。
            @Override
            public boolean isCanUsePhoneState() {
                return true;
            }

            //当isCanUseWifiState=false时，可传入Mac地址信息，穿山甲sdk使用您传入的Mac地址信息
            @Override
            public String getMacAddress() {
                return "";
            }

            // 设置青少年合规，默认值GMAdConstant.ADULT_STATE.AGE_ADULT为成年人
            @Override
            public GMAdConstant.ADULT_STATE getAgeGroup() {
                return GMAdConstant.ADULT_STATE.AGE_ADULT;
            }
        };
    }


}
