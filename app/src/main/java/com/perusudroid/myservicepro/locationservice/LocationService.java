package com.perusudroid.myservicepro.locationservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.perusudroid.myservicepro.downloadservice.DownloadReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by Perusudroid on 4/19/2018.
 */

public class LocationService extends IntentService {

    public final static int NO_GPS = 1;
    public final static int NO_INTERNET = 2;
    public final static int LOCATION = 3;
    public final static int NO_PERMISSION = 4;
    private static final String TAG = LocationService.class.getSimpleName();

    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mLocationProviderClient;
    private ResultReceiver mReceiver;

    public LocationService() {
        super(LocationService.class.getSimpleName());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Location location = locationResult.getLastLocation();

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "onLocationResult: lattitude " + location.getLatitude() + " longitude " + location.getLongitude());
                getAddressFromLocation(location.getLatitude(), location.getLongitude());
            }else{
                Log.e(TAG, "onLocationResult: NO GPS in callback" );
                mReceiver.send(NO_GPS, null);
                stopSelf();
            }

        }
    };


    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            if (Geocoder.isPresent()) {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address fetchedAddress = addresses.get(0);
                    StringBuilder strAddress = new StringBuilder();
                    for (int i = 0; i <= fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                    }

                    if (mReceiver != null) {

                        Bundle mBundle = new Bundle();
                        mBundle.putString("lat", String.valueOf(latitude));
                        mBundle.putString("lng", String.valueOf(longitude));
                        mBundle.putString("address", strAddress.toString());

                        mReceiver.send(LOCATION, mBundle);
                    }

                    Log.d(TAG, "getAddressFromLocation: " + strAddress.toString());
                } else {
                    Log.e(TAG, "getAddressFromLocation: Searching Current Address");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getAddressFromLocation: No Network Found");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "getAddressFromLocation: No Location Name Found..!");
        }
    }


    public LocationService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        if (intent != null) {
            if (intent.getExtras() != null) {
                mReceiver = intent.getParcelableExtra("receiver");
                if (mReceiver != null) {
                    getLocationUpdates(mReceiver);
                }
            }
        }
    }

    private void getLocationUpdates(final ResultReceiver mReceiver) {
        Log.d(TAG, "getLocationUpdates: ");
        if (mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Handler mHandler = new Handler(getMainLooper());
                    mHandler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (ActivityCompat.checkSelfPermission(LocationService.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(LocationService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        mReceiver.send(NO_PERMISSION, null);
                                        stopSelf();
                                        return;
                                    }
                                    mLocationProviderClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, Looper.getMainLooper());
                                }
                            }
                    );
                } else {
                    mReceiver.send(NO_INTERNET, null);
                }
            } else {
                mReceiver.send(NO_GPS, null);
                Log.e(TAG, "getLocationUpdates: NO GPS in method" );
                stopSelf();
            }
        }
    }

    private LocationRequest getLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(120 * 1000); //2 min
        mLocationRequest.setFastestInterval(120 * 1000); //2 min
        return mLocationRequest;

    }
}
