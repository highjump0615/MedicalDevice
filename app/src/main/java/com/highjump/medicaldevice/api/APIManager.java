package com.highjump.medicaldevice.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
