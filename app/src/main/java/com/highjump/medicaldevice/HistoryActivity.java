package com.highjump.medicaldevice;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

public class HistoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout mRefreshLayout;

    protected int mnCurrentCount = 0;
    protected int mnTotalCount;
    protected boolean mbNeedMore = false;

    protected void initSwipeRefresh() {
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
    }

    protected void stopRefresh() {
        mRefreshLayout.setRefreshing(false);
    }
}
