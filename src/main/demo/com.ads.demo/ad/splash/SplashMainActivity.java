package com.ads.demo.ad.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.header.app.untext.R;

/**
 * splash广告使用示例，具体广告逻辑参考SplashActivity。
 */
public class SplashMainActivity extends Activity {
    public static final String KEY_AD_UNIT_ID = "ad_unit_id";

    private Button mButtonDownloadShow;
    private TextView mTvAdUnitId;
    private String mAdUnitId;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_main);

        mTvAdUnitId = findViewById(R.id.tv_ad_unit_id);
        mAdUnitId = getResources().getString(R.string.splash_unit_id);
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));

        radioGroup = findViewById(R.id.radio_group);
        initRadioGroup();

        mButtonDownloadShow = findViewById(R.id.btn_download_show);
        mButtonDownloadShow.setOnClickListener(v -> {
            Intent intent = new Intent(SplashMainActivity.this, SplashActivity.class);
            intent.putExtra(KEY_AD_UNIT_ID, mAdUnitId);
            SplashMainActivity.this.startActivity(intent);
        });
    }

    private void initRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_normal) {
                    mAdUnitId = getResources().getString(R.string.splash_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                } else if (checkedId == R.id.radio_bidding) {
                    mAdUnitId = getResources().getString(R.string.splash_bidding_unit_id);
                    mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                }
            }
        });
    }
}
