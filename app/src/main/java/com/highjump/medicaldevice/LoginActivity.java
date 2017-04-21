package com.highjump.medicaldevice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.highjump.medicaldevice.utils.CommonUtils;

public class LoginActivity extends LoginSignupActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化toolbar
        initToolbar();

        // 初始化控件
        mEditUsername = (EditText)findViewById(R.id.edit_username);
        mEditPassword = (EditText)findViewById(R.id.edit_password);

        Button button = (Button) findViewById(R.id.but_login);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_login:
                mstrUsername = mEditUsername.getText().toString();
                mstrPassword = mEditPassword.getText().toString();

                // 检查输入是否合适
                if (TextUtils.isEmpty(mstrUsername)) {
                    CommonUtils.createErrorAlertDialog(this, "请输入手机号码").show();
                    return;
                }
                if (TextUtils.isEmpty(mstrPassword)) {
                    CommonUtils.createErrorAlertDialog(this, "请输入密码").show();
                    return;
                }

                doLogin();
                break;
        }
    }
}
