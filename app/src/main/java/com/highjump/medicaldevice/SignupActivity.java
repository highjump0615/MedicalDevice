package com.highjump.medicaldevice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignupActivity extends LoginSignupActivity implements View.OnClickListener {

    private EditText mEditCPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 初始化toolbar
        initToolbar();

        // 初始化控件
        mEditUsername = (EditText)findViewById(R.id.edit_username);
        mEditPassword = (EditText)findViewById(R.id.edit_password);
        mEditCPassword = (EditText)findViewById(R.id.edit_cpassword);

        Button button = (Button) findViewById(R.id.but_signup);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_signup:
                doSignup();
                break;
        }
    }

    /**
     * 注册
     */
    private void doSignup() {

        mstrUsername = mEditUsername.getText().toString();
        mstrPassword = mEditPassword.getText().toString();
        String strCPassword = mEditCPassword.getText().toString();

        // 检查输入是否合适
        if (TextUtils.isEmpty(mstrUsername)) {
            CommonUtils.createErrorAlertDialog(this, "请输入手机号码").show();
            return;
        }
        if (TextUtils.isEmpty(mstrPassword)) {
            CommonUtils.createErrorAlertDialog(this, "请输入密码").show();
            return;
        }
        if (!mstrPassword.equals(strCPassword)) {
            CommonUtils.createErrorAlertDialog(this, "确认密码不一致").show();
            return;
        }

        // 如果在登陆过程当中，则退出
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }

        mProgressDialog = ProgressDialog.show(this, "", "正在注册...");

        // 调用相应的API
        APIManager.getInstance().userSignup(
                mstrUsername,
                mstrPassword,
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mProgressDialog.dismiss();

                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.createErrorAlertDialog(SignupActivity.this, Config.STR_CONNET_FAIL, e.getMessage()).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mProgressDialog.dismiss();

                                // 失败
                                if (!response.isSuccessful()) {
                                    CommonUtils.createErrorAlertDialog(SignupActivity.this, "注册失败", response.message()).show();

                                    response.close();
                                    return;
                                }

                                try {
                                    // 获取返回数据
                                    ApiResponse resultObj = new ApiResponse(response.body().string());

                                    if (!resultObj.isSuccess()) {
                                        CommonUtils.createErrorAlertDialog(SignupActivity.this, "注册失败").show();
                                        return;
                                    }
                                }
                                catch (Exception e) {
                                    // 解析失败
                                    CommonUtils.createErrorAlertDialog(SignupActivity.this, Config.STR_PARSE_FAIL, e.getMessage()).show();
                                }

                                response.close();
                            }
                        });
                    }
                }
        );
    }
}
