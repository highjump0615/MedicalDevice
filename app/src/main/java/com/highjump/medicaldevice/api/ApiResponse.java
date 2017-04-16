package com.highjump.medicaldevice.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 4/16/17.
 */

public class ApiResponse {

    // 参数名称
    private final String PARAM_RESULT = "result";

    private boolean isSuccess;
    private JSONObject result = null;

    public ApiResponse(String response) {
        try {
            // 返回来的数据的都是encoded, 所以先把它解码
            String strResponseDecoded = URLDecoder.decode(response, "UTF-8");

            result = new JSONObject(strResponseDecoded);

            // 查看是否成功
            isSuccess = result.getInt("result") > 0;
        }
        catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * api调用是否成功
     * @return true:成功
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * 获取返回值
     * @return json
     */
    public JSONObject getResult() {
        return result;
    }
}
