package com.highjump.medicaldevice.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.highjump.medicaldevice.utils.CommonUtils;
import com.highjump.medicaldevice.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TreatLog {

    // 理疗时间
    private Date time;
    // 理疗地址
    private String place;
    // 设备编码
    private String deviceCode;

    public TreatLog(JSONObject data) {
        try {
            // string转date
            time = CommonUtils.stringToDate(data.getString("treatTime"));
            place = data.getString("treatPlace");
            deviceCode = data.getString("deviceCode");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取时间
     * @return
     */
    public String getTimeString() {
        return CommonUtils.dateToString(time);
    }

    public String getPlace() {
        return place;
    }

    public String getDeviceCode() {
        return deviceCode;
    }
}
