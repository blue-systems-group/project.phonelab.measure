package edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

    private long mMaxUpdateIntervalSec;
    private boolean mStarted;

    private AlarmManager mAlarmManager;

    private final String ALARM_INTENT_NAME = this.getClass().getName() + ".Alarm";
    private PendingIntent mAlarmPendingIntent;
    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Intent fired, action is " + intent.getAction());
            try {
                triggerUpdate();
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

        mMaxUpdateIntervalSec = 60L;
        mAlarmPendingIntent = PendingIntent.getBroadcast(mContext, 0,
                new Intent(ALARM_INTENT_NAME), PendingIntent.FLAG_UPDATE_CURRENT);
        mContext.registerReceiver(mAlarmReceiver, new IntentFilter(ALARM_INTENT_NAME));
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public abstract void doReceive(Intent intent) throws Exception;
    public abstract List<String> getInterestedIntents();
    public abstract void triggerUpdate() throws Exception;

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
        mStarted = false;
    }
}
