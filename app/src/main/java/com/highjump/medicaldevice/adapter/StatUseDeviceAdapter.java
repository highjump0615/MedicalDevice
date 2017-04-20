package com.highjump.medicaldevice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highjump.medicaldevice.R;
import com.highjump.medicaldevice.StatisticsDeviceActivity;
import com.highjump.medicaldevice.model.Device;

import java.util.ArrayList;


public class StatUseDeviceAdapter extends BaseAdapter {

    private ArrayList<Device> maryData;

    public StatUseDeviceAdapter(Context ctx, ArrayList<Device> aryData) {
        mContext = ctx;
        maryData = aryData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int nResId = R.layout.layout_stat_use_device_item;

        // 标题
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            nResId = R.layout.layout_stat_use_device_header;
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
        } else {
            vhRes = new StatUseDeviceAdapter.ViewHolderItem(v);
        }

        return vhRes;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StatUseDeviceAdapter.ViewHolderItem) {

            StatUseDeviceAdapter.ViewHolderItem viewHolder = (StatUseDeviceAdapter.ViewHolderItem)holder;

            Device itemData = maryData.get(position - 1);
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
        private TextView mTextAddress;
        private TextView mTextDevice;

        ViewHolderItem(View itemView) {
            super(itemView);

            // 初始化控件
            mTextUsage = (TextView) itemView.findViewById(R.id.text_usage);
            mTextAddress = (TextView) itemView.findViewById(R.id.text_address);
            mTextDevice = (TextView) itemView.findViewById(R.id.text_device);
        }

        /**
         * 显示数据
         * @param data
         * @param index
         */
        void fillContent(Device data, int index) {
            super.fillContent(index);

            // 使用次数
            mTextUsage.setText(String.valueOf(data.getUseCount()));

            // 放置地址
            mTextAddress.setText(data.getPlace());

            // 设备编码
            mTextDevice.setText(data.getDeviceCode());
        }
    }
}
