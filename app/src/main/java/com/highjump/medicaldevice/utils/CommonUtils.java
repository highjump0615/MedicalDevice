package com.highjump.medicaldevice.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.baidu.location.BDLocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    // 机智云参数
    private String gzUid;
    private String gzToken;

    private BDLocation currentLocation;

    // 实例； 第一次被调用的时候会设置
    private static CommonUtils mInstance = null;

    /**
     * 获取实例
     */
    public static CommonUtils getInstance() {
        if (mInstance == null) {
            mInstance = new CommonUtils();
        }

        return mInstance;
    }

    //
    // Getter & Setter
    //
    public String getGzUid() {
        return gzUid;
    }

    public void setGzUid(String gzUid) {
        this.gzUid = gzUid;
    }

    public String getGzToken() {
        return gzToken;
    }

    public void setGzToken(String gzToken) {
        this.gzToken = gzToken;
    }

    public BDLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(BDLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * 跳转到指定的activity
     */
    public static void moveNextActivity(Activity source, Class<?> destinationClass, boolean removeSource, boolean clear) {
        Intent intent = new Intent(source, destinationClass);

        if (clear) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        source.startActivity(intent);

        if (removeSource) {
            source.finish();
        }
    }

    /**
     * dip转pixel
     * @param ctx context
     * @param dp dip
     * @return pixel
     */
    public static int dp2px(Context ctx, float dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 创建对话框
     */
    public static Dialog createErrorAlertDialog(final Context context, String message) {
        return createErrorAlertDialog(context, "", message);
    }

    /**
     * 创建对话框
     */
    public static Dialog createErrorAlertDialog(final Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null).create();
    }

    /**
     * string转date
     * @param strDate
     * @return
     */
    public static Date stringToDate(String strDate) {
        Date time = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            time = sdf.parse(strDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * date转string
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }
}
