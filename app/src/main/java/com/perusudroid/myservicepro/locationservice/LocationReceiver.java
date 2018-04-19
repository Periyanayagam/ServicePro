package com.perusudroid.myservicepro.locationservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Perusudroid on 4/19/2018.
 */

public class LocationReceiver extends ResultReceiver {

    private Result result;

    public LocationReceiver(Handler handler) {
        super(handler);
    }

    public void setResult(Result result) {
        this.result = result;
    }

    interface Result {
        void onResultReceived(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (result != null) {
            result.onResultReceived(resultCode, resultData);
        }
    }
}
