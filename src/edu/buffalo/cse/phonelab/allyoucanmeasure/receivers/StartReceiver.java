package edu.buffalo.cse.phonelab.allyoucanmeasure.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.buffalo.cse.phonelab.allyoucanmeasure.services.MainService;

public class StartReceiver extends BroadcastReceiver {
    private static final String TAG = LocalUtils.getTag(StartReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Starting WiseFi Service from" + this.getClass().getSimpleName());
		context.startService(new Intent(context, MainService.class));
	}
}
