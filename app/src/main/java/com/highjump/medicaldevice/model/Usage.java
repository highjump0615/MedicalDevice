package com.highjump.medicaldevice.model;

import org.json.JSONException;
import org.json.JSONObject;

class Usage {

    private int useCount;

    Usage(JSONObject data) {
        if (data == null) {
            return;
        }

        try {
            useCount = data.getInt("usageSum");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getUseCount() {
        return useCount;
    }
}
