package com.ads.demo.custom.toutiao;

import android.content.Context;
import android.util.Log;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YLH 信息流广告自定义Adapter
 */
public class TouTiaoCustomerNative extends GMCustomNativeAdapter {

    private static final String TAG = AppConst.TAG_PRE + TouTiaoCustomerNative.class.getSimpleName();

    @Override
    public void load(Context context, GMAdSlotNative adSlot, GMCustomServiceConfig serviceConfig) {
        if (isNativeAd()) {
            Log.i(TAG, "自渲染_native");
            if (isServerBidding()) {
                String adm = getAdm(); //通过该api获取adm
                Object extraDataNoParse = getExtraDataNoParse(); //通过该api获取透传字段
                List<TouTiaoNativeAd> list = new ArrayList<>();
                TouTiaoNativeAd nativeAd = new TouTiaoNativeAd(null);
                list.add(nativeAd);
                callLoadSuccess(list); //调用成功回调给
//                callLoadFail(new GMCustomAdError(22,"自定义errorMsg"));  //通过广告加载失败，调用该api
            }
        } else if (isExpressRender()) {
            Log.i(TAG, "模板_native");
        } else {
            Log.i(TAG, "其他类型");
        }
    }


    /**
     * 是否clientBidding广告
     *
     * @return
     */
    public boolean isClientBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }

    /**
     * 是否serverBidding广告
     *
     * @return
     */
    public boolean isServerBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_SERVER_BIDING;
    }

    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
        Log.e(TAG, "竞价结果回传：win : " + win + "  winnerPrice : " + winnerPrice + " loseReason : " + loseReason);
    }
}
