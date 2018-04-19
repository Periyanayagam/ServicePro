package com.perusudroid.myservicepro.locationservice;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.perusudroid.myservicepro.R;
import com.perusudroid.myservicepro.Utils;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements LocationReceiver.Result {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private LocationReceiver mLocationReceiver;
    private ListView listView;
    private LocationAdapter locationAdapter;
    List<Data> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        listView = findViewById(R.id.listView);
        mLocationReceiver = new LocationReceiver(new Handler());
        mLocationReceiver.setResult(this);

        doStartService();
    }

    private void doStartService() {
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("receiver", mLocationReceiver);
        startService(intent);
    }

    public void showGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS");
        builder.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        AlertDialog mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                            boolean showRationale = false;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                showRationale = shouldShowRequestPermissionRationale(permission);
                            }

                            if (!showRationale) {
                                Toast.makeText(this, "Location permission denied permanently. Enable in settings.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        doStartService();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (!isGpsEnabled(this)) {
                    showGpsDialog();
                } else {
                    doStartService();
                }
                break;
        }
    }

    @Override
    public void onResultReceived(int resultCode, Bundle resultData) {

        Log.d(TAG, "onResultReceived: resultCode " + resultCode + " resultData " + (resultData == null));

        switch (resultCode) {

            case LocationService.NO_PERMISSION:

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String permissions[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissions, 2);
                    }
                }
                break;

            case LocationService.NO_GPS:
                showGpsDialog();

                Toast.makeText(this, "GPS is not Enabled", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResultReceived: NO GPS");
                break;
            case LocationService.NO_INTERNET:
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResultReceived: NO INTERNET");
                break;
            case LocationService.LOCATION:
                if (resultData != null) {
                    Log.d(TAG, "onResultReceived: " + resultData.getString("lat")
                            + "lng " + resultData.getString("lng") + " address " +
                            resultData.getString("address"));

                    //mData.clear();
                    mData.add(new Data(resultData.getString("address"),
                            resultData.getString("lat"),
                            resultData.getString("lng")
                            ));

                    //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"jhhjv","ngfg"}));

                    if(locationAdapter == null){
                        locationAdapter = new LocationAdapter(mData, getApplicationContext() );
                        listView.setAdapter(locationAdapter);
                    }else{
                        locationAdapter.refresh(mData);
                    }


                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Utils.isMyServiceRunning(LocationService.class, this)){
            stopService(new Intent(this, LocationService.class));
        }

    }
}
