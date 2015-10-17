package edu.buffalo.cse.phonelab.allyoucanmeasure.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.provider.Settings;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthLte;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class LocalUtils {

    private static String TAG = getTag(LocalUtils.class);

    public static final int MB = 1024*1024;

    /** Generate log tag for class.  */
    public static String getTag(Class<?> c) {
        return "AllYouCanMeasure-" + c.getSimpleName();
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
        else if (o instanceof CellInfoLte) {
            CellInfoLte lte = (CellInfoLte) o;
            json.put("type", "lte");
            json.put("cellIdentity", toJSONObject(lte.getCellIdentity()));
            json.put("cellSignalStrength", toJSONObject(lte.getCellSignalStrength()));
        }
        else if (o instanceof CellIdentityLte) {
            CellIdentityLte identity = (CellIdentityLte) o;
            json.put("ci", identity.getCi());
            json.put("mcc", identity.getMcc());
            json.put("mnc", identity.getMnc());
            json.put("pci", identity.getPci());
            json.put("tac", identity.getTac());
        }
        else if (o instanceof CellSignalStrengthLte) {
            CellSignalStrengthLte signal = (CellSignalStrengthLte) o;
            json.put("asu", signal.getAsuLevel());
            json.put("dbm", signal.getDbm());
            json.put("level", signal.getLevel());
            String[] fields = {"mRsrp", "mRsrq", "mRssnr", "mCqi", "mTimingAdvance"};
            for (String name : fields) {
                try {
                    Field f = signal.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    json.put(name, (Integer)f.get(signal));
                }
                catch (Exception e) {
                    json.put(name, -1);
                }
            }
        }
        else if (o instanceof CellInfoCdma) {
            json.put("type", "cmda");
        }
        else if (o instanceof CellInfoGsm) {
            json.put("type", "gsm");
        }
        else if (o instanceof CellInfoWcdma) {
            json.put("type", "wcdma");
        }
        else if (o instanceof Location) {
            Location l = (Location) o;
            json.put("accuracy", l.getAccuracy());
            json.put("altitude", l.getAltitude());
            json.put("bearing", l.getBearing());
            json.put("elapsedRealtimeNanos", l.getElapsedRealtimeNanos());
            json.put("latitude", l.getLatitude());
            json.put("longitude", l.getLongitude());
            json.put("provider", l.getProvider());
            json.put("speed", l.getSpeed());
        }
        else if (o instanceof ActivityRecognitionResult) {
            ActivityRecognitionResult result = (ActivityRecognitionResult) o;
            json.put("mostProbable", toJSONObject(result.getMostProbableActivity()));
            JSONArray array = new JSONArray();
            for (DetectedActivity a : result.getProbableActivities()) {
                array.put(toJSONObject(a));
            }
            json.put("probableActivities", array);
        }
        else if (o instanceof DetectedActivity) {
            DetectedActivity a = (DetectedActivity) o;
            switch (a.getType()) {
                case DetectedActivity.IN_VEHICLE:
                    json.put("type", "IN_VEHICLE");
                    break;
                case DetectedActivity.ON_BICYCLE:
                    json.put("type", "ON_BICYCLE");
                    break;
                case DetectedActivity.ON_FOOT:
                    json.put("type", "ON_FOOT");
                    break;
                case DetectedActivity.RUNNING:
                    json.put("type", "RUNNING");
                    break;
                case DetectedActivity.STILL:
                    json.put("type", "STILL");
                    break;
                case DetectedActivity.TILTING:
                    json.put("type", "TILTING");
                    break;
                case DetectedActivity.UNKNOWN:
                    json.put("type", "UNKNOWN");
                    break;
                case DetectedActivity.WALKING:
                    json.put("type", "WALKING");
                    break;
            }
            json.put("confidence", a.getConfidence());
        }
        return json;
    }

    public static String getDateTimeString(long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        return sdf.format(new Date(ms));
    }
}
