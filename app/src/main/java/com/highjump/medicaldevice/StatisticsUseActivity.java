package com.highjump.medicaldevice;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TabHost;

import com.highjump.medicaldevice.adapter.StatUseDeviceAdapter;
import com.highjump.medicaldevice.adapter.StatUseUserAdapter;
import com.highjump.medicaldevice.adapter.StatUserAdapter;

public class StatisticsUseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_use);

        // 初始化toolbar
        initToolbar();

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("device").setIndicator("设备").setContent(R.id.listdevice));
        tabHost.addTab(tabHost.newTabSpec("user").setIndicator("设备").setContent(R.id.listuser));

        // 初始化列表
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listdevice);
        recyclerView.setLayoutManager(layoutManager);

        StatUseDeviceAdapter adapter = new StatUseDeviceAdapter();
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.listuser);
        recyclerView.setLayoutManager(layoutManager);

        StatUseUserAdapter adapterUser = new StatUseUserAdapter();
        recyclerView.setAdapter(adapterUser);
    }
}
