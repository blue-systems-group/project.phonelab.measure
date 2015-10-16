package edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public abstract class Receiver extends BroadcastReceiver {

    protected Context mContext;
    protected final String TAG = LocalUtils.getTag(this.getClass());
    protected long mLastUpdated;

    public static final String KEY_ACTION = "action";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_UPTIME = "uptime";

    public Receiver(Context context) {
        Log.d(TAG, "===== Creating " + this.getClass().getSimpleName() + " =======");

        mContext = context;
        mLastUpdated = 0L; 

        IntentFilter intentFilter = new IntentFilter();
        for (String name : getInterestedIntents()) {
            intentFilter.addAction(name);
        }
        mContext.registerReceiver(this, intentFilter);
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public abstract void doReceive(Intent intent) throws Exception;
    public abstract List<String> getInterestedIntents();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent fired, action is " + intent.getAction());
        try {
            doReceive(intent);
            mLastUpdated = SystemClock.elapsedRealtimeNanos();
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to handle intent.", e);
        }
    }

    public void log(JSONObject json) throws JSONException {
        if (!json.has(KEY_ACTION)) {
            return;
        }
        if (!json.has(KEY_TIMESTAMP)) {
            json.put(KEY_TIMESTAMP, System.currentTimeMillis());
        }
        if (!json.has(KEY_UPTIME)) {
            json.put(KEY_UPTIME, SystemClock.elapsedRealtimeNanos());
        }
        Log.i(TAG, json.toString());
    }
}
