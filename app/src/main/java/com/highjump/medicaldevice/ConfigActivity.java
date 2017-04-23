package com.highjump.medicaldevice;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.api.WifiAutoConnectManager;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.Device;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;
import com.highjump.medicaldevice.utils.NetUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConfigActivity extends DeviceBaseActivity implements View.OnClickListener {

    private final String TAG = ConfigActivity.class.getSimpleName();

    private EditText mEditCode;
    private Device mDevice;
    private EditText mEditAddress;
    private EditText mEditPassword;
    private TextView mTextNotice;

    public final static int REQUEST_CODE = 1;

    private WifiManager mWifiManager;
    private List<ScanResult> mListWifi;
    private String[] mwifiNames;
    private int mnWifiIndex = 0;

    // 调用服务状态
    private String mstrApiErr = "";

    private Button mButConfigure;

    private final int ONBOARD_TIMEOUT = 60;
    private GizWifiConfigureMode mnCurrentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // 初始化toolbar
        initToolbar();

        mEditCode = (EditText)findViewById(R.id.edit_code);
        // 阻止弹出键盘, 设备编码是自动获取的
        mEditCode.setInputType(InputType.TYPE_NULL);

        //
        // 获取wifi列表
        //
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        mListWifi = mWifiManager.getScanResults();

        // 设置spinner
        Spinner spinner = (Spinner) findViewById(R.id.spin_wifi);
        mwifiNames = new String[mListWifi.size()];

        for (int i = 0; i < mListWifi.size(); i++) {
            ScanResult sr = mListWifi.get(i);

            // 把当前的ssid排到最前面
            if (wifiInfo != null && wifiInfo.getBSSID().equals(sr.BSSID)) {
                if (i > 0) {
                    mwifiNames[i] = mwifiNames[0];
                    mwifiNames[0] = sr.SSID;

                    continue;
                }
            }

            mwifiNames[i] = sr.SSID;
        }

        // 发现搜索到的网络才设置Adapter
        if (!mListWifi.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mwifiNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mnWifiIndex = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        // 初始化控件
        mButConfigure = (Button) findViewById(R.id.but_configure);
        mButConfigure.setOnClickListener(this);
        Button button = (Button) findViewById(R.id.but_save);
        button.setOnClickListener(this);
        button = (Button) findViewById(R.id.but_cancel);
        button.setOnClickListener(this);

        mEditAddress = (EditText) findViewById(R.id.edit_address);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mTextNotice = (TextView) findViewById(R.id.text_notice);
    }

    /**
     * 处理绑定设备
     */
    @Override
    protected void processBindDevice(String did) {
        super.processBindDevice(did);

        mEditCode.setText(did);

        // 创建device
        mDevice = new Device(did);
    }

    /**
     * 找到设备
     * @param device
     */
    @Override
    public void didDiscovered(GizWifiDevice device) {
        mTextNotice.setText("设备配置完毕");

        String strDid = device.getDid();

        mDevice.setIpAddress(device.getIPAddress());
        mDevice.setMacAddress(device.getMacAddress());

        // 取消绑定
        GizWifiSDK.sharedInstance().unbindDevice(
                CommonUtils.getInstance().getGzUid(),
                CommonUtils.getInstance().getGzToken(),
                strDid
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConfigActivity.REQUEST_CODE) {
            String strQrCode = data.getStringExtra(ScanActivity.SCAN_CODE);

            // 二维码绑定设备
            bindDeviceByQRCode(strQrCode);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_configure:
                configureDevice();
                break;

            case R.id.but_save:
                setDevice();
                break;

            case R.id.but_cancel:
                break;
        }
    }

    /**
     * 配置设备
     */
    private void configureDevice() {

        // 关闭键盘
        View viewCurrent = getCurrentFocus();

        if (viewCurrent != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewCurrent.getWindowToken(), 0);
        }

        // 检查输入是否合适
        if (mListWifi.size() == 0) {
            CommonUtils.createErrorAlertDialog(this, "请输入网络").show();
        }

        String strSsid = mwifiNames[mnWifiIndex];

        mButConfigure.setEnabled(false);

        mTextNotice.setText("正在搜索设备... 最多一分钟");

        ArrayList<GizWifiGAgentType> aryMode = new ArrayList<GizWifiGAgentType>();
        aryMode.add(GizWifiGAgentType.GizGAgentHF);

        mnCurrentMode = GizWifiConfigureMode.GizWifiAirLink;

        GizWifiSDK.sharedInstance().setDeviceOnboarding(
                strSsid,
                mEditPassword.getText().toString(),
                GizWifiConfigureMode.GizWifiAirLink,
                null,
                ONBOARD_TIMEOUT,
                aryMode);
    }

    // 配置设置
    private void setDevice() {
        String strAddress = mEditAddress.getText().toString();

        // 检查输入是否合适
        if (mDevice == null) {
            CommonUtils.createErrorAlertDialog(this, "请输入设备编码").show();
            return;
        }

        if (TextUtils.isEmpty(strAddress)) {
            CommonUtils.createErrorAlertDialog(this, "请输入放置地点").show();
        }

        mTextNotice.setText("正在保存...");

        String strSsid = mwifiNames[mnWifiIndex];

        // 清空状态
        mstrApiErr = "";

        // 放置地址
        mDevice.setPlace(strAddress);

        // 调用相应的API
        APIManager.getInstance().setDevice(
                User.currentUser(null),
                mDevice,
                strSsid,
                mEditPassword.getText().toString(),
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextNotice.setText(e.getMessage());
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
                                mstrApiErr = "保存失败";
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
                                    mTextNotice.setText(mstrApiErr);
                                    return;
                                }

                                // 返回
                                onBackPressed();
                            }
                        });
                    }
                }
        );
    }

    /**
     * 配置设备结果
     * @param result
     * @param mac
     * @param did
     * @param productKey
     */
    @Override
    protected void didSetDeviceOnboarding(final GizWifiErrorCode result, String mac, final String did, String productKey) {

        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
            // 确认是否的设备
            if (productKey.equals(Config.PRODUCT_KEY[0])) {
                // 绑定设备
                GizWifiSDK.sharedInstance().bindRemoteDevice(
                        CommonUtils.getInstance().getGzUid(),
                        CommonUtils.getInstance().getGzToken(),
                        mac,
                        Config.PRODUCT_KEY[0],
                        Config.PRODUCT_SECRET
                );
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    mTextNotice.setText("设备配置成功, 正在绑定...");
                    mEditCode.setText(did);
                }
                else {
                    if (result == GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_TIMEOUT) {
                        mTextNotice.setText("设备配网超时");

                        // airline失败时，尝试softap模式
                        if (mnCurrentMode == GizWifiConfigureMode.GizWifiAirLink) {
                            readyToSoftAP();
                        }
                    }
                    else if (result == GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED) {
                        mTextNotice.setText("手机当前Wifi与设备配网SSID不匹配，无法完成设备配网");
                    }
                    else {
                        mTextNotice.setText("配置失败");
                    }

                    mButConfigure.setEnabled(true);
                }
            }
        });
    }

    /**
     * SoftAP模式配置设置
     */
    private void readyToSoftAP() {

        ScanResult scanResult;
        String strSSID = "";

        for (int i = 0; i < mListWifi.size(); i++) {
            scanResult = mListWifi.get(i);
            if (scanResult.SSID.length() > Config.SoftAP_Start.length()) {
                if (scanResult.SSID.contains(Config.SoftAP_Start)) {
                    strSSID = scanResult.SSID;
                }
            }
        }

        if (strSSID.isEmpty()) {
            return;
        }

        // 切换至设备热点
        WifiAutoConnectManager.WifiCipherType cipherType = WifiAutoConnectManager.getCipherType(ConfigActivity.this, strSSID);
        WifiAutoConnectManager manager = new WifiAutoConnectManager(mWifiManager);
        manager.connect(strSSID, Config.SoftAP_PSW, cipherType);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // 确认连接状态，开始onBoard
                boolean isChecked = true;
                while (isChecked) {
                    // 当前连接的ip名称
                    String presentSSID = NetUtils.getCurentWifiSSID(ConfigActivity.this);
                    if (!TextUtils.isEmpty(presentSSID) && presentSSID.contains(Config.SoftAP_Start)) {
                        // 连接是否正常
                        if (checkNetwork(ConfigActivity.this)) {
                            isChecked = false;
                            GizWifiSDK.sharedInstance().setDeviceOnboarding(
                                    mwifiNames[mnWifiIndex],
                                    mEditPassword.getText().toString(),
                                    GizWifiConfigureMode.GizWifiSoftAP,
                                    presentSSID,
                                    ONBOARD_TIMEOUT,
                                    null);

                            mnCurrentMode = GizWifiConfigureMode.GizWifiSoftAP;
                            mTextNotice.setText("Airlink模式设备配网超时, 正在进行SoftAP模式配置...");
                        }
                    }
                }

                timer.cancel();
            }
        }, 5 * 1000);

    }

    /**
     * 检查网络连通性（工具方法）
     * @param context
     * @return
     */
    public boolean checkNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }
}
