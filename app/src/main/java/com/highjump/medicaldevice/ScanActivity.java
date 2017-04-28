package com.highjump.medicaldevice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private static final  int REQUEST_CODE_SETTING = 100;

    private ZXingScannerView mScannerView;

    public static final String SCAN_CODE = "scan_code";
    public static final String SCAN_TYPE = "scan_type";

    public static final int SCAN_TYPE_START = 0;
    public static final int SCAN_TYPE_GETCODE = 1;

    // 扫码页面类型
    private int mnType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // 获取类型
        Intent intent = getIntent();
        if (intent.hasExtra(SCAN_TYPE)) {
            mnType = intent.getIntExtra(SCAN_TYPE, 0);
        }

        // 初始化toolbar
        initToolbar();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);

        // 权限设置
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_SETTING)
                .permission(Manifest.permission.CAMERA)
                .rationale(new RationaleListener() {

                    @Override
                    public void showRequestPermissionRationale(int arg0, Rationale arg1) {
                        AndPermission.rationaleDialog(ScanActivity.this, arg1).show();
                    }
                })
                .send();
    }

    @Override
    public void handleResult(Result result) {

        // 打开设备
        if (mnType == ScanActivity.SCAN_TYPE_START) {
            // 跳转到设备页面
            Intent intent = new Intent(this, DeviceActivity.class);
            intent.putExtra(ScanActivity.SCAN_CODE, result.getText());
            startActivity(intent);
        }
        // 获取设备编号
        else {
            Intent intent = new Intent();
            intent.putExtra(ScanActivity.SCAN_CODE, result.getText());
            setResult(ConfigActivity.REQUEST_CODE, intent);
        }

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    private static class CustomViewFinderView extends ViewFinderView {

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setSquareViewFinder(true);

            // 调整边界颜色
            int nColorPrimary = getContext().getResources().getColor(R.color.colorPrimary);
            setBorderColor(nColorPrimary);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, mPermListener);
    }

    /**
     * 权限设置监听器
     */
    private PermissionListener mPermListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(ScanActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(ScanActivity.this, REQUEST_CODE_SETTING).show();
            }
        }
    };
}
