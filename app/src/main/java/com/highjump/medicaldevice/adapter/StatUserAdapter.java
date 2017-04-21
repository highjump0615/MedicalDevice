package com.highjump.medicaldevice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highjump.medicaldevice.R;
import com.highjump.medicaldevice.StatisticsUserActivity;
import com.highjump.medicaldevice.model.User;

import java.util.ArrayList;


public class StatUserAdapter extends BaseAdapter {

    private ArrayList<User> maryData;

    public StatUserAdapter(Context ctx, ArrayList<User> aryData) {
        mContext = ctx;
        maryData = aryData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int nResId = R.layout.layout_stat_user_item;

        // 标题
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            nResId = R.layout.layout_stat_user_header;
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
            vhRes = new ViewHolderItem(v);
        }

        return vhRes;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderItem) {

            ViewHolderItem viewHolder = (ViewHolderItem)holder;

            User itemData = maryData.get(position - 1);
            viewHolder.fillContent(itemData, position);
        }
        // 滚动到底部，需要加载
        else if (holder instanceof ViewHolderFooter){
//            Log.d(TAG, "Footer is shown: ");

            StatisticsUserActivity activity = (StatisticsUserActivity)mContext;
            activity.getData(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int nViewType = super.getItemViewType(position);

        // 超过数据数量，那就是底部
        if (position > maryData.size()) {
            nViewType = ITEM_TYPE.ITEM_TYPE_FOOTER.ordinal();
        }

        return nViewType;
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

        private TextView mTextPhone;
        private TextView mTextName;
        private TextView mTextTime;

        ViewHolderItem(View itemView) {
            super(itemView);

            // 初始化控件
            mTextPhone = (TextView) itemView.findViewById(R.id.text_phone);
            mTextName = (TextView) itemView.findViewById(R.id.text_name);
            mTextTime = (TextView) itemView.findViewById(R.id.text_time);
        }

        /**
         * 显示数据
         * @param data
         * @param index
         */
        void fillContent(User data, int index) {
            super.fillContent(index);

            // 手机号
            mTextPhone.setText(data.getUsername());

            // 姓名
            mTextName.setText(data.getName());

            // 注册时间
            mTextTime.setText(data.getTimeString());
        }
    }
}
