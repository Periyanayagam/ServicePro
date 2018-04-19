package com.perusudroid.myservicepro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.perusudroid.myservicepro.backgroundservice.FirstActivity;
import com.perusudroid.myservicepro.downloadservice.DownloadActivity;
import com.perusudroid.myservicepro.locationservice.LocationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void locationClicked(View view) {
        startActivity(new Intent(this, LocationActivity.class));
    }

    public void downloadClicked(View view) {
        startActivity(new Intent(this, DownloadActivity.class));
    }

    public void backgroundClicked(View view) {
        startActivity(new Intent(this, FirstActivity.class));
    }
}
