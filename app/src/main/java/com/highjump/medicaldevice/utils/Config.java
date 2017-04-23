package com.highjump.medicaldevice.utils;


public class Config {

    // SharedPreference 有关参数
    public static final String PREF_NAME = "md_pref";
    public static final String PREF_USER_DATA = "md_user_data";

    public static final String STR_CONNET_FAIL = "连接服务器失败";
    public static final String STR_PARSE_FAIL = "解析响应数据失败";

    public static final int PAGE_SIZE = 30;

    // 机智云参数
    public static final String APP_ID = "7b8abb8479bb4554afe07d9a7d4f7ae2";
    public static final String APP_SECRET = "04712d8e70a245da9ae85350370a24f9";
    public static final String[] PRODUCT_KEY = {"ac602bd62fc947908bcf7d4559c9e85e"};
    public static final String PRODUCT_SECRET = "daff0db636b741a3b89d5ae32b926393";

    // 设备热点默认前缀
    public static final String SoftAP_Start = "XPG-GAgent";
    // 设备热点默认密码
    public static final String SoftAP_PSW = "123456789";
}
