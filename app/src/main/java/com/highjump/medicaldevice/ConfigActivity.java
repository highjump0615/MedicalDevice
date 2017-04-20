package com.highjump.medicaldevice;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.api.GizWifiSSID;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.highjump.medicaldevice.model.Device;
import com.highjump.medicaldevice.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = ConfigActivity.class.getSimpleName();

    private EditText mEditCode;
    private Device mDevice;
    private EditText mEditAddress;
    private EditText mEditPassword;

    public final static int REQUEST_CODE = 1;

    private WifiManager mWifiManager;
    private List<ScanResult> mListWifi;
    private int mnWifiIndex = 0;

    private GizWifiSDKListener gizWifiSDKListener = new GizWifiSDKListener() {

        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {
            super.didBindDevice(result, did);

            // 成功
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                processBindDevice(did);
            }
            // 二维码不符合
            else if (result == GizWifiErrorCode.GIZ_OPENAPI_BAD_QRCODE_CONTENT) {
                Toast.makeText(ConfigActivity.this, "二维码不符合", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ConfigActivity.this, "二维码错误", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didBindDevice(int error, String errorMessage, String did) {
            // 失败
            if (error != 0) {
                String toast = "二维码错误" + "\n" + errorMessage;
                Toast.makeText(ConfigActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
            // 成功
            else {
                processBindDevice(did);
            }
        };

        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {

            for (GizWifiDevice device : deviceList) {
                String strDid = device.getDid();

                // 获取ip地址和mac地址
                if (mEditCode.getText().toString().equals(strDid)) {

                    mDevice.setIpAddress(device.getIPAddress());
                    mDevice.setMacAddress(device.getMacAddress());

                    // 取消绑定
                    GizWifiSDK.sharedInstance().unbindDevice(
                            CommonUtils.getInstance().getGzUid(),
                            CommonUtils.getInstance().getGzToken(),
                            strDid
                    );
                }
            }
        }
    };

    /**
     * 处理绑定设备
     */
    private void processBindDevice(String did) {
        mEditCode.setText(did);

        // 创建device
        mDevice = new Device(did);

        // 获取绑定的设备
        GizWifiSDK.sharedInstance().getBoundDevices(
                CommonUtils.getInstance().getGzUid(),
                CommonUtils.getInstance().getGzToken(),
                null
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // 初始化toolbar
        initToolbar();

        mEditCode = (EditText)findViewById(R.id.edit_code);
        // 阻止弹出键盘
        mEditCode.setInputType(InputType.TYPE_NULL);

        // 点击这个edit, 跳转到扫码页面
        mEditCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigActivity.this, ScanActivity.class);
                intent.putExtra(ScanActivity.SCAN_TYPE, ScanActivity.SCAN_TYPE_GETCODE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //
        // 获取wifi列表
        //
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        mListWifi = mWifiManager.getScanResults();

        // 设置spinner
        Spinner spinner = (Spinner) findViewById(R.id.spin_wifi);
        ArrayList<String> aryWifiName = new ArrayList<String>();
        String[] wifiNames = new String[mListWifi.size()];

        for (int i = 0; i < mListWifi.size(); i++) {
            ScanResult sr = mListWifi.get(i);
            wifiNames[i] = sr.SSID;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wifiNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);;
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

        // 初始化控件
        Button button = (Button) findViewById(R.id.but_configure);
        button.setOnClickListener(this);
        mEditAddress = (EditText) findViewById(R.id.edit_address);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(gizWifiSDKListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConfigActivity.REQUEST_CODE) {
            final String strQrCode = data.getStringExtra(ScanActivity.SCAN_CODE);

            Log.e(TAG, "QR code: " + strQrCode);

            // 要检查二维码的合法性，Bind
            if (strQrCode.contains("product_key=") && strQrCode.contains("did=") && strQrCode.contains("passcode=")) {
                // 虚拟设备都是这种形式
                GizWifiSDK.sharedInstance().bindDevice(
                        CommonUtils.getInstance().getGzUid(),
                        CommonUtils.getInstance().getGzToken(),
                        getParamFomeUrl(strQrCode, "did"),
                        getParamFomeUrl(strQrCode, "passcode"),
                        null);
            }
            else if (strQrCode.contains("type=") && strQrCode.contains("code=")) {
            }
            else {
                GizWifiSDK.sharedInstance().bindDeviceByQRCode(
                        CommonUtils.getInstance().getGzUid(),
                        CommonUtils.getInstance().getGzToken(),
                        strQrCode
                );
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_configure:
                setDevice();
                break;
        }
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

        if (mListWifi.size() == 0) {
            CommonUtils.createErrorAlertDialog(this, "请输入网络").show();
        }
    }
}
