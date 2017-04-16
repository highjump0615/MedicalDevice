package com.highjump.medicaldevice.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

public class CommonUtils {

    /**
     * 跳转到指定的activity
     */
    public static void moveNextActivity(Activity source, Class<?> destinationClass, boolean removeSource) {
        Intent intent = new Intent(source, destinationClass);

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
}
