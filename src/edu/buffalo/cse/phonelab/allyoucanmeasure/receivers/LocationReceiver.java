package edu.buffalo.cse.phonelab.allyoucanmeasure.receivers;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces.Receiver;
import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public class LocationReceiver extends Receiver
    implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private final String ACTION_LOCATION_UPDATED = this.getClass().getName() + ".LocationUpdated";

    public LocationReceiver(Context context) {
        super(context);

        setMaxUpdateIntervalSec(10);
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(TAG, "Connected to Google API.");
        requestLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int arg1) {
        Log.e(TAG, "Google API connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Google API connection failed: " + result.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY_ACTION, ACTION_LOCATION_UPDATED);
            json.put("location", LocalUtils.toJSONObject(location));
            log(json);
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to log location.", e);
        }
    }

    private void requestLocationUpdate() {
        Log.d(TAG, "Requesting location update every " + getMaxUpdateIntervalSec() + " secs.");
        LocationRequest request = new LocationRequest();
        request.setInterval(getMaxUpdateIntervalSec()*1000);
        request.setFastestInterval(0);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, request, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void stop() {
        stopLocationUpdate();
        super.stop();
    }
}
