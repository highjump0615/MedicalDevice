package com.highjump.medicaldevice.utils;


import android.app.Activity;
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
}
