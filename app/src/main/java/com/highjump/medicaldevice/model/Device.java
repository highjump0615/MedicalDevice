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

    private double latitude;
    private double longitude;

    private String status;

    public Device(String did) {
        super(null);

        deviceCode = did;
    }

    public Device(JSONObject data) {
        super(data);

        try {
            deviceCode = data.getString("deviceCode");

            // 定位
            if (data.has("location")) {
                // 解析定位信息
                String strLocation[] = data.getString("location").split(",");
                longitude = Double.parseDouble(strLocation[0]);
                latitude = Double.parseDouble(strLocation[1]);
            }

            // 状态
            if (data.has("deviceStatus")) {
                status = data.getString("deviceStatus");
            }

            place = data.getString("leasePlace");
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

    public void setPlace(String place) {
        this.place = place;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }
}
