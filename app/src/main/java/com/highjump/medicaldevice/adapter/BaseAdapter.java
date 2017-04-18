package com.highjump.medicaldevice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highjump.medicaldevice.R;


class BaseAdapter extends Adapter<RecyclerView.ViewHolder> {

    boolean mbNeedMore = false;
    Context mContext;

    static enum ITEM_TYPE {
        ITEM_TYPE_HEADER,
        ITEM_TYPE_CONTENT,
        ITEM_TYPE_FOOTER,
    }

    public void setNeedMore(boolean value) {
        this.mbNeedMore = value;
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

    /**
     * ViewHolderItem
     */
    static class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView mTextNo;

        ViewHolderItem(View itemView) {
            super(itemView);

            // 初始化控件
            mTextNo = (TextView) itemView.findViewById(R.id.text_no);
        }

        /**
         * 更新数据
         * @param index 索引
         */
        void fillContent(int index) {
            // 序号
            mTextNo.setText(String.valueOf(index));
        }
    }

    /**
     * ViewHolderHeader
     */
    static class ViewHolderHeader extends RecyclerView.ViewHolder {

        ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }

    /**
     * ViewHolderFooter
     */
    static class ViewHolderFooter extends RecyclerView.ViewHolder {

        ViewHolderFooter(View itemView) {
            super(itemView);
        }
    }
}
