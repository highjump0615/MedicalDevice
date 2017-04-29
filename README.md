通络设备控制App (Android)
===================

> 通络医疗设备系统的客户端APP, 并且将远程控制通络设备

## Overview

### 1. 主要功能
- APP端  
设备处是配置，扫码设备二维码，触发设备加电指令，注册会员，查看设备分布，查看会员信息，查看设备信息，查看设备使用情况  

### 2. 技术内容
- 百度地图&定位  
显示[地图 Android SDK v4.3.0](http://lbsyun.baidu.com/index.php?title=androidsdk)  
[实时定位 Android SDK v7.1](http://lbsyun.baidu.com/index.php?title=android-locsdk)  
第一次定位后地图手工移动到目前为止  
获取地图中心的地理位置  
- 通过[OkHttp3.7.0](https://github.com/square/okhttp)实现数据交互  
- NavigationView菜单和设置菜单的动态设置  
未登录时，登录后根据用户角色显示不同的菜单
- 保存已登录的用户信息，避免下次打开app再次登录  
通过[Google Gson 2.7](https://github.com/google/gson)实现Java对象和Json的互相转换  
在SharedPreference保存参数 (data/data/packagename/shared_pref)  
- 通过DrawLayout和NavigationView实现  
- 所有列表都有“下拉刷新”和自动“加载更多”功能  
SwipeRefreshLayout和自定义Cell的使用  
- 基于Zxing的[二维码扫码](https://github.com/dm77/barcodescanner)功能  
- [机智云Android SDK v2.07.07](http://dev.gizwits.com/zh-cn/developer/resource/sdk?service=m2m)  
配置设置: ```Login``` -> ```setDeviceOnboarding()``` -> ```bindRemoteDevice()``` -> ```getBoundDevices()``` -> ```unbindDevice()```  
扫码开机: ```Login``` -> ```bindRemoteDevice()``` -> ```getBoundDevices()``` -> ```unbindDevice()```    
- 通过[AndPermission](https://github.com/yanzhenjie/AndPermission)实现Android6.0以上的动态权限管理

## Need to Improve  
- 短信验证等完善功能

