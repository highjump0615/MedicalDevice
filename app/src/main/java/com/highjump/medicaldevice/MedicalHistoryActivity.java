package com.highjump.medicaldevice;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.highjump.medicaldevice.adapter.MedicalHistoryAdapter;

public class MedicalHistoryActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MedicalHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // 初始化toolbar
        initToolbar();

        // 初始化列表
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MedicalHistoryAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
}
