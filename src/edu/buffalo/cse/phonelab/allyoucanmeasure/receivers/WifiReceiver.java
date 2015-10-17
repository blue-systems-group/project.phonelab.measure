package edu.buffalo.cse.phonelab.allyoucanmeasure.receivers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces.Receiver;
import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public class WifiReceiver extends Receiver {

    private WifiManager mWifiManager;

    public WifiReceiver(Context context) {
        super(context);

        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        setMaxUpdateIntervalSec(5);
    }

    @Override
    public List<String> getInterestedIntents() {
        List<String> intents = new ArrayList<String>();
        intents.add(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intents.add(WifiManager.RSSI_CHANGED_ACTION);
        intents.add(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intents.add(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        return intents;
    }

    private void logScanResult(Intent intent, JSONObject json) throws JSONException {
        JSONArray array = new JSONArray();
        for (ScanResult entry : mWifiManager.getScanResults()) {
            array.put(LocalUtils.toJSONObject(entry));
        }
        json.put("results", array);
    }

    public void logRSSIChange(Intent intent, JSONObject json) throws JSONException {
        int newRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
        json.put("newRSSI", newRssi);
    }

    private void logWifiState(Intent intent, JSONObject json) throws JSONException {
        int prevState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1);
        int newState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

        json.put("prevState", prevState);
        json.put("newState", newState);
    }

    private void logNetworkState(Intent intent, JSONObject json) throws JSONException {
        String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        WifiInfo wifiInfo = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);

        if (bssid != null) {
            json.put("BSSID", "bssid");
        }
        if (networkInfo != null) {
            json.put("networkInfo", LocalUtils.toJSONObject(networkInfo));
        }
        if (wifiInfo != null) {
            json.put("wifiInfo", LocalUtils.toJSONObject(wifiInfo));
        }
    }

    @Override
    public void doReceive(Intent intent) throws Exception {
        String action = intent.getAction();

        JSONObject json = new JSONObject();
        json.put(KEY_ACTION, action);

        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            logScanResult(intent, json);
        }
        else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            logRSSIChange(intent, json);
        }
        else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            logWifiState(intent, json);
        }
        else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            logNetworkState(intent, json);
        }
        log(json);
    }

    public void triggerUpdate() {
        mWifiManager.startScan();
    }
}
