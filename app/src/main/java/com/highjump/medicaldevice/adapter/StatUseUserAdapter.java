package com.highjump.medicaldevice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highjump.medicaldevice.R;
import com.highjump.medicaldevice.StatisticsDeviceActivity;
import com.highjump.medicaldevice.model.User;

import java.util.ArrayList;


public class StatUseUserAdapter extends BaseAdapter {

    private ArrayList<User> maryData;

    public StatUseUserAdapter(Context ctx, ArrayList<User> aryData) {
        mContext = ctx;
        maryData = aryData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int nResId = R.layout.layout_stat_use_user_item;

        // 标题
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            nResId = R.layout.layout_stat_use_user_header;
        }
        // 底部
        else if (viewType == ITEM_TYPE.ITEM_TYPE_FOOTER.ordinal()) {
            nResId = R.layout.layout_list_loading;
        }

        // 创建视图
        View v = LayoutInflater.from(parent.getContext()).inflate(nResId, parent, false);

        RecyclerView.ViewHolder vhRes = null;

        // 标题
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            vhRes = new ViewHolderHeader(v);
        }
        // 底部
        else if (viewType == ITEM_TYPE.ITEM_TYPE_FOOTER.ordinal()) {
            vhRes = new ViewHolderFooter(v);
        }
        else {
            vhRes = new StatUseUserAdapter.ViewHolderItem(v);
        }

        return vhRes;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StatUseUserAdapter.ViewHolderItem) {

            StatUseUserAdapter.ViewHolderItem viewHolder = (StatUseUserAdapter.ViewHolderItem)holder;

            User itemData = maryData.get(position - 1);
            viewHolder.fillContent(itemData, position);
        }
        // 滚动到底部，需要加载
        else if (holder instanceof ViewHolderFooter){
//            Log.d(TAG, "Footer is shown: ");

            StatisticsDeviceActivity activity = (StatisticsDeviceActivity)mContext;
            activity.getData(false);
        }
    }

    @Override
    public int getItemCount() {
        // 数量考虑头部
        int nCount = maryData.size() + 1;

        // 底部
        if (mbNeedMore) {
            nCount++;
        }

        return nCount;
    }

    /**
     * ViewHolder
     */
    private static class ViewHolderItem extends BaseAdapter.ViewHolderItem {

        private TextView mTextUsage;
        private TextView mTextPhone;

        ViewHolderItem(View itemView) {
            super(itemView);

            // 初始化控件
            mTextUsage = (TextView) itemView.findViewById(R.id.text_usage);
            mTextPhone = (TextView) itemView.findViewById(R.id.text_phone);
        }

        /**
         * 显示数据
         * @param data
         * @param index
         */
        void fillContent(User data, int index) {
            super.fillContent(index);

            // 使用次数
            mTextUsage.setText(String.valueOf(data.getUseCount()));

            // 手机号
            mTextPhone.setText(data.getUsername());
        }
    }
}
