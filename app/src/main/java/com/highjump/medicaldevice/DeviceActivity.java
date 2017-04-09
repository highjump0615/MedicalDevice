package com.highjump.medicaldevice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mLayoutDialogStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // 初始化toolbar
        initToolbar();

        // 打开确认启动对话框
        mLayoutDialogStart = (RelativeLayout)findViewById(R.id.layout_dialog_start);
        Button button = (Button)findViewById(R.id.but_cancel);
        button.setOnClickListener(this);
    }

    private void showDialogView(boolean show, View view) {

        AlphaAnimation animAlpha;

        if (show) {
            animAlpha = new AlphaAnimation(0f, 1f);
            view.setVisibility(View.VISIBLE);
        }
        else {
            animAlpha = new AlphaAnimation(1f, 0f);
            view.setVisibility(View.GONE);
        }

        animAlpha.setDuration(200);
        view.startAnimation(animAlpha);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_start:
                break;

            case R.id.but_cancel:
                showDialogView(false, mLayoutDialogStart);
                break;
        }
    }
}
