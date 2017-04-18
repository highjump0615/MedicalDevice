package com.highjump.medicaldevice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTextUserName;
    private TextView mTextName;
    private TextView mTextIdcode;
    private TextView mTextNotice;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        // 初始化toolbar
        initToolbar();

        // 初始化控件
        Button button = (Button) findViewById(R.id.but_edit_info);
        button.setOnClickListener(this);

        button = (Button) findViewById(R.id.but_edit_pwd);
        button.setOnClickListener(this);

        mTextUserName = (TextView) findViewById(R.id.text_username);
        mTextName = (TextView) findViewById(R.id.text_name);
        mTextIdcode = (TextView) findViewById(R.id.text_idcode);
        mTextNotice = (TextView) findViewById(R.id.text_notice);

        mCurrentUser = User.currentUser(null);

        // 如果没获取到用户信息，调用获取服务
        if (!mCurrentUser.isFetched()) {
            getUserInfo();
        }
    }

    /**
     * 获取用户信息
     */

    public void getUserInfo() {
        // 调用相应的API
        APIManager.getInstance().getUserInfo(
                mCurrentUser.getId(),
                mCurrentUser.getLoginId(),
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
                                        mTextNotice.setText("获取失败");

                                        response.close();
                                        return;
                                    }

                                    // 补充用户信息
                                    JSONObject jsonRes = resultObj.getResult();

                                    mCurrentUser.setName(jsonRes.getString("name"));
                                    mCurrentUser.setIdCode(jsonRes.getString("idCode"));

                                    // 显示更新
                                    updateUserInfo();
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

    @Override
    protected void onResume() {
        super.onResume();

        updateUserInfo();
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo() {
        mTextUserName.setText(mCurrentUser.getUsername());
        mTextName.setText(mCurrentUser.getName());
        mTextIdcode.setText(mCurrentUser.getIdCode());
    }

    @Override
    public void onClick(View v) {
        int nId = v.getId();

        switch (nId) {
            case R.id.but_edit_info:
                // 跳转到修改会员信息页面
                CommonUtils.moveNextActivity(UserInfoActivity.this, UserInfoEditActivity.class, false, false);
                break;

            case R.id.but_edit_pwd:
                // 跳转到修改密码页面
                CommonUtils.moveNextActivity(UserInfoActivity.this, UserInfoPasswordActivity.class, false, false);
                break;
        }
    }
}
