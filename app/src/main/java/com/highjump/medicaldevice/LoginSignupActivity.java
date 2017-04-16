package com.highjump.medicaldevice;

import android.app.ProgressDialog;
import android.widget.EditText;

import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginSignupActivity extends BaseActivity {

    protected EditText mEditUsername;
    protected EditText mEditPassword;

    // 用户名
    protected String mstrUsername;
    // 密码
    protected String mstrPassword;

    protected ProgressDialog mProgressDialog;

    /**
     * 登录
     */
    protected void doLogin() {

        mProgressDialog = ProgressDialog.show(this, "", "正在登录...");

        // 调用相应的API
        APIManager.getInstance().userLogin(
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
                                CommonUtils.createErrorAlertDialog(LoginSignupActivity.this, Config.STR_CONNET_FAIL, e.getMessage()).show();
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
                                    CommonUtils.createErrorAlertDialog(LoginSignupActivity.this, "登录失败", response.message()).show();

                                    response.close();
                                    return;
                                }

                                try {
                                    // 获取返回数据
                                    ApiResponse resultObj = new ApiResponse(response.body().string());

                                    if (!resultObj.isSuccess()) {
                                        CommonUtils.createErrorAlertDialog(LoginSignupActivity.this, "登录失败").show();

                                        response.close();
                                        return;
                                    }

                                    // 设置当前用户
                                    new User(mstrUsername, resultObj.getResult());

                                    // 跳转到主页面
                                    CommonUtils.moveNextActivity(LoginSignupActivity.this, MainActivity.class, false);
                                }
                                catch (Exception e) {
                                    // 解析失败
                                    CommonUtils.createErrorAlertDialog(LoginSignupActivity.this, Config.STR_PARSE_FAIL, e.getMessage()).show();
                                }

                                response.close();
                            }
                        });
                    }
                }
        );
    }
}
