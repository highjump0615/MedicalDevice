package com.highjump.medicaldevice;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.highjump.medicaldevice.adapter.MedicalHistoryAdapter;
import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.TreatLog;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MedicalHistoryActivity extends HistoryActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MedicalHistoryAdapter mAdapter;

    private TextView mTextCount;

    // 调用服务状态
    private String mstrApiErr = "";

    ArrayList<TreatLog> maryData = new ArrayList<TreatLog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // 初始化toolbar
        initToolbar();

        // 初始化控件
        mTextCount = (TextView) findViewById(R.id.text_count);

        // 初始化列表
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MedicalHistoryAdapter(this, maryData);
        mRecyclerView.setAdapter(mAdapter);

        initSwipeRefresh();

        // 获取数据
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataWithProgress(true, false);
            }
        }, 500);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();

        // 获取数据
        getData(true);
    }

    /**
     * 获取历史信息
     * @param bAnimation 是否需要加载表示
     * @param bRefresh 重新加载
     */
    public void getDataWithProgress(boolean bAnimation, boolean bRefresh) {
        // 标识加载
        if (bAnimation) {
            if (!mRefreshLayout.isRefreshing()) {
                mRefreshLayout.setRefreshing(true);
            }
        }

        getData(bRefresh);
    }

    /**
     * 获取数据
     * @param bRefresh 是否刷新
     */
    public void getData(final boolean bRefresh) {

        if (bRefresh) { // refreshing
            mnCurrentCount = 0;
            mbNeedMore = false;
        }

        // 清空状态
        mstrApiErr = "";

        // 调用相应的API
        APIManager.getInstance().getTreatHistory(
                User.currentUser(null),
                mnCurrentCount / Config.PAGE_SIZE,
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {

                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopRefresh();
                                Toast.makeText(MedicalHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (!response.isSuccessful()) {
                            // 失败
                            mstrApiErr = response.message();
                        }
                        else {
                            try {
                                // 获取返回数据
                                ApiResponse resultObj = new ApiResponse(response.body().string());

                                if (!resultObj.isSuccess()) {
                                    mstrApiErr = "获取失败";
                                }
                                else {
                                    // 清空原有的数据
                                    if (bRefresh) {
                                        maryData.clear();
                                    }

                                    JSONObject jsonRes = resultObj.getResult();
                                    // 总数量
                                    mnTotalCount = jsonRes.getInt("deviceUsage");

                                    // 记录
                                    JSONArray aryJson = jsonRes.getJSONArray("usageRecords");
                                    for (int i = 0; i < aryJson.length(); i++) {
                                        JSONObject jsonObj = aryJson.getJSONObject(i);
                                        TreatLog newLog = new TreatLog(jsonObj);
                                        maryData.add(newLog);
                                    }

                                    // 决定有没有加载更多
                                    mbNeedMore = (aryJson.length() == Config.PAGE_SIZE);

                                    mnCurrentCount = maryData.size();
                                }
                            } catch (Exception e) {
                                // 解析失败
                                mstrApiErr = Config.STR_PARSE_FAIL;
                            }

                            response.close();
                        }

                        // 更新界面，UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopRefresh();

                                // 出现了错误，直接退出
                                if (!mstrApiErr.isEmpty()) {
                                    Toast.makeText(MedicalHistoryActivity.this, mstrApiErr, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // 总数量
                                mTextCount.setText(String.valueOf(mnTotalCount));

                                // 更新列表
                                mAdapter.setNeedMore(mbNeedMore);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
        );
    }
}
