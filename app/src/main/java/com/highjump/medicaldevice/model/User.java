package com.highjump.medicaldevice.model;


import org.json.JSONException;
import org.json.JSONObject;

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
    // 用户角色
    private String userRole;

    // 保存实例, 后来用于获取当前用户
    private static User mInstance = null;

    public User(String uname, JSONObject data) {
        try {
            username = uname;
            loginId = data.getString("loginID");
            id = data.getString("userID");
            userRole = data.getString("userID");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mInstance = this;
    }

    /**
     * 获取当前用户
     * @return - 用户模型实例
     */
    public static User currentUser() {
        return mInstance;
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

}
