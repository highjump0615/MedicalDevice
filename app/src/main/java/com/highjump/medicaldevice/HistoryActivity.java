package com.highjump.medicaldevice;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.highjump.medicaldevice.adapter.MedicalHistoryAdapter;

public class HistoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mRefreshLayout;

    protected TextView mTextCount;

    protected int mnCurrentCount = 0;
    protected int mnTotalCount;
    protected boolean mbNeedMore = false;

    // 调用服务状态
    protected String mstrApiErr = "";

    /**
     * 初始化列表
     */
    protected void initList() {
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mRefreshLayout.setOnRefreshListener(this);

        // 初始化控件
        mTextCount = (TextView) findViewById(R.id.text_count);
    }

    @Override
    public void onRefresh() {
    }

    protected void stopRefresh() {
        mRefreshLayout.setRefreshing(false);
    }
}
