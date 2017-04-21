package com.highjump.medicaldevice.api;

import com.baidu.location.BDLocation;
import com.highjump.medicaldevice.model.Device;
import com.highjump.medicaldevice.model.User;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class APIManager {

    private static String API_PATH_DATA = "http://219.234.2.171:8888";

    // API功能
    private final String ACTION_SIGNUP = "registerMember";
    private final String ACTION_LOGIN = "loginUser";
    private final String ACTION_GET_USERINFO = "getMemberInfo";
    private final String ACTION_SET_USERINFO = "sendMemberInfo";
    private final String ACTION_GET_TREATHISTORY = "listTreat";
    private final String ACTION_GET_USERLIST = "listMember";
    private final String ACTION_GET_DEVICELIST = "listDevice";
    private final String ACTION_GET_USELIST_DEVICE = "totalDeviceUsage";
    private final String ACTION_GET_USELIST_USER = "totalMemberUsage";
    private final String ACTION_SET_DEVICE = "saveDevice";
    private final String ACTION_USE_DEVICE = "useDevice";

    // 参数名称
    private final String PARAM_ACTION = "action";
    private final String PARAM_DATA = "data";

    // 时限 (毫秒)
    private int mnTimeout = 20000;

    // 实例； 第一次被调用的时候会设置
    private static APIManager mInstance = null;

    /**
     * 获取API_Manager实例
     */
    public static APIManager getInstance() {
        if (mInstance == null) {
            mInstance = new APIManager();
        }

        return mInstance;
    }

    /**
     * 会员注册
     * @param username 用户名
     * @param userpassword 密码
     * @param responseCallback 回调函数
     */
    public void userSignup(String username,
                           String userpassword,
                           Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("username", username);
            objData.put("password", userpassword);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_SIGNUP, objData.toString(), responseCallback);
    }

    /**
     * 会员登录
     * @param username 用户名
     * @param userpassword 密码
     * @param responseCallback 回调函数
     */
    public void userLogin(String username,
                          String userpassword,
                          Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("username", username);
            objData.put("password", userpassword);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_LOGIN, objData.toString(), responseCallback);
    }

    /**
     * 获取会员信息
     * @param userId 用户标识
     * @param loginId 登录编号
     * @param responseCallback 回调函数
     */
    public void getUserInfo(String userId,
                            String loginId,
                            Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", userId);
            objData.put("loginID", loginId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_GET_USERINFO, objData.toString(), responseCallback);
    }

    /**
     * 保存会员信息
     * @param user 用户信息
     * @param responseCallback 回调函数
     */
    public void saveUserInfo(User user,
                             Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("username", user.getUsername());
            objData.put("name", user.getName());
            objData.put("idCode", user.getIdCode());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_SET_USERINFO, objData.toString(), responseCallback);
    }

    /**
     * 获取理疗记录
     * @param user 用户信息
     * @param currentPage 当前页号
     * @param responseCallback 回调函数
     */
    public void getTreatHistory(User user,
                                int currentPage,
                                Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("currentPage", currentPage);
            objData.put("pageSize", Config.PAGE_SIZE);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_GET_TREATHISTORY, objData.toString(), responseCallback);
    }

    /**
     * 获取会员列表
     * @param user 用户信息
     * @param currentPage 当前页号
     * @param responseCallback 回调函数
     */
    public void getUserList(User user,
                            int currentPage,
                            Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("currentPage", currentPage);
            objData.put("pageSize", Config.PAGE_SIZE);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_GET_USERLIST, objData.toString(), responseCallback);
    }

    /**
     * 获取设备列表
     * @param user 用户信息
     * @param currentPage 当前页号
     * @param responseCallback 回调函数
     */
    public void getDeviceList(User user,
                            int currentPage,
                            Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("currentPage", currentPage);
            objData.put("pageSize", Config.PAGE_SIZE);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_GET_DEVICELIST, objData.toString(), responseCallback);
    }

    /**
     * 获取使用列表
     * @param user 用户信息
     * @param type 使用列表类型
     *             0: 设备
     *             1: 会员
     * @param currentPage 当前页号
     * @param responseCallback 回调函数
     */
    public void getUseList(User user,
                           int type,
                           int currentPage,
                           Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("currentPage", currentPage);
            objData.put("pageSize", Config.PAGE_SIZE);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String action = ACTION_GET_USELIST_USER;
        if (type == 0) {
            action = ACTION_GET_USELIST_DEVICE;
        }

        sendToServiceByPost(API_PATH_DATA, action, objData.toString(), responseCallback);
    }

    /**
     * 提交设备参数
     * @param user
     * @param device
     * @param ssid
     * @param password
     * @param responseCallback
     */
    public void setDevice(User user,
                          Device device,
                          String ssid,
                          String password,
                          Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("deviceCode", device.getDeviceCode());
            objData.put("ip", device.getIpAddress());
            objData.put("port", "");
            objData.put("mac", device.getMacAddress());
            objData.put("ssid", ssid);
            objData.put("password", password);
            objData.put("leasePlace", device.getPlace());
            objData.put("leaseTime", CommonUtils.dateToString(new Date()));

            // 位置
            String strLocation = "";

            BDLocation location = CommonUtils.getInstance().getCurrentLocation();
            if (location != null) {
                strLocation = location.getLatitude() + "," + location.getLongitude();
            }

            objData.put("location", strLocation);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_SET_DEVICE, objData.toString(), responseCallback);
    }

    /**
     * 提交使用记录
     * @param user
     * @param deviceCode
     * @param responseCallback
     */
    public void useDevice(User user,
                          String deviceCode,
                          Callback responseCallback) {

        JSONObject objData = new JSONObject();
        try {
            objData.put("userID", user.getId());
            objData.put("loginID", user.getLoginId());
            objData.put("deviceCode", deviceCode);
            objData.put("startTime", CommonUtils.dateToString(new Date()));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        sendToServiceByPost(API_PATH_DATA, ACTION_USE_DEVICE, objData.toString(), responseCallback);
    }

    /**
     * GET方式发送请求
     * @param serviceAPIURL url
     * @param action 功能
     * @param jsonParam 参数
     * @param responseCallback 回调函数
     */
    private void sendToServiceByGet(String serviceAPIURL,
                                    String action,
                                    String jsonParam,
                                    Callback responseCallback) {

        String strUrl = serviceAPIURL;

        strUrl += "?" + PARAM_ACTION + "=" + action;
        strUrl += "&" + PARAM_DATA + "=" + jsonParam;

        Request request = new Request.Builder()
                .url(strUrl)
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(responseCallback);
    }

    /**
     * POST方式发送请求
     * @param serviceAPIURL url
     * @param action 功能
     * @param jsonParam 参数
     * @param responseCallback 回调函数
     */
    private void sendToServiceByPost(String serviceAPIURL,
                                     String action,
                                     String jsonParam,
                                     Callback responseCallback) {

        // 构建参数
        FormBody formParam = new FormBody.Builder()
                .add("action", action)
                .add("data", jsonParam)
                .build();

        Request request = new Request.Builder()
                .url(serviceAPIURL)
                .post(formParam)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(responseCallback);
    }
}
