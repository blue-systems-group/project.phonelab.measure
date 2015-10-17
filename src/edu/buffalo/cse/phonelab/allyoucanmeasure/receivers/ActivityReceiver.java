package edu.buffalo.cse.phonelab.allyoucanmeasure.receivers;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;

import edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces.Receiver;
import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public class ActivityReceiver extends Receiver
    implements ConnectionCallbacks, OnConnectionFailedListener {

    private final String ACTION_ACTIVITY_UPDATED = this.getClass().getName() + ".ActivityUpdate";
    private GoogleApiClient mGoogleApiClient;

    private PendingIntent mActivityPendingIntent;
    class ActivityIntentService extends IntentService {

        public ActivityIntentService() {
            super("ActivityIntentService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            JSONObject json = new JSONObject();
            try {
                json.put(KEY_ACTION, ACTION_ACTIVITY_UPDATED);
                json.put("result", LocalUtils.toJSONObject(result));
                log(json);
            }
            catch (Exception e) {
                Log.e(TAG, "Failed to log activity.", e);
            }
        }
    }

    public ActivityReceiver(Context context) {
        super(context);

        setMaxUpdateIntervalSec(10);

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
            .addApi(ActivityRecognition.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
        mActivityPendingIntent = PendingIntent.getService(mContext, 0,
                new Intent(mContext, ActivityIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(TAG, "Connected to Google API.");
        requestActivityUpdate();
    }

    @Override
    public void onConnectionSuspended(int arg1) {
        Log.e(TAG, "Google API connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Google API connection failed: " + result.getErrorMessage());
    }

    private void requestActivityUpdate() {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                getMaxUpdateIntervalSec()*1000,
                mActivityPendingIntent);
    }

    private void stopActivityUpdate() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient, mActivityPendingIntent);
    }

    @Override
    public void start() {
        if (!mStarted) {
            Log.d(TAG, "Connecting to Google API");
            mGoogleApiClient.connect();
        }
        mStarted = true;
    }

    @Override
    public void stop() {
        stopActivityUpdate();
        mStarted = false;
        mGoogleApiClient.disconnect();
    }

}
