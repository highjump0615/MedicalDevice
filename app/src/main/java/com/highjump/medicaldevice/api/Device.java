package com.highjump.medicaldevice.api;

import com.highjump.medicaldevice.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class Device {

    // 设备编码
    private String deviceCode;
    // 放置地址
    private String place;
    // 放置时间
    private Date time;

    public Device(JSONObject data) {
        try {
            // string转date
            time = CommonUtils.stringToDate(data.getString("leaseTime"));
            place = data.getString("leasePlace");
            deviceCode = data.getString("deviceCode");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getPlace() {
        return place;
    }

    /**
     * 获取时间
     * @return
     */
    public String getTimeString() {
        return CommonUtils.dateToString(time);
    }
}
