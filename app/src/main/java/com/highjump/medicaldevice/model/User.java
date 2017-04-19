package com.highjump.medicaldevice.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class User {

    public static String USER_SYSADMIN = "系统管理员";
    public static String USER_DEVADMIN = "设备管理员";
    public static String USER_NORMAL = "普通会员";

    // 用户名
    private String username;
    // 用户标识
    private String id;
    // 登录编号
    private String loginId;
    // 姓名
    private String name;
    // 身份证号
    private String idCode;
    // 用户角色
    private String userRole;
    // 注册时间
    private Date time;

    // 保存实例, 后来用于获取当前用户
    private static User mInstance = null;

    public User(String uname, JSONObject data) {
        try {
            username = uname;
            loginId = data.getString("loginID");
            id = data.getString("userID");
            userRole = data.getString("userRole");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mInstance = this;
    }

    /**
     * 构造函数 - 获取用户列表时使用
     * @param data
     */
    public User(JSONObject data) {
        try {
            username = data.getString("username");;
            name = data.getString("name");
            // string转date
            time = CommonUtils.stringToDate(data.getString("registerTime"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前用户
     * @return - 用户模型实例
     */
    public static User currentUser(Context context) {
        if (mInstance == null && context != null) {
            // 获取Preference的参数
            SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);
            String strJson = preferences.getString(Config.PREF_USER_DATA, "");

            if (!strJson.isEmpty()) {
                Gson gson = new Gson();
                mInstance = gson.fromJson(strJson, User.class);
            }
        }

        return mInstance;
    }

    /**
     * 注销
     * @param context
     */
    public static void logOut(Context context) {
        mInstance = null;

        // 删除Preference的参数
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Config.PREF_USER_DATA);
        editor.apply();
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    /**
     * 是否已获取用户信息
     * @return
     */
    public boolean isFetched() {
        boolean bRes = true;

        if (name == null || name.isEmpty()) {
            bRes = false;
        }

        return bRes;
    }

    /**
     * 获取注册时间
     * @return
     */
    public String getTimeString() {
        return CommonUtils.dateToString(time);
    }
}
