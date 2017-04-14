package com.highjump.medicaldevice.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highjump.medicaldevice.BaseActivity;
import com.highjump.medicaldevice.R;


public class MedicalHistoryAdapter extends BaseAdapter {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int nResId = R.layout.layout_history_item;

        // 标题
        if (viewType == BaseAdapter.ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            nResId = R.layout.layout_history_header;
        }

        // 创建视图
        View v = LayoutInflater.from(parent.getContext()).inflate(nResId, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    /**
     * ViewHodler
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
