package com.ads.demo;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * 初始化监听
     */
    public abstract void initListener();

    /**
     * 初始化广告加载管理类
     */
    public abstract void initAdLoader();
}
