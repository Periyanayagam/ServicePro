package com.perusudroid.myservicepro.downloadservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.perusudroid.myservicepro.R;
import com.perusudroid.myservicepro.Utils;
import com.perusudroid.myservicepro.locationservice.LocationService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloadActivity extends AppCompatActivity implements DownloadReceiver.Receiver {

    private static final String TAG = DownloadActivity.class.getSimpleName();
    private DownloadReceiver mDownloadReceiver;
    private TextView tvStatus;
    private ProgressDialog mProgressDialog;

    private String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        tvStatus = findViewById(R.id.tvStatus);
        mProgressDialog = new ProgressDialog(this);
    }

    public void downloadListClicked(View view) {
        mDownloadReceiver = new DownloadReceiver(mHandler);
        mDownloadReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);

        /* Send optional extras to Download IntentService */
        intent.putExtra("url", url);
        intent.putExtra("receiver", mDownloadReceiver);
        intent.putExtra("type", 1);

        startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                tvStatus.setText("Downloading..!");
                mProgressDialog.setTitle("Downloading");
                mProgressDialog.setMessage("Please wait..");
                mProgressDialog.show();
                Toast.makeText(this, "Downloading..", Toast.LENGTH_SHORT).show();
                break;
            case DownloadService.STATUS_FINISHED:
                tvStatus.setText("Completed");
                mProgressDialog.dismiss();
                Toast.makeText(this, "Downloading completed", Toast.LENGTH_SHORT).show();

                if (resultData != null) {
                    if (resultData.getInt("type") == 1) {
                        resultData.getString("result");
                        Log.d(TAG, "onReceiveResult: " + resultData.getString("result"));
                    }

                }

                break;
            case DownloadService.STATUS_ERROR:
                Toast.makeText(this, "Downloading Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Utils.isMyServiceRunning(DownloadService.class, this)){
            stopService(new Intent(this, DownloadService.class));
        }
    }
}
