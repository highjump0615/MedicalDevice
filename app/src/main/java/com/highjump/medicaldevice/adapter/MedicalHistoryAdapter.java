package com.highjump.medicaldevice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highjump.medicaldevice.HistoryActivity;
import com.highjump.medicaldevice.MedicalHistoryActivity;
import com.highjump.medicaldevice.R;
import com.highjump.medicaldevice.model.TreatLog;

import java.util.ArrayList;


public class MedicalHistoryAdapter extends BaseAdapter {

    private ArrayList<TreatLog> maryData;

    public MedicalHistoryAdapter(Context ctx, ArrayList<TreatLog> aryData) {
        mContext = ctx;
        maryData = aryData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int nResId = R.layout.layout_history_item;

        // 标题
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            nResId = R.layout.layout_history_header;
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

            TreatLog itemData = maryData.get(position - 1);
            viewHolder.fillContent(itemData, position);
        }
        // 滚动到底部，需要加载
        else if (holder instanceof ViewHolderFooter){
//            Log.d(TAG, "Footer is shown: ");

            MedicalHistoryActivity activity = (MedicalHistoryActivity)mContext;
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
     * ViewHodler
     */
    private static class ViewHolderItem extends BaseAdapter.ViewHolderItem {

        private TextView mTextTime;
        private TextView mTextAddress;
        private TextView mTextDevice;

        ViewHolderItem(View itemView) {
            super(itemView);

            // 初始化控件
            mTextTime = (TextView) itemView.findViewById(R.id.text_time);
            mTextAddress = (TextView) itemView.findViewById(R.id.text_address);
            mTextDevice = (TextView) itemView.findViewById(R.id.text_device);
        }

        /**
         * 显示数据
         * @param data
         * @param index
         */
        void fillContent(TreatLog data, int index) {
            super.fillContent(index);

            // 理疗时间
            mTextTime.setText(data.getTimeString());

            // 理疗地址
            mTextAddress.setText(data.getPlace());

            // 设备编码
            mTextDevice.setText(data.getDeviceCode());
        }
    }
}
