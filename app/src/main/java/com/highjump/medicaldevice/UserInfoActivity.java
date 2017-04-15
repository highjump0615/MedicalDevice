package com.highjump.medicaldevice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.highjump.medicaldevice.utils.CommonUtils;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        // 初始化toolbar
        initToolbar();

        Button button = (Button) findViewById(R.id.but_edit_info);
        button.setOnClickListener(this);

        button = (Button) findViewById(R.id.but_edit_pwd);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int nId = v.getId();

        switch (nId) {
            case R.id.but_edit_info:
                // 跳转到修改会员信息页面
                CommonUtils.moveNextActivity(UserInfoActivity.this, UserInfoEditActivity.class, false);
                break;

            case R.id.but_edit_pwd:
                // 跳转到修改密码页面
                CommonUtils.moveNextActivity(UserInfoActivity.this, UserInfoPasswordActivity.class, false);
                break;
        }
    }
}
