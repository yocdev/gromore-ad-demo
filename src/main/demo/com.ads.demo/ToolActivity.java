package com.ads.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.TToast;
import com.header.app.untext.R;

/**
 * Create by WUzejian on 2020-09-01.
 */
public class ToolActivity extends Activity {
    public final static String SP_PPE_INFO = "tt_mediation_ppe_info";
    public static final String KEY_PPE_CONTENT = "tt_ppe_content";
    public static final String DEFAULT_PPE = "ppe_union_sdk";

    private static final String KEY_PANGLE_PREVIEW_AD_ID = "tt_pangle_preview_ad_id"; //adid参数
    private static final String KEY_PANGLE_PREVIEW_CREATIVE_ID = "tt_pangle_preview_creative_id"; //creative_id参数
    private static final String KEY_PANGLE_PREVIEW_EXT = "tt_pangle_preview_ext"; // ext参数

    SharedPreferences sp;

    EditText et_ppe;
    TextView ppe_desc;
    Button btn_tool_change;

    LinearLayout ppe_layout;
    LinearLayout imeiLayout;
    TextView imeiTextView;
    LinearLayout oaidLayout;
    TextView oaidTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        et_ppe = findViewById(R.id.et_ppe);
        ppe_desc = findViewById(R.id.ppe_desc);
        btn_tool_change = findViewById(R.id.btn_tool_change);
        ppe_layout = findViewById(R.id.ppe_layout);
        imeiLayout = findViewById(R.id.imei_layout);
        imeiTextView = findViewById(R.id.imei_tv);
        oaidLayout = findViewById(R.id.oaid_layout);
        oaidTextView = findViewById(R.id.oaid_tv);
        btn_tool_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ppe = et_ppe.getText().toString();
                setPPE(ppe);
            }
        });

        findViewById(R.id.theme_status_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GMMediationAdSdk.setThemeStatus(0); //0: 正常模式   1: 夜间模式
            }
        });
        findViewById(R.id.theme_status_neight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GMMediationAdSdk.setThemeStatus(1); //0: 正常模式   1: 夜间模式
            }
        });

        findViewById(R.id.set_pangle_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPanglePreview(); //设置穿山甲预览参数
            }
        });
        findViewById(R.id.clear_pangle_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPanglePreview(); //清除穿山甲预览参数
            }
        });

        showPPE();
        getDeviceId();
        initListener();
    }


    /**
     * 展示msdk中的ppe内容
     */
    private void showPPE() {
        String ppe = null;
        try {
            sp = getSharedPreferences(SP_PPE_INFO, Context.MODE_PRIVATE);
            ppe = sp.getString(KEY_PPE_CONTENT, null);
        } catch (Exception e) {

        }

        //ppe不为空
        if (!TextUtils.isEmpty(ppe)) {
            et_ppe.setText(ppe);
            ppe_desc.setText("已经打开PPE环境，PPE环境为：" + ppe);
        } else {
            ppe_desc.setText("已经关闭PPE环境");
        }
    }

    /**
     * 设置ppe内容
     */
    private void setPPE(String ppe) {
        try {
            if (sp == null) {
                sp = getSharedPreferences(SP_PPE_INFO, Context.MODE_PRIVATE);
            }
            sp.edit().putString(KEY_PPE_CONTENT, ppe).commit();
            //ppe不为空
            if (!TextUtils.isEmpty(ppe)) {
                ppe_desc.setText("已经打开PPE环境，PPE环境为：" + ppe);
            } else {
                ppe_desc.setText("已经关闭PPE环境");
            }
        } catch (Exception e) {

        }
    }


    private void initListener() {
        ppe_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyContentToClipboard(DEFAULT_PPE, getApplicationContext());
                TToast.show(getApplicationContext(), "已复制到粘贴板");
            }
        });
    }

    /**
     * 获取设备id
     */
    private void getDeviceId() {
        final String imei = getImei(this);
        final String oaid = GMMediationAdSdk.getZbh(this);

        if (TextUtils.isEmpty(imei)) {
            imeiTextView.setText("暂无数据");
        } else {
            imeiTextView.setText(imei);
        }

        imeiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(imei)) {
                    TToast.show(getApplicationContext(), "暂无数据");
                } else {
                    copyContentToClipboard(imei, getApplicationContext());
                    TToast.show(getApplicationContext(), "IMEI已复制到粘贴板");
                }

            }
        });

        if (TextUtils.isEmpty(oaid)) {
            oaidTextView.setText("暂无数据");
        } else {
            oaidTextView.setText(oaid);
        }

        oaidLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(oaid)) {
                    TToast.show(getApplicationContext(), "暂无数据");
                } else {
                    copyContentToClipboard(imei, getApplicationContext());
                    TToast.show(getApplicationContext(), "OAID已复制到粘贴板");
                }
            }
        });
    }


    /**
     * 获取IMEI号
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {
        String mImei = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                mImei = telephonyManager.getDeviceId();
            }
        } catch (Throwable e) {

        }
        return mImei;
    }


    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @param context
     */
    public void copyContentToClipboard(String content, Context context) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    /**
     * 设置穿山甲预览
     */
    private void setPanglePreview() {
        try {
            if (sp == null) {
                sp = getSharedPreferences(SP_PPE_INFO, Context.MODE_PRIVATE);
            }

            String adId = "1602438514615316";
            String creativeId = "1602439237360647";
            String ext = "pangle_extra";

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_PANGLE_PREVIEW_AD_ID, adId);
            editor.putString(KEY_PANGLE_PREVIEW_CREATIVE_ID, creativeId);
            editor.putString(KEY_PANGLE_PREVIEW_EXT, ext);
            editor.commit();
            TToast.show(getApplicationContext(), "设置成功");
        } catch (Exception e) {

        }
    }

    /**
     * 清除穿山甲预览
     */
    private void clearPanglePreview() {
        try {
            if (sp == null) {
                sp = getSharedPreferences(SP_PPE_INFO, Context.MODE_PRIVATE);
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_PANGLE_PREVIEW_AD_ID, null);
            editor.putString(KEY_PANGLE_PREVIEW_CREATIVE_ID, null);
            editor.putString(KEY_PANGLE_PREVIEW_EXT, null);
            editor.commit();
            TToast.show(getApplicationContext(), "清除成功");
        } catch (Exception e) {

        }
    }
}
