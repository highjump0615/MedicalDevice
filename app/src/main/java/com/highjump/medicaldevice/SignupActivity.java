package com.highjump.medicaldevice;

import android.os.Bundle;

public class SignupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 初始化toolbar
        initToolbar();
    }
}
