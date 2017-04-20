package com.highjump.medicaldevice;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class BaseActivity extends AppCompatActivity {

    /**
     * 初始化toolbar
     */
    protected void initToolbar() {
        // 初始化toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置back图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // 点击back图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * 从网址获取参数
     * @param url
     * @param param
     * @return
     */
    protected String getParamFomeUrl(String url, String param) {
        String value = "";

        int startindex = url.indexOf(param + "=");
        startindex += (param.length() + 1);

        String subString = url.substring(startindex);
        int endindex = subString.indexOf("&");

        if (endindex == -1) {
            value = subString;
        } else {
            value = subString.substring(0, endindex);
        }
        return value;
    }
}
