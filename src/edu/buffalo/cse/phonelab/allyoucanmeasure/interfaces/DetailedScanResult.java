package edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces;

import org.json.JSONObject;

import android.net.wifi.ScanResult;


public class DetailedScanResult {

    public String BSSID;
    public String SSID;
    public int frequency;
    public int level;
    public int stationCount;
    public double channelUtilization;

    public DetailedScanResult() {
    }

    public DetailedScanResult(ScanResult result) {
        this.BSSID = result.BSSID;
        this.SSID = result.SSID;
        this.frequency = result.frequency;
        this.level = result.level;
        this.stationCount = 0;
        this.channelUtilization = 0;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("SSID", SSID);
            json.put("BSSID", BSSID);
            json.put("frequency", frequency);
            json.put("level", level);
            json.put("stationCount", stationCount);
            json.put("channelUtilization", channelUtilization);
        }
        catch (Exception e) {
            // ignore
        }
        return json.toString();
    }
}
