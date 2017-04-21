package com.highjump.medicaldevice;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditPassword;
    private EditText mEditCPassword;
    private Button mButConfirm;

    // 调用服务状态
    private String mstrApiErr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_pwd);

        // 初始化toolbar
        initToolbar();

        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditCPassword = (EditText) findViewById(R.id.edit_cpassword);

        mButConfirm = (Button) findViewById(R.id.but_confirm);
        mButConfirm.setOnClickListener(this);
        Button button = (Button) findViewById(R.id.but_cancel);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_confirm:
                changePassword();
                break;

            case R.id.but_cancel:
                onBackPressed();
                break;
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {

        String strPassword = mEditPassword.getText().toString();
        String strCPassword = mEditCPassword.getText().toString();

        // 检查输入是否合适
        if (TextUtils.isEmpty(strPassword)) {
            CommonUtils.createErrorAlertDialog(this, "请输入新的密码").show();
            return;
        }
        if (!strPassword.equals(strCPassword)) {
            CommonUtils.createErrorAlertDialog(this, "确认密码不一致").show();
            return;
        }

        // 清空状态
        mstrApiErr = "";

        // 调用相应的API
        APIManager.getInstance().setPassword(
                User.currentUser(null),
                strPassword,
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserInfoPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (!response.isSuccessful()) {
                            // 失败
                            mstrApiErr = response.message();
                        }

                        try {
                            // 获取返回数据
                            ApiResponse resultObj = new ApiResponse(response.body().string());

                            if (!resultObj.isSuccess()) {
                                mstrApiErr = "保存失败";
                            }
                        }
                        catch (Exception e) {
                            // 解析失败
                            mstrApiErr = Config.STR_PARSE_FAIL;
                        }

                        response.close();

                        // 更新界面，UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 出现了错误，直接退出
                                if (!mstrApiErr.isEmpty()) {
                                    mButConfirm.setEnabled(true);
                                    Toast.makeText(UserInfoPasswordActivity.this, mstrApiErr, Toast.LENGTH_SHORT).show();

                                    return;
                                }

                                // 返回
                                onBackPressed();
                            }
                        });
                    }
                }
        );

        mButConfirm.setEnabled(false);
    }
}
