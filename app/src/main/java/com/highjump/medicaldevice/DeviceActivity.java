package com.highjump.medicaldevice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.Device;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DeviceActivity extends DeviceBaseActivity implements View.OnClickListener {

    private final String TAG = DeviceActivity.class.getSimpleName();

    private RelativeLayout mLayoutDialogStart;
    private RelativeLayout mLayoutDialogNotice;
    private GizWifiDevice mDevice;

    private final int SN_START = 1;

    // 调用服务状态
    private String mstrApiErr = "";

    /**
     * 设备监听
     */
    protected GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {

        // 接受数据回调
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
            Log.e(TAG, "result: " + result);

            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Toast.makeText(DeviceActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                return;
            }

            // 启动指令
            if (sn == SN_START) {
                // 显示提示对话框
                showDialogView(true, mLayoutDialogNotice, true);

                // 提交使用记录
                setUseDevice();

                // 解除绑定
                mDevice.setSubscribe(false);

                GizWifiSDK.sharedInstance().unbindDevice(
                        CommonUtils.getInstance().getGzUid(),
                        CommonUtils.getInstance().getGzToken(),
                        device.getDid()
                );
            }
        }
    };

    /**
     * 提交使用记录
     */
    private void setUseDevice() {
        // 清空状态
        mstrApiErr = "";

        // 调用相应的API
        APIManager.getInstance().useDevice(
                User.currentUser(null),
                mDevice.getDid(),
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DeviceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (!response.isSuccessful()) {
                            // 失败
                            mstrApiErr = response.message();
                        }

                        try {
                            // 获取返回数据
                            ApiResponse resultObj = new ApiResponse(response.body().string());

                            if (!resultObj.isSuccess()) {
                                mstrApiErr = "使用记录提交失败";
                            }
                        }
                        catch (Exception e) {
                            // 解析失败
                            mstrApiErr = Config.STR_PARSE_FAIL;
                        }

                        response.close();

                        // 更新界面，UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 出现了错误，直接退出
                                if (!mstrApiErr.isEmpty()) {
                                    Toast.makeText(DeviceActivity.this, mstrApiErr, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // 初始化toolbar
        initToolbar();

        // 初始化确认启动对话框
        mLayoutDialogStart = (RelativeLayout)findViewById(R.id.layout_dialog_start);
        Button button = (Button)findViewById(R.id.but_cancel);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.but_start);
        button.setOnClickListener(this);

        // 初始化成功提示对话框
        mLayoutDialogNotice = (RelativeLayout)findViewById(R.id.layout_dialog_success);
        button = (Button)findViewById(R.id.but_ok);
        button.setOnClickListener(this);

        Intent intent = getIntent();
        String strQrCode = intent.getStringExtra(ScanActivity.SCAN_CODE);

        // 二维码绑定设备
        bindDeviceByQRCode(strQrCode);
    }

    /**
     * 显示/隐藏对话框
     * @param show
     * @param view
     * @param animation
     */
    private void showDialogView(boolean show, View view, boolean animation) {

        Log.e(TAG, "showDialogView: " + view.getClass().getSimpleName() + ", " + show);

        AlphaAnimation animAlpha;

        if (show) {
            animAlpha = new AlphaAnimation(0f, 1f);
            view.setVisibility(View.VISIBLE);
        }
        else {
            animAlpha = new AlphaAnimation(1f, 0f);
            view.setVisibility(View.GONE);
        }

        if (animation) {
            animAlpha.setDuration(200);
            view.startAnimation(animAlpha);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_start:
                startDevice();
                break;

            case R.id.but_cancel:
                showDialogView(false, mLayoutDialogStart, true);
                break;

            case R.id.but_ok:
                showDialogView(false, mLayoutDialogNotice, true);

                // 清空数据
                mDevice = null;

                break;
        }
    }

    /**
     * 启动设备
     */
    private void startDevice() {
        // 隐藏启动对话框
        showDialogView(false, mLayoutDialogStart, false);

        ConcurrentHashMap<String, Object> command = new ConcurrentHashMap<String, Object>();

        // map中key为云端创建数据点的标识名，value为需要传输的值
        command.put("Power_Switch", true);

        // 调用write方法即可下发命令
        mDevice.write(command, SN_START);
    }

    /**
     * 处理绑定设备
     */
    @Override
    protected void processBindDevice(String did) {
        super.processBindDevice(did);
    }

    /**
     * 找到设备
     * @param device
     */
    @Override
    public void didDiscovered(GizWifiDevice device) {
        // 防止反复设置
        if (mDevice != null) {
            return;
        }

        mDevice = device;

        mDevice.setListener(gizWifiDeviceListener);
        mDevice.setSubscribe(true);

        // 显示开机对话框
        showDialogView(true, mLayoutDialogStart, true);
    }
}
