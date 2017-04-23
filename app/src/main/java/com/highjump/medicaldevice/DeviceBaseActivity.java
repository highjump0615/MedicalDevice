package com.highjump.medicaldevice;


import android.util.Log;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import java.util.List;

public abstract class DeviceBaseActivity extends BaseActivity {
    private final String TAG = ConfigActivity.class.getSimpleName();

    private String mstrDid;

    private GizWifiSDKListener gizWifiSDKListener = new GizWifiSDKListener() {

        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {
            super.didBindDevice(result, did);

            // 成功
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                DeviceBaseActivity.this.processBindDevice(did);
            }
            // 二维码不符合
            else if (result == GizWifiErrorCode.GIZ_OPENAPI_BAD_QRCODE_CONTENT) {
                Toast.makeText(DeviceBaseActivity.this, "二维码不符合", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(DeviceBaseActivity.this, "二维码错误", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void didBindDevice(int error, String errorMessage, String did) {
            // 失败
            if (error != 0) {
                String toast = "二维码错误" + "\n" + errorMessage;
                Toast.makeText(DeviceBaseActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
            // 成功
            else {
                DeviceBaseActivity.this.processBindDevice(did);
            }
        };

        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {

            for (GizWifiDevice device : deviceList) {
                String strDid = device.getDid();

                // 获取ip地址和mac地址
                if (strDid.equals(mstrDid)) {
                    DeviceBaseActivity.this.didDiscovered(device);
                }
            }
        }

        @Override
        public void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {
            DeviceBaseActivity.this.didSetDeviceOnboarding(result, mac, did, productKey);
        }
    };

    /**
     * 处理绑定设备
     */
    protected void processBindDevice(String did) {

        mstrDid = did;

        // 获取绑定的设备
        GizWifiSDK.sharedInstance().getBoundDevices(
                CommonUtils.getInstance().getGzUid(),
                CommonUtils.getInstance().getGzToken(),
                null
        );
    }

    /**
     * 找到设备
     * @param device
     */
    public void didDiscovered(GizWifiDevice device) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(gizWifiSDKListener);
    }

    /**
     * 二维码绑定设备
     * @param qrCode
     */
    protected void bindDeviceByQRCode(String qrCode) {
        Log.e(TAG, "QR code: " + qrCode);

        // 要检查二维码的合法性，Bind
        if (qrCode.contains("product_key=") && qrCode.contains("did=") && qrCode.contains("passcode=")) {
            // 虚拟设备都是这种形式
            GizWifiSDK.sharedInstance().bindDevice(
                    CommonUtils.getInstance().getGzUid(),
                    CommonUtils.getInstance().getGzToken(),
                    getParamFomeUrl(qrCode, "did"),
                    getParamFomeUrl(qrCode, "passcode"),
                    null);
        }
        else if (qrCode.contains("mac=")) {
            // 实际设备
            GizWifiSDK.sharedInstance().bindRemoteDevice(
                    CommonUtils.getInstance().getGzUid(),
                    CommonUtils.getInstance().getGzToken(),
                    getParamFomeUrl(qrCode, "mac"),
                    Config.PRODUCT_KEY[0],
                    Config.PRODUCT_SECRET);
        }
        else if (qrCode.contains("type=") && qrCode.contains("code=")) {
        }
        else {
            GizWifiSDK.sharedInstance().bindDeviceByQRCode(
                    CommonUtils.getInstance().getGzUid(),
                    CommonUtils.getInstance().getGzToken(),
                    qrCode
            );
        }
    }

    protected void didSetDeviceOnboarding(GizWifiErrorCode result, String mac, String did, String productKey) {

    };
}
