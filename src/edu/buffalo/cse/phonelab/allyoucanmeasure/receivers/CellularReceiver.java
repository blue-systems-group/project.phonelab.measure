package edu.buffalo.cse.phonelab.allyoucanmeasure.receivers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import edu.buffalo.cse.phonelab.allyoucanmeasure.interfaces.Receiver;
import edu.buffalo.cse.phonelab.allyoucanmeasure.utils.LocalUtils;

public class CellularReceiver extends Receiver {

    private TelephonyManager mTelephonyManager;

    private final String ACTION_CELL_INFO_UPDATED = this.getClass().getName() + ".CellInfo";

    public CellularReceiver(Context context) {
        super(context);

        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        setMaxUpdateIntervalSec(5);
    }

    @Override
    public void doReceive(Intent intent) throws Exception {
    }

    @Override
    public List<String> getInterestedIntents() {
        return new ArrayList<String>();
    }

    private void logCellInfo() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (CellInfo cellInfo : mTelephonyManager.getAllCellInfo()) {
            array.put(LocalUtils.toJSONObject(cellInfo));
        }
        json.put(KEY_ACTION, ACTION_CELL_INFO_UPDATED);
        json.put("cellInfo", array);
        log(json);
    }

    @Override
    public void triggerUpdate() throws Exception {
        logCellInfo();
    }
}
