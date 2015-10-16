package edu.buffalo.cse.phonelab.allyoucanmeasure.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.provider.Settings;

public class LocalUtils {

    private static String TAG = getTag(LocalUtils.class);

    public static final int MB = 1024*1024;

    /** Generate log tag for class.  */
    public static String getTag(Class<?> c) {
        return "WiseFi-" + c.getSimpleName();
    }

    public static int randInt(int min, int max) {
        return min + (int)(Math.random() * (max-min+1));
    }

    public static void setAirplaneMode(Context context, boolean enable) {
        boolean previousState = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if (previousState != enable) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enable? 1: 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", enable);
            context.sendBroadcast(intent);
        }
    }

    public static JSONObject toJSONObject(Object o) throws JSONException {
        JSONObject json = new JSONObject();

        if (o instanceof NetworkInfo) {
            NetworkInfo networkInfo = (NetworkInfo) o;
            json.put("detailedState", networkInfo.getDetailedState().name());
            json.put("extraInfo", networkInfo.getExtraInfo());
            json.put("reason", networkInfo.getReason());
            json.put("state", networkInfo.getState().name());
            json.put("subType", networkInfo.getSubtypeName());
            json.put("type", networkInfo.getTypeName());
            json.put("isAvailable", networkInfo.isAvailable());
            json.put("isConnected", networkInfo.isConnected());
            json.put("isFailover", networkInfo.isFailover());
            json.put("isRoaming", networkInfo.isRoaming());
        }
        else if (o instanceof ScanResult) {
            ScanResult result = (ScanResult) o;
            json.put("tsf", result.timestamp);
            json.put("SSID", result.SSID);
            json.put("BSSID", result.BSSID);
            json.put("capabilities", result.capabilities);
            json.put("RSSI", result.level);
            json.put("frequency", result.frequency);
        }
        else if (o instanceof WifiInfo) {
            WifiInfo info = (WifiInfo) o;
            json.put("BSSID", info.getBSSID());
            json.put("SSID", info.getSSID());
            json.put("ipAddr", info.getIpAddress());
            json.put("macAddr", info.getMacAddress());
            json.put("RSSI", info.getRssi());
        }

        return json;
    }
}
