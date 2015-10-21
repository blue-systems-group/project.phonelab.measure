package edu.buffalo.cse.phonelab.allyoucanmeasure.receivers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;

import edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces.Receiver;
import edu.buffalo.cse.phonelab.allyoucanmeasure.services.ActivityIntentService;
import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public class ActivityReceiver extends Receiver {

    private PendingIntent mActivityPendingIntent;

    public ActivityReceiver(Context context) {
        super(context);

        setMaxUpdateIntervalSec(10);

        mActivityPendingIntent = PendingIntent.getService(mContext, 0,
                new Intent(mContext, ActivityIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle arg0) {
        super.onConnected(arg0);
        requestActivityUpdate();
    }

    private void requestActivityUpdate() {
        Log.d(TAG, "Requesting activity update every " +
                getMaxUpdateIntervalSec() + " secs.");
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
    public void stop() {
        stopActivityUpdate();
        super.stop();
    }

    @Override
    public void doReceive(Intent intent) throws Exception {
        JSONObject json = new JSONObject();
        JSONObject result = new JSONObject(intent.getStringExtra(
                    ActivityIntentService.EXTRA_ACTIVITY));
        json.put(KEY_ACTION, intent.getAction());
        json.put("result", result);
        log(json);
    }

    @Override
    public List<String> getInterestedIntents() {
        List<String> actions = new ArrayList<String>();
        actions.add(ActivityIntentService.ACTION_ACTIVITY_UPDATED);
        return actions;
    }
}
