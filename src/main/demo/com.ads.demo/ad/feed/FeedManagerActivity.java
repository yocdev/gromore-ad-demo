package com.ads.demo.ad.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.header.app.untext.R;

public class FeedManagerActivity extends Activity {
    public static final String KEY_AD_UNIT_ID = "ad_unit_id"; //广告位ID

    private RadioGroup mRadioGroup;
    private String mAdUnitId; //广告位
    private TextView mTvAdUnitId;
    private Button singleButton;   //简单接入示例
    private Button listButton; //消息流（ListView）接入示例
    private Button recycleButton; //消息流（RecycleView）接入示例


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_manager_activity);
        mRadioGroup = findViewById(R.id.radio_group);
        mTvAdUnitId = findViewById(R.id.feed_unit_id);
        singleButton = findViewById(R.id.btn_single_ad);
        listButton = findViewById(R.id.btn_list_ad);
        recycleButton = findViewById(R.id.btn_recycle_ad);

        mAdUnitId = getResources().getString(R.string.feed_express_unit_id);
        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
        initRadioGroup();
        clickButton(FeedSimpleActivity.class, singleButton);
        clickButton(FeedListActivity.class, listButton);
        clickButton(FeedRecycleActivity.class, recycleButton);
    }

    private void initRadioGroup() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.open_express_ad:
                        mAdUnitId = getResources().getString(R.string.feed_express_unit_id);
                        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                        break;
                    case R.id.open_native_ad:
                        mAdUnitId = getResources().getString(R.string.feed_native_unit_id);
                        mTvAdUnitId.setText(String.format(getResources().getString(R.string.ad_unit_id), mAdUnitId));
                        break;
                }
            }
        });
    }

    private void clickButton(final Class clz, Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedManagerActivity.this, clz);
                intent.putExtra(KEY_AD_UNIT_ID, mAdUnitId);
                startActivity(intent);
            }
        });
    }

}
