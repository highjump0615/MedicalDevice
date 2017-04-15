package com.highjump.medicaldevice.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;


public class BaseAdapter extends Adapter<RecyclerView.ViewHolder> {

    public static enum ITEM_TYPE {
        ITEM_TYPE_HEADER,
        ITEM_TYPE_CONTENT,
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // 第一行是标题
        if (position == 0) {
            return ITEM_TYPE.ITEM_TYPE_HEADER.ordinal();
        }
        else {
            return ITEM_TYPE.ITEM_TYPE_CONTENT.ordinal();
        }
    }
}
