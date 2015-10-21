package edu.buffalo.cse.phonelab.allyoucanmeasure.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces.Receiver;
import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;



public class MainService extends Service {

    private final String TAG = LocalUtils.getTag(this.getClass());

    private static final String RECEIVER_PACKAGE_NAME =
        "edu.buffalo.cse.phonelab.allyoucanmeasure.receivers";

    private String[] RECEIVER_CLASS_NAMES = {
        "WifiReceiver",
        "CellularReceiver",
        "LocationReceiver",
        "ActivityReceiver",
    };

    private Process mLogcatProcess;

    private Map<String, Receiver> mReceivers;
    private Context mContext;
    private boolean mStarted;



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getLogcatFilePath() {
        File dir = new File("/sdcard/AllYouCanMeasure/");
        dir.mkdirs();
        return (new File(dir, "logcat-" + LocalUtils.getDateTimeString(
                        System.currentTimeMillis()) + ".log")).getAbsolutePath();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        Log.d(TAG, "Creating " + this.getClass().getSimpleName());
        mReceivers = new HashMap<String, Receiver>();
        for (String name : RECEIVER_CLASS_NAMES) {
            try {
                mReceivers.put(name, (Receiver) Class.forName(
                            RECEIVER_PACKAGE_NAME + "." + name)
                        .getConstructor(Context.class).newInstance(mContext));
                Log.d(TAG, "Created " + name);
            }
            catch (Exception e) {
                Log.e(TAG, "Failed to create " + name, e);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!mStarted) {
            return;
        }

        Log.d(TAG, "Destroying " + this.getClass().getSimpleName());
        for (Entry<String, Receiver> entry : mReceivers.entrySet()) {
            String name = entry.getKey();
            Receiver receiver = entry.getValue();
            Log.d(TAG, "Stopping " + name);
            receiver.stop();
        }

        mStarted = false;

        mLogcatProcess.destroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mStarted) {
            Log.d(TAG, "Not restarting " + this.getClass().getSimpleName());
        }
        for (Entry<String, Receiver> entry : mReceivers.entrySet()) {
            String name = entry.getKey();
            Receiver receiver = entry.getValue();
            Log.d(TAG, "Starting " + name);
            receiver.start();
        }

        String cmd = "logcat -v threadtime -f " + getLogcatFilePath() + " -r 1024 -n 4096";
        Log.d(TAG, "Starting Logcat: " + cmd);
        try {
            mLogcatProcess = (new ProcessBuilder())
                .command(cmd.split("\\s+"))
                .redirectErrorStream(true)
                .start();
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to start Logcat process.", e);
        }


        mStarted = true;
        return START_STICKY;
    }

    @Override
    public void onTrimMemory(int level) {
        // TODO Auto-generated method stub
        super.onTrimMemory(level);
    }
}
