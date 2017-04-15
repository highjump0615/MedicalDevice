package com.highjump.medicaldevice.utils;


import android.app.Activity;
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
     * @param ctx
     * @param dp
     * @return
     */
    public static int dp2px(Context ctx, float dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }
}
