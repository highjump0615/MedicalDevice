package com.highjump.medicaldevice.model;

import com.highjump.medicaldevice.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class Device extends Usage {

    // 设备编码
    private String deviceCode;
    // 放置地址
    private String place;
    // 放置时间
    private Date time;

    private String ipAddress;
    private String macAddress;

    public Device(String did) {
        super(null);

        deviceCode = did;
    }

    public Device(JSONObject data) {
        super(data);

        try {
            place = data.getString("leasePlace");
            deviceCode = data.getString("deviceCode");
            // string转date
            time = CommonUtils.stringToDate(data.getString("leaseTime"));
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}