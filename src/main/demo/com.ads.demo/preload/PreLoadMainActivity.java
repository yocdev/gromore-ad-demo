package com.ads.demo.preload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IdRes;
import android.view.View;
import android.view.WindowManager;

import com.header.app.untext.R;

/**
 * Create by yds on 2022-03-12.
 */
public class PreLoadMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_preload_main);
        bindButton(R.id.pre_load_banner, PreLoadBannerActivity.class);
        bindButton(R.id.pre_load_full_video, PreLoadFullVideoActivity.class);
        bindButton(R.id.pre_load_interstitial, PreLoadInterstitialActivity.class);
        bindButton(R.id.pre_load_reward, PreLoadRewardVideoActivity.class);
        bindButton(R.id.pre_load_interstitial_full, PreLoadInterstitialFullActivity.class);
        bindButton(R.id.pre_load_feed, PreLoadFeedMainActivity.class);

    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreLoadMainActivity.this, clz));
            }
        });

    }
}
