package com.highjump.medicaldevice;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.highjump.medicaldevice.api.APIManager;
import com.highjump.medicaldevice.api.ApiResponse;
import com.highjump.medicaldevice.model.Device;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, BaiduMap.OnMapStatusChangeListener {

    private final int SDK_PERMISSION_REQUEST = 127;

    MapView mMapView = null;
    BaiduMap mBaiduMap = null;

    LatLng mMapLocation;

    // 百度定位
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    boolean mbFirstLocation = true;

    // 当前用户
    private User mCurrentUser;

    // 调用服务状态
    private String mstrApiErr = "";

    private ArrayList<Device> maryDevice = new ArrayList<Device>();

    private GizWifiSDKListener gizWifiSDKListener = new GizWifiSDKListener() {
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            super.didUserLogin(result, uid, token);

            // 保存参数
            CommonUtils.getInstance().setGzUid(uid);
            CommonUtils.getInstance().setGzToken(token);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // 获取当前用户
        //
        mCurrentUser = User.currentUser(this);

        // 百度地图SDK初始化
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        // 初始化toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 设置drawer菜单
        navigationView.getMenu().clear();

        if (mCurrentUser == null) {
            // 未登录
            navigationView.inflateMenu(R.menu.activity_main_drawer_not_login);
        }
        else {
            // 登录时，根据用户角色显示不同的菜单
            if (mCurrentUser.getUserRole().equals(User.USER_SYSADMIN)) {
                // 系统管理员
                navigationView.inflateMenu(R.menu.activity_main_drawer_sysadmin);
            }
            else if (mCurrentUser.getUserRole().equals(User.USER_DEVADMIN)) {
                // 设备管理员
                navigationView.inflateMenu(R.menu.activity_main_drawer_devadmin);
            }
            else {
                // 普通会员
                navigationView.inflateMenu(R.menu.activity_main_drawer_normal);
            }
        }

        // 开始定位
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

        initLocation();

        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 改变地址状态，使地图显示在恰当的缩放大小
        MapStatus mapStatus = new MapStatus.Builder().zoom(15).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(this);

        // 扫码按钮
        Button button = (Button)findViewById(R.id.but_scan);
        button.setOnClickListener(this);
        ImageButton imageButton = (ImageButton)findViewById(R.id.but_refresh);
        imageButton.setOnClickListener(this);

        getPermissions();

        //
        // 初始化机智云SDK
        //
        List<String> productKeyList = Arrays.asList(Config.PRODUCT_KEY);
        ConcurrentHashMap<String, String> serverMap = new ConcurrentHashMap<String, String>();

        serverMap.put("openAPIInfo", "api.gizwits.com");
        serverMap.put("siteInfo", "site.gizwits.com");
        serverMap.put("pushInfo", "");

        GizWifiSDK.sharedInstance().startWithAppID(
                this,
                Config.APP_ID,
                Config.APP_SECRET,
                productKeyList,
                serverMap,
                false
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GizWifiSDK.sharedInstance().userLoginAnonymous();
            }
        }, 500);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mCurrentUser != null) {
            // 已登录
            getMenuInflater().inflate(R.menu.main_loggedin, menu);

            MenuItem item = menu.findItem(R.id.action_username);
            item.setTitle(mCurrentUser.getUsername());
        }
        else {
            // 未登录
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            // 跳转到登录页面
            gotoLogin();

            return true;
        }
        else if (id == R.id.action_signup) {
            // 跳转到注册页面
            gotoSignup();

            return true;
        }
        else if (id == R.id.action_logout) {
            // 注销
            User.logOut(this);

            // 重新加载主页面
            CommonUtils.moveNextActivity(MainActivity.this, MainActivity.class, true, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 登录
        if (id == R.id.nav_login) {
            gotoLogin();
        }
        else if (id == R.id.nav_signup) {
            gotoSignup();
        }
        // 配置设备
        else if (id == R.id.nav_devconf) {
            CommonUtils.moveNextActivity(MainActivity.this, ConfigActivity.class, false, false);
        }
        // 理疗记录
        else if (id == R.id.nav_medicalhistory) {
            CommonUtils.moveNextActivity(MainActivity.this, MedicalHistoryActivity.class, false, false);
        }
        // 会员信息
        else if (id == R.id.nav_userinfo) {
            CommonUtils.moveNextActivity(MainActivity.this, UserInfoActivity.class, false, false);
        }
        // 会员统计
        else if (id == R.id.nav_stat_user) {
            CommonUtils.moveNextActivity(MainActivity.this, StatisticsUserActivity.class, false, false);
        }
        // 设备统计
        else if (id == R.id.nav_stat_device) {
            CommonUtils.moveNextActivity(MainActivity.this, StatisticsDeviceActivity.class, false, false);
        }
        // 使用统计
        else if (id == R.id.nav_stat_use) {
            CommonUtils.moveNextActivity(MainActivity.this, StatisticsUseActivity.class, false, false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 地图生命周期管理
        mMapView.onResume();

        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(gizWifiSDKListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_scan:
                // 未登录时跳转到登录页面
                if (mCurrentUser == null) {
                    gotoLogin();
                }
                else {
                    // 跳转到扫描页面
                    CommonUtils.moveNextActivity(MainActivity.this, ScanActivity.class, false, false);
                }

                break;

            case R.id.but_refresh:
                // 获取周边设备
                findDevices();
                break;
        }
    }

    /**
     * 跳转到登录页面
     */
    public void gotoLogin() {
        CommonUtils.moveNextActivity(MainActivity.this, LoginActivity.class, false, false);
    }

    /**
     * 跳转到注册页面
     */
    public void gotoSignup() {
        CommonUtils.moveNextActivity(MainActivity.this, SignupActivity.class, false, false);
    }

    /**
     * 开始定位
     */
    private void initLocation() {

        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        int span = 3000;

        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(span);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(false);

        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);

        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);

        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(false);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(false);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);

        //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /*
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            /*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            // 读取电话状态权限
            addPermission(permissions, Manifest.permission.READ_PHONE_STATE);

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }
            else {
                permissionsList.add(permission);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        mMapLocation = mapStatus.target;
        findDevices();
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // 成功
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                location.getLocType() == BDLocation.TypeNetWorkLocation ||
                location.getLocType() == BDLocation.TypeOffLineLocation) {

                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build();

                mBaiduMap.setMyLocationData(locData);

                CommonUtils.getInstance().setCurrentLocation(location);

                // 第一次定位时, 将地图位置移动到当前位置
                if (mbFirstLocation) {
                    mbFirstLocation = false;

                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);

                    // 获取周边设备
                    findDevices();
                }
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    /**
     * 获取周边设备
     */
    private void findDevices() {
        double dLatitude, dLongitude;

        // 找不到根据位置，退出
        if (mMapLocation != null) {
            dLatitude = mMapLocation.latitude;
            dLongitude = mMapLocation.longitude;
        }
        else if (CommonUtils.getInstance().getCurrentLocation() != null) {
            dLatitude = CommonUtils.getInstance().getCurrentLocation().getLatitude();
            dLongitude = CommonUtils.getInstance().getCurrentLocation().getLongitude();
        }
        else {
            return;
        }

        // 清空状态
        mstrApiErr = "";

        // 清除当前的marker
        mBaiduMap.clear();

        // 调用相应的API
        APIManager.getInstance().findDevice(
                dLatitude,
                dLongitude,
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        // UI线程上运行
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                            // 设备
                            JSONArray aryJson = resultObj.getResult().getJSONArray("devices");
                            for (int i = 0; i < aryJson.length(); i++) {
                                JSONObject jsonObj = aryJson.getJSONObject(i);
                                Device deviceNew = new Device(jsonObj);

                                // 查看是否已存在
                                Device deviceExisting = null;
                                for (Device d : maryDevice) {
                                    if (d.getDeviceCode().equals(deviceNew.getDeviceCode())) {
                                        deviceExisting = d;
                                        break;
                                    }
                                }

                                // 如果已存在，更新位置，否则添加
                                if (deviceExisting == null) {
                                    maryDevice.add(deviceNew);
                                }
                                else {
                                    deviceExisting.setLatitude(deviceNew.getLatitude());
                                    deviceExisting.setLongitude(deviceNew.getLongitude());
                                }
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
                                    Toast.makeText(MainActivity.this, mstrApiErr, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // 地图上标注
                                for (Device device : maryDevice) {
                                    LatLng point = new LatLng(device.getLatitude(), device.getLongitude());
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_mark);

                                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                                    mBaiduMap.addOverlay(option);
                                }
                            }
                        });
                    }
                }
        );
    }
}
