package com.highjump.medicaldevice;

import android.os.Bundle;

public class ConfigActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // 初始化toolbar
        initToolbar();
    }
}
