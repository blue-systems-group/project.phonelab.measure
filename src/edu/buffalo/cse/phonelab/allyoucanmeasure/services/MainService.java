package edu.buffalo.cse.phonelab.allyoucanmeasure.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;



public class MainService extends Service {

    private final String TAG = LocalUtils.getTag(this.getClass());



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "========= Creating " + this.getClass().getSimpleName() + " ============");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTrimMemory(int level) {
        // TODO Auto-generated method stub
        super.onTrimMemory(level);
    }
}
