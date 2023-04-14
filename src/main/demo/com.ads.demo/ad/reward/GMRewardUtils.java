package com.ads.demo.ad.reward;

import android.text.TextUtils;
import android.util.Log;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;

import java.util.HashMap;
import java.util.Map;

public class GMRewardUtils {

    private static final String TAG = "TMeRewardUtils";

    /**
     * 激励视频服务端验证，GroMore会把设置的字符串透传给相应的ADN
     */
    public static Map<String, String> getCustomData() {
        // adn的服务端奖励验证，customData数据会分别透传给各家adn。
        Map<String, String> customData = new HashMap<>();
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_PANGLE, "pangle media_extra");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, "gdt custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_KS, "ks custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_SIGMOB, "sigmob custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_MINTEGRAL, "mintegral custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_BAIDU, "baidu custom data");

        // 如果开启了gromre服务端激励验证，可以传以下信息，跟adn无关。
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GROMORE_EXTRA, "gromore serverside verify extra data"); // 会透传给媒体的服务器

        return customData;
    }

    /**
     * 奖励验证回调
     */
    public static void processRewardVerify(RewardItem rewardItem) {
        Map<String, Object> customData = rewardItem.getCustomData();
        if (customData != null) {
            // 首先判断是否启用了GroMore的服务端验证
            Boolean isGroMoreServerSideVerify = (Boolean) customData.get(RewardItem.KEY_IS_GROMORE_SERVER_SIDE_VERIFY);
            if (isGroMoreServerSideVerify != null && isGroMoreServerSideVerify) {
                // 开启了GroMore的服务端激励验证，这里可以获取GroMore的服务端激励验证信息
                boolean isVerify = rewardItem.rewardVerify();
                // 如果isVerify=false，则可以根据下面的错误码来判断为什么是false，
                //  1、如果errorCode为40001/40002/50001/50002，则是因为请求异常导致，媒体可以根据自己的判断决定是否发放奖励。
                //  2、否则，就是媒体服务端回传的验证结果是false，此时应该不发放奖励。

                Integer reason = (Integer) customData.get(RewardItem.KEY_REASON);
                if (reason != null) {
                    Logger.d(TAG, "rewardItem，开发者服务器回传的reason，开发者不传时为空");
                }
                Integer errorCode = (Integer) customData.get(RewardItem.KEY_ERROR_CODE);
                if (errorCode != null) {
                    String errorMsg = (String) customData.get(RewardItem.KEY_ERROR_MSG);
                    Logger.d(TAG, "rewardItem, gromore服务端验证异常时的错误信息，未发生异常时为0或20000：errorCode:" + errorCode + ", errMsg: " + errorMsg);
                }
                String gromoreExtra = (String) customData.get(RewardItem.KEY_GROMORE_EXTRA);
                Logger.d(TAG, "rewardItem, 开发者通过AdSlot传入的extra信息，会透传给媒体的服务器。开发者不传时为空，extra:" + gromoreExtra);
                String transId = (String) customData.get(RewardItem.KEY_TRANS_ID);
                Logger.d(TAG, "rewardItem, gromore服务端验证产生的transId，一次广告播放会产生的唯一的transid: " + transId);
            } else {
                // 未开启GroMore的服务端激励验证，这里获取adn的激励验证信息
                String adnName = (String) customData.get(RewardItem.KEY_ADN_NAME);
                if (!TextUtils.isEmpty(adnName)) {
                    switch (adnName) {
                        case RewardItem.KEY_GDT:
                            Logger.d(TAG, "rewardItem gdt: " + customData.get(RewardItem.KEY_GDT_TRANS_ID));
                            break;
                    }
                }
            }
        }
    }

    public static GMRewardedAdListener getRewardPlayAgainListener() {
        //穿山甲再看一次监听
        return new GMRewardedAdListener() {
            /**
             * 广告的展示回调 每个广告仅回调一次
             */
            public void onRewardedAdShow() {
                Log.d(TAG, "onRewardedAdShow---play again");
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            @Override
            public void onRewardedAdShowFail(AdError adError) {
                if (adError == null) {
                    return;
                }
                Log.d(TAG, "onRewardedAdShowFail---play again, errCode: " + adError.code + ", errMsg: " + adError.message);
                // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
            }

            /**
             * 注意Admob的激励视频不会回调该方法
             */
            @Override
            public void onRewardClick() {
                Log.d(TAG, "onRewardClick---play again");
            }

            /**
             * 广告关闭的回调
             */
            public void onRewardedAdClosed() {
                Log.d(TAG, "onRewardedAdClosed---play again");
            }

            /**
             * 视频播放完毕的回调 Admob广告不存在该回调
             */
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete---play again");
            }

            /**
             * 1、视频播放失败的回调
             */
            public void onVideoError() {
                Log.d(TAG, "onVideoError---play again");
            }

            /**
             * 激励视频播放完毕，验证是否有效发放奖励的回调
             */
            public void onRewardVerify(RewardItem rewardItem) {
                Log.d(TAG, "onRewardVerify---play again");
                // 这里是未开启gromore服务端奖励验证的逻辑，如果开启了Gromore服务端奖励验证，请参考GMRewardUtils.processRewardVerify()。
                if (rewardItem != null) {
                    // 根据isRewardVerify来判断是否进行奖励发放
                    boolean isRewardVerify = rewardItem.rewardVerify();
                    Log.d(TAG, "onRewardVerify rewardItem isRewardVerify: " + isRewardVerify);
                }
            }

            /**
             * - Mintegral GDT Admob广告不存在该回调
             */
            @Override
            public void onSkippedVideo() {
                Log.d(TAG, "onSkippedVideo---play again");
            }
        };
    }

}
