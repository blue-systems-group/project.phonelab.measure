package edu.buffalo.cse.phonelab.allyoucanmeasure.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;

import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public class ActivityIntentService extends IntentService {

    private final String TAG = LocalUtils.getTag(this.getClass());
    private Context mContext;

    private final static String PREFIX = ActivityIntentService.class.getName();
    public final static String ACTION_ACTIVITY_UPDATED = PREFIX + ".ActivityUpdate";
    public final static String EXTRA_ACTIVITY = PREFIX + ".Activity";

    public ActivityIntentService() {
        super("ActivityIntentService");
        mContext = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent activityIntent = new Intent(ACTION_ACTIVITY_UPDATED);
        try {
            activityIntent.putExtra(EXTRA_ACTIVITY,
                    LocalUtils.toJSONObject(result).toString());
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to get activity result.");
        }
        mContext.sendBroadcast(activityIntent);
    }
}
