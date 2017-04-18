package com.highjump.medicaldevice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoEditActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditName;
    private EditText mEditIdcode;
    private TextView mTextNotice;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_edit);

        // 初始化toolbar
        initToolbar();

        // 初始化控件
        EditText editUsername = (EditText) findViewById(R.id.edit_username);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditIdcode = (EditText) findViewById(R.id.edit_idcode);

        Button button = (Button) findViewById(R.id.but_confirm);
        button.setOnClickListener(this);
        button = (Button) findViewById(R.id.but_cancel);
        button.setOnClickListener(this);

        mTextNotice = (TextView) findViewById(R.id.text_notice);

        mCurrentUser = User.currentUser(null);

        editUsername.setText(mCurrentUser.getUsername());
        mEditName.setText(mCurrentUser.getName());
        mEditIdcode.setText(mCurrentUser.getIdCode());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_confirm:
                saveUserInfo();
                break;

            case R.id.but_cancel:
                onBackPressed();
                break;
        }
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {
        mTextNotice.setText("正在保存...");

        // 更新当前用户
        mCurrentUser.setName(mEditName.getText().toString());
        mCurrentUser.setIdCode(mEditIdcode.getText().toString());

        // 调用相应的API
        APIManager.getInstance().saveUserInfo(
                mCurrentUser,
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextNotice.setText(e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // 失败
                                if (!response.isSuccessful()) {
                                    mTextNotice.setText(response.message());

                                    response.close();
                                    return;
                                }

                                try {
                                    // 获取返回数据
                                    ApiResponse resultObj = new ApiResponse(response.body().string());

                                    if (!resultObj.isSuccess()) {
                                        mTextNotice.setText("保存失败");

                                        response.close();
                                        return;
                                    }

                                    // 返回
                                    onBackPressed();
                                }
                                catch (Exception e) {
                                    // 解析失败
                                    mTextNotice.setText(Config.STR_PARSE_FAIL);
                                }

                                response.close();
                            }
                        });
                    }
                }
        );
    }
}
