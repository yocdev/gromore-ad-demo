package com.ads.demo.custom.toutiao;

import android.content.Context;
import android.util.Log;

import com.ads.demo.AppConst;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.draw.GMCustomDrawAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotDraw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Usage:
 * Doc:
 */
public class TouTiaoCustomerDraw extends GMCustomDrawAdapter {

    private static final String TAG = AppConst.TAG_PRE + TouTiaoCustomerDraw.class.getSimpleName();

    @Override
    public void load(Context context, GMAdSlotDraw adSlot, GMCustomServiceConfig serviceConfig) {
        if (isNativeAd()) {
            if (isServerBidding()) {
                String adm = getAdm(); //通过该api获取adm
                Object extraDataNoParse = getExtraDataNoParse(); //通过该api获取透传字段
                List<TouTiaoDrawAd> list = new ArrayList<>();
                TouTiaoDrawAd drawAd = new TouTiaoDrawAd();
                list.add(drawAd);
                callLoadSuccess(list); //调用成功回调给
            }
            Log.i(TAG, "自渲染Draw广告");
        } else if (isExpressRender()) {
            Log.i(TAG, "模板Draw广告");
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

    /**
     * 竞价结果回传
     *
     * @param win
     * @param winnerPrice
     * @param loseReason
     * @param extra
     */
    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
        Log.e(TAG, "竞价结果回传：win : " + win + "  winnerPrice : " + winnerPrice + " loseReason : " + loseReason);
    }
}