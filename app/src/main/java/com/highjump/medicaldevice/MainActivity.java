package com.highjump.medicaldevice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.highjump.medicaldevice.utils.CommonUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    MapView mMapView = null;
    BaiduMap mBaiduMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 改变地址状态，使地图显示在恰当的缩放大小
        MapStatus mapStatus = new MapStatus.Builder().zoom(15).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        // 扫码按钮
        Button button = (Button)findViewById(R.id.but_scan);
        button.setOnClickListener(this);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
            CommonUtils.moveNextActivity(MainActivity.this, LoginActivity.class, false);

            return true;
        }
        else if (id == R.id.action_signup) {
            // 跳转到注册页面
            CommonUtils.moveNextActivity(MainActivity.this, SignupActivity.class, false);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 配置设备
        if (id == R.id.nav_devconf) {
            CommonUtils.moveNextActivity(MainActivity.this, ConfigActivity.class, false);
        }
        // 理疗记录
        else if (id == R.id.nav_medicalhistory) {
            CommonUtils.moveNextActivity(MainActivity.this, MedicalHistoryActivity.class, false);
        }
        // 会员信息
        else if (id == R.id.nav_userinfo) {
            CommonUtils.moveNextActivity(MainActivity.this, UserInfoActivity.class, false);
        }
        // 会员统计
        else if (id == R.id.nav_stat_user) {
            CommonUtils.moveNextActivity(MainActivity.this, StatisticsUserActivity.class, false);
        }
        // 设备统计
        else if (id == R.id.nav_stat_device) {
            CommonUtils.moveNextActivity(MainActivity.this, StatisticsDeviceActivity.class, false);
        }
        // 使用统计
        else if (id == R.id.nav_stat_use) {
            CommonUtils.moveNextActivity(MainActivity.this, StatisticsUseActivity.class, false);
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.but_scan:
                // 跳转到扫描页面
                CommonUtils.moveNextActivity(MainActivity.this, ScanActivity.class, false);
                break;
        }
    }
}
