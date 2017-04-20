package com.highjump.medicaldevice;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.highjump.medicaldevice.adapter.StatUseDeviceAdapter;
import com.highjump.medicaldevice.adapter.StatUseUserAdapter;
import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.Device;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StatisticsUseActivity extends BaseActivity {

    private SwipeRefreshLayout mRefreshLayoutDevice;
    private SwipeRefreshLayout mRefreshLayoutUser;

    private int mnCurrentCountDevice = 0;
    private int mnTotalCountDevice;
    private boolean mbNeedMoreDevice = false;

    private int mnCurrentCountUser = 0;
    private int mnTotalCountUser;
    private boolean mbNeedMoreUser = false;

    // 调用服务状态
    protected String mstrApiErrDevce = "";
    protected String mstrApiErrUser = "";

    private StatUseDeviceAdapter mAdapterDevice;
    private StatUseUserAdapter mAdapterUser;

    ArrayList<Device> maryDataDevice = new ArrayList<Device>();
    ArrayList<User> maryDataUser = new ArrayList<User>();

    private TabHost mTabHost;
    private TextView mTextCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_use);

        // 初始化toolbar
        initToolbar();

        // 初始化tab
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("device").setIndicator("设备").setContent(R.id.swiperefresh_device));
        mTabHost.addTab(mTabHost.newTabSpec("user").setIndicator("会员").setContent(R.id.swiperefresh_user));

        // 初始化列表
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // 设备
        mRefreshLayoutDevice = (SwipeRefreshLayout)findViewById(R.id.swiperefresh_device);
        mRefreshLayoutDevice.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 获取数据
                getData(0, true);
                stopRefresh(0);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_device);
        recyclerView.setLayoutManager(layoutManager);

        mAdapterDevice = new StatUseDeviceAdapter(this, maryDataDevice);
        recyclerView.setAdapter(mAdapterDevice);

        layoutManager = new LinearLayoutManager(this);

        // 会员
        mRefreshLayoutUser = (SwipeRefreshLayout)findViewById(R.id.swiperefresh_user);
        mRefreshLayoutUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 获取数据
                getData(1, true);
                stopRefresh(1);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list_user);
        recyclerView.setLayoutManager(layoutManager);

        mAdapterUser = new StatUseUserAdapter(this, maryDataUser);
        recyclerView.setAdapter(mAdapterUser);

        // 获取数据
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDeviceDataWithProgress(true, false);
            }
        }, 500);

        // 初始化控件
        mTextCount = (TextView) findViewById(R.id.text_count);
    }

    /**
     * 获取历史信息
     * @param bAnimation 是否需要加载表示
     * @param bRefresh 重新加载
     */
    public void getDeviceDataWithProgress(boolean bAnimation, boolean bRefresh) {
        // 标识加载
        if (bAnimation) {
            if (!mRefreshLayoutDevice.isRefreshing()) {
                mRefreshLayoutDevice.setRefreshing(true);
            }
        }

        getData(0, bRefresh);
        getData(1, bRefresh);
    }

    /**
     * 获取数据
     * @param type 使用列表类型
     *             0: 设备
     *             1: 会员
     * @param bRefresh 是否刷新
     */
    public void getData(final int type, final boolean bRefresh) {

        if (bRefresh) { // refreshing
            if (type == 0) {
                mnCurrentCountDevice = 0;
                mbNeedMoreDevice = false;
            }
            else {
                mnCurrentCountUser = 0;
                mbNeedMoreUser = false;
            }
        }

        // 清空状态
        setErrorMessage(type, "");

        // 调用相应的API
        APIManager.getInstance().getUseList(
                User.currentUser(null),
                type,
                getCurrentCount(type) / Config.PAGE_SIZE,
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {

                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopRefresh(type);
                                Toast.makeText(StatisticsUseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (!response.isSuccessful()) {
                            // 失败
                            setErrorMessage(type, response.message());
                        }
                        else {
                            try {
                                // 获取返回数据
                                ApiResponse resultObj = new ApiResponse(response.body().string());

                                if (!resultObj.isSuccess()) {
                                    setErrorMessage(type, "获取失败");
                                }
                                else {
                                    // 设备
                                    if (type == 0) {
                                        // 清空原有的数据
                                        if (bRefresh) {
                                            maryDataDevice.clear();
                                        }

                                        JSONObject jsonRes = resultObj.getResult();
                                        // 总数量
                                        mnTotalCountDevice = jsonRes.getInt("usageTotal");

                                        // 记录
                                        JSONArray aryJson = jsonRes.getJSONArray("usages");
                                        for (int i = 0; i < aryJson.length(); i++) {
                                            JSONObject jsonObj = aryJson.getJSONObject(i);
                                            Device newDevice = new Device(jsonObj);
                                            maryDataDevice.add(newDevice);
                                        }

                                        // 决定有没有加载更多
                                        mbNeedMoreDevice = (aryJson.length() == Config.PAGE_SIZE);

                                        mnCurrentCountDevice = maryDataDevice.size();
                                    }
                                    // 会员
                                    else {
                                        // 清空原有的数据
                                        if (bRefresh) {
                                            maryDataUser.clear();
                                        }

                                        JSONObject jsonRes = resultObj.getResult();
                                        // 总数量
                                        mnTotalCountUser = jsonRes.getInt("usageTotal");

                                        // 记录
                                        JSONArray aryJson = jsonRes.getJSONArray("usages");
                                        for (int i = 0; i < aryJson.length(); i++) {
                                            JSONObject jsonObj = aryJson.getJSONObject(i);
                                            User newUser = new User(jsonObj);
                                            maryDataUser.add(newUser);
                                        }

                                        // 决定有没有加载更多
                                        mbNeedMoreUser = (aryJson.length() == Config.PAGE_SIZE);

                                        mnCurrentCountUser = maryDataUser.size();
                                    }
                                }
                            } catch (Exception e) {
                                // 解析失败
                                setErrorMessage(type, Config.STR_PARSE_FAIL);
                            }

                            response.close();
                        }

                        // 更新界面，UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mTabHost.getCurrentTab() == type) {

                                    stopRefresh(type);

                                    // 出现了错误，直接退出
                                    String strError = getErrorMessage(type);
                                    if (!strError.isEmpty()) {
                                        Toast.makeText(StatisticsUseActivity.this, strError, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // 总数量
                                    mTextCount.setText(String.valueOf(getTotalCount(type)));
                                }

                                // 更新列表
                                if (type == 0) {
                                    mAdapterDevice.setNeedMore(mbNeedMoreDevice);
                                    mAdapterDevice.notifyDataSetChanged();
                                }
                                else {
                                    mAdapterUser.setNeedMore(mbNeedMoreUser);
                                    mAdapterUser.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
        );
    }

    /**
     * 设置错误信息
     * @param type 使用列表类型
     *             0: 设备
     *             1: 会员
     * @param value 内容
     */
    private void setErrorMessage(int type, String value) {
        if (type == 0) {
            mstrApiErrDevce = value;
        }
        else {
            mstrApiErrUser = value;
        }
    }

    /**
     * 获取错误信息
     * @param type 类型
     * @return
     */
    private String getErrorMessage(int type) {
        if (type == 0) {
            return mstrApiErrDevce;
        }

        return mstrApiErrUser;
    }

    /**
     * 获取当前数量
     * @param type 类型
     * @return
     */
    private int getCurrentCount(int type) {
        if (type == 0) {
            return mnCurrentCountDevice;
        }

        return mnCurrentCountUser;
    }

    /**
     * 获取当前数量
     * @param type 类型
     * @return
     */
    private int getTotalCount(int type) {
        if (type == 0) {
            return mnTotalCountDevice;
        }

        return mnTotalCountDevice;
    }

    /**
     * 停止滚动显示
     * @param type 类型
     */
    private void stopRefresh(int type) {
        if (type == 0) {
            mRefreshLayoutDevice.setRefreshing(false);
        }
        else {
            mRefreshLayoutUser.setRefreshing(false);
        }
    }
}
