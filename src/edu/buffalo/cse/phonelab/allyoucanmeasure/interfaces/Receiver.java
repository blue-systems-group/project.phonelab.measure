package edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;

import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public abstract class Receiver extends BroadcastReceiver
    implements ConnectionCallbacks, OnConnectionFailedListener {

    protected Context mContext;
    protected final String TAG = LocalUtils.getTag(this.getClass());
    protected long mLastUpdated;

    public static final String KEY_ACTION = "action";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_UPTIME = "uptime";

    private long mMaxUpdateIntervalSec;
    protected boolean mStarted;

    private AlarmManager mAlarmManager;

    protected GoogleApiClient mGoogleApiClient;

    private final String ALARM_INTENT_NAME = this.getClass().getName() + ".Alarm";
    private PendingIntent mAlarmPendingIntent;
    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (elapseNanosSinceLastUpdate() > getMaxUpdateIntervalSec()*1e9) {
                    triggerUpdate();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Failed to trigger update.", e);
            }
            if (mStarted) {
                mAlarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime()+mMaxUpdateIntervalSec*1000,
                        mAlarmPendingIntent);
            }
        }
    };

    public Receiver(Context context) {
        Log.d(TAG, "Creating " + this.getClass().getSimpleName());

        mContext = context;
        mLastUpdated = 0L; 

        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        for (String name : getInterestedIntents()) {
            intentFilter.addAction(name);
        }
        mContext.registerReceiver(this, intentFilter);

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
            .addApi(ActivityRecognition.API)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();


        mMaxUpdateIntervalSec = 60L;
        mAlarmPendingIntent = PendingIntent.getBroadcast(mContext, 0,
                new Intent(ALARM_INTENT_NAME), PendingIntent.FLAG_UPDATE_CURRENT);
        mContext.registerReceiver(mAlarmReceiver, new IntentFilter(ALARM_INTENT_NAME));
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public void triggerUpdate() throws Exception {
        // pass
    }

    public long elapseNanosSinceLastUpdate() {
        return SystemClock.elapsedRealtimeNanos() - mLastUpdated;
    }


    public List<String> getInterestedIntents() {
        return new ArrayList<String>();
    }

    public void doReceive(Intent intent) throws Exception {
        // pass
    }

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

    public long getMaxUpdateIntervalSec() {
        return mMaxUpdateIntervalSec;
    }

    public void setMaxUpdateIntervalSec(int interval) {
        mMaxUpdateIntervalSec = interval;
        if (mStarted) {
            stop();
            start();
        }
    }

    public void start() {
        if (mStarted) {
            Log.d(TAG, "Not restarting " + this.getClass().getSimpleName());
        }
        Log.d(TAG, "Connecting to Google API.");
        mGoogleApiClient.connect();

        Log.d(TAG, "Start repeating every " + mMaxUpdateIntervalSec + " seconds.");
        mAlarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+mMaxUpdateIntervalSec*1000,
                mAlarmPendingIntent);
        mStarted = true;
    }

    public void stop() {
        Log.d(TAG, "Stop repeating.");
        mAlarmManager.cancel(mAlarmPendingIntent);

        Log.d(TAG, "Disconnecting from Google API.");
        mGoogleApiClient.disconnect();
        mStarted = false;
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(TAG, "Connected to Google API.");
    }

    @Override
    public void onConnectionSuspended(int arg1) {
        Log.e(TAG, "Google API connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Google API connection failed: " + result.getErrorMessage());
    }

}
